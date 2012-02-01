package org.husio.weather.station.wh1080;

import java.util.Timer;
import java.util.TimerTask;

import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbIrp;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;

import org.husio.Configuration;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherStation;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.husio.usb.UsbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;

/**
 * 
 * This is the WH1080 weather station interface. Handles data mapping,
 * representation, etc.
 * 
 * @author rafael
 * 
 */
public class WH1080Driver implements WeatherStation {

    private static final String FORCE_CLAIM_CONFIG_OPTION = "org.husio.weather.station.wh1080.WH1080Driver.usbForceClaim";
    private static final String POLL_INTERVAL_CONFIG_OPTION = "org.husio.weather.station.wh1080.WH1080Driver.pollIntervalMinutes";

    private static final byte WRITE_COMMAND = (byte) 0xA0;
    private static final byte END_MARK = (byte) 0x20;
    private static final byte READ_COMMAND = (byte) 0xA1;
    private static final byte WRITE_COMMAND_WORD = (byte) 0xA2;

    private STATUS status = STATUS.STOPPED;

    /**
     * USB vendor id for locating the station.
     */
    public static final short USB_VENDOR_ID = (short) 0x1941;

    /**
     * USB product id for locating the station.
     */
    public static final short USB_PRODUCT_ID = (short) 0x8021;

    private static final Logger log = LoggerFactory.getLogger(WH1080Driver.class);

    private UsbDevice usbDevice;
    private UsbPipe usbPipe;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpoint;

    private FixedMemoryBlock fmb;

    private Timer timer;
    private WeatherStation station;

    /**
     * Will find the station in the USB bus.
     * 
     * @throws Exception
     */
    public WH1080Driver() throws Exception {
	log.debug("Starting WH1080 Driver");
	usbDevice = UsbUtils.findDevice(WH1080Driver.USB_VENDOR_ID, WH1080Driver.USB_PRODUCT_ID);
	usbInterface = (UsbInterface) usbDevice.getActiveUsbConfiguration().getUsbInterfaces().get(0);
	usbEndpoint = (UsbEndpoint) usbInterface.getUsbEndpoints().get(0);
	usbPipe = usbEndpoint.getUsbPipe();
	station = this;
	timer = new Timer("WH1080");
    }

    /**
     * starts and connects to the USB device
     */
    public synchronized void start() throws Exception {
	log.info("Starting the WH1080 Driver");

	try {
	    usbInterface.claim(new InterfacePolicy());
	    usbPipe.open();
	    int delay = Integer.parseInt(Configuration.getProperty(POLL_INTERVAL_CONFIG_OPTION));
	    log.info("Will refresh weather information every: " + delay + " minutes");
	    this.setSamplingTimeMinutes(UsbUtils.intToUnsignedByte(delay));
	    long delayMs= delay * 60 * 1000;
	    // we ensure that at least one entry is fully complete, give a five seconds margin.
	    timer.scheduleAtFixedRate(new WeatherPublisherTask(), delayMs+5000,delayMs);
	    this.status = STATUS.RUNNING;

	} catch (UsbException e) {
	    this.status = STATUS.ERROR;
	    log.error("Could not connect with WH1080, is it been used?", e);
	    if (usbPipe.isOpen())
		usbPipe.close();
	    usbInterface.release();
	}
    }

    @Override
    public synchronized void stop() throws Exception {
	this.status = STATUS.STOPPED;
	timer.cancel();
	this.usbPipe.abortAllSubmissions();
	if (this.usbPipe.isOpen())
	    this.usbPipe.close();
	this.usbInterface.release();
	log.info("WH1080 Driver Stopped");
    }

    @Override
    public synchronized STATUS getStatus() {
	return status;
    }

    /**
     * Reads 32 bytes of data from the given address, and writes them to the
     * given buffer, at the given offset.
     * 
     * @param address
     *            the address of the EEPROM memory to read from
     * @param dataButter
     *            the buffer to write the bytes to
     * @param offset
     *            where to place the 32 read bytes at.
     * @throws Exception
     *             in case something goes wrong at the USB protocol level.
     */
     void readAddress(int address, byte[] dataBuffer, int offset) throws Exception {

	// prepare a control packet to request the read

	byte[] command = { READ_COMMAND, // command
		(byte) (address / 256), // address high
		(byte) (address % 256), // address low
		END_MARK, // end mark
		READ_COMMAND, // command
		0, // address high
		0, // address low
		END_MARK, // end mark
	};

	this.writeCommand(command);

	// read 32 bytes in 4x8 bytes packets from the station
	for (int i = 0; i < 4; i++) this.readReplyPacket(dataBuffer, offset + 8 * i);

	log.trace("Read Address " + UsbUtil.toHexString(address) + ":" + UsbUtils.toHexString(dataBuffer, offset, 32));
    }

