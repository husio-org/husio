package org.husio.weather.station.wh1080;

import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbIrp;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;

import org.husio.usb.UsbUtils;
import org.husio.weather.api.WeatherStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WH1080 implements WeatherStation{
    
    public static final short USB_VENDOR_ID=(short) 0x1941;
    
    public static final short USB_PRODUCT_ID=(short) 0x8021;

    private static final Logger log = LoggerFactory.getLogger(WH1080.class);
    
    private UsbDevice usbDevice;
    private UsbPipe usbPipe;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpoint;
    private byte[] dataBuffer ;
 

    public WH1080() throws Exception{
	log.debug("Starting WH1080 Driver");
	usbDevice=UsbUtils.findDevice(WH1080.USB_VENDOR_ID, WH1080.USB_PRODUCT_ID);
	usbInterface=(UsbInterface) usbDevice.getActiveUsbConfiguration().getUsbInterfaces().get(0);
	usbEndpoint=(UsbEndpoint) usbInterface.getUsbEndpoints().get(0);
	usbPipe=usbEndpoint.getUsbPipe();
	
	// Init the buffer for communication
	dataBuffer=new byte[32];
    }
    
    public void start() throws Exception{
	log.debug("Starting the Weather Station Interface");

	try {
	    usbInterface.claim();
	    usbPipe.open();

	} catch (UsbException e) {
	    log.error("Could not connect with WH1080, is it been used?",e);
	    if(usbPipe.isOpen()) usbPipe.close();
	    usbInterface.release();
	}
    }

    public void readAddress(int address) throws Exception{

	// prepare a control packet to request the read
	
	byte[] command={
		(byte) 0xa1, //command
		(byte) (address/256), //address high
		(byte) (address%256), //address low
		(byte) 0x20, // end mark
		(byte) 0xa1, //command
		(byte) (address/256), //address high
		(byte) (address%256), //address low
		(byte) 0x20, // end mark
	};
		
	UsbControlIrp cirp=this.usbDevice.createUsbControlIrp(
		(byte) (UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_CLASS | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE ), 
		UsbConst.REQUEST_SET_CONFIGURATION, 
		(short) (UsbConst.REQUESTTYPE_RECIPIENT_ENDPOINT << 8), 	//value 
		(short) 0
	);
	
	cirp.setData(command);
	
	// send the command
	usbDevice.syncSubmit(cirp);
	
	// read 32 bytes in 4 8 bytes packets from the station
	for(int i=0;i<4;i++){
		UsbIrp irp= usbPipe.createUsbIrp();
		irp.setData(dataBuffer);
		irp.setLength(8);
		irp.setOffset(8*i);
		usbPipe.syncSubmit(irp);
		assert irp.isComplete():"Irp is not complete!";
	}
	
	log.debug("Read Address "+UsbUtil.toHexString(address)+": "+UsbUtil.toHexString(" 0x", dataBuffer));
    }

    @Override
    public void stop() throws Exception {
	if(this.usbPipe.isOpen()) this.usbPipe.close();
	this.usbInterface.release();
    }
    


}