    /**
     * Writes a command packet to the station
     * 
     * @param command
     * @throws Exception
     */
    void writeCommand(byte[] command) throws Exception {
	UsbControlIrp cirp = this.usbDevice.createUsbControlIrp((byte) (UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_CLASS | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE),
		UsbConst.REQUEST_SET_CONFIGURATION, (short) (UsbConst.REQUESTTYPE_RECIPIENT_ENDPOINT << 8), // value
		(short) 0);

	cirp.setData(command);

	// send the command
	usbDevice.syncSubmit(cirp);
    }

    /**
     * reads a 8 byte reply packet from the station.
     * 
     * @param dataBuffer
     * @param offset
     */
    void readReplyPacket(byte[] dataBuffer, int offset) throws Exception{
	UsbIrp irp = usbPipe.createUsbIrp();
	irp.setData(dataBuffer);
	irp.setLength(8);
	irp.setOffset(offset);
	usbPipe.syncSubmit(irp);
	assert irp.isComplete() : "Irp is not complete!";
    }

    /**
     * Writes a byte to the station
     * 
     * @param address
     * @param data
     */
     void writeByteAddress(int address, byte data) throws Exception {

	// prepare a control packet to request the read

	byte[] command = { WRITE_COMMAND_WORD, // command
		(byte) (address / 256), // address high
		(byte) (address % 256), // address low
		END_MARK, // end mark
		WRITE_COMMAND_WORD, // command
		data, 0, END_MARK, // end mark
	};

	this.writeCommand(command);
	
	byte[] reply=new byte[8];
	this.readReplyPacket(reply, 0);
	for (byte b: reply){
	    assert b==0x65: "Invalid WH1080 Reply";
	}
	
	log.debug("Command was processed OK");

    }
     
    public synchronized void setSamplingTimeMinutes(byte num) throws Exception{
	log.debug("Settign WH1080 sampling time to minutes: " + num + " minutes");
	this.writeByteAddress(FixedMemoryBlock.SAMPLING_INTERVAL_SETTING_ADDRESS, num);
    }

    public synchronized HistoryDataEntry readHistoryDataEntry(int address) throws Exception {
	log.debug("Reading history data entry at address: " + UsbUtil.toHexString(address));
	return new HistoryDataEntry(address, this);
    }

    public synchronized FixedMemoryBlock readFixedMemoryBlock() throws Exception {
	log.debug("Reading WH1080 Status FMB");
	return this.fmb = new FixedMemoryBlock(this);
    }

    public WeatherObservation readLastDataEntry() throws Exception {
	this.readFixedMemoryBlock();
	return this.readHistoryDataEntry(fmb.lastCompletedEntryAddress()).getObservation();
    }

    public FixedMemoryBlock fmb() {
	return fmb;
    }

    /**
     * Private inner task that will publish the weather to the rest of the husio
     * components.
     * 
     * @author rafael
     * 
     */
    private class WeatherPublisherTask extends TimerTask {

	/**
	 * publishes the weather events as required.
	 */
	@Override
	public void run() {
	    try {
		EventBusService.publish(new WeatherObservationEvent(station, readLastDataEntry()));
	    } catch (Exception e) {
		log.error("Could not read weather from station.", e);
	    }
	}

    }

    private class InterfacePolicy implements UsbInterfacePolicy {
	public boolean forceClaim(UsbInterface usbInterface) {
	    return Configuration.getBooleanProperty(FORCE_CLAIM_CONFIG_OPTION);
	}
    }

    /**
     * Report that this module is a weather station, and only one is allowed in
     * the system.
     */
    @Override
    public MODULE_TYPE getModuleType() {
	return MODULE_TYPE.WEATHER_STATION;
    }

}
