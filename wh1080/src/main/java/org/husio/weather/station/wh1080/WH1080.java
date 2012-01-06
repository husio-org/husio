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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WH1080 {
    
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
	
	log.debug("Claiming the USB Interface");
	this.connect();
	this.readConfig();
    }
    
    private void connect() throws Exception{
	try {
	    usbInterface.claim();
	    usbPipe.open();

	} catch (UsbException e) {
	    log.error("Could not connect with WH1080, is it been used?",e);
	    if(usbPipe.isOpen()) usbPipe.close();
	    usbInterface.release();
	}
    }

    private void readConfig() throws Exception{
	log.debug("Preparing command packet");
	
	int offset=0;
	
	byte[] command={
		(byte) 0xa1, //command
		(byte) (offset/256), //address high
		(byte) (offset%256), //address low
		(byte) 0x20, // end mark
		(byte) 0xa1, //command
		(byte) (offset/256), //address high
		(byte) (offset%256), //address low
		(byte) 0x20, // end mark
	};
		
	UsbControlIrp cirp=this.usbDevice.createUsbControlIrp(
		(byte) (UsbConst.REQUESTTYPE_DIRECTION_OUT | UsbConst.REQUESTTYPE_TYPE_CLASS | UsbConst.REQUESTTYPE_RECIPIENT_INTERFACE ), 
		UsbConst.REQUEST_SET_CONFIGURATION, 
		(short) (UsbConst.REQUESTTYPE_RECIPIENT_ENDPOINT << 8), 	//value 
		(short) 0
	);
	
	cirp.setData(command);
	
	log.debug("Sending command packet");

	usbDevice.syncSubmit(cirp);
	
	log.debug("Data is:"+UsbUtil.toHexString(" 0x", dataBuffer));
	UsbIrp irp= usbPipe.createUsbIrp();
	irp.setData(dataBuffer);
	usbPipe.syncSubmit(irp);
	log.debug("Got Packet");
	log.debug("Is complete"+irp.isComplete());
	log.debug("Data is:"+UsbUtil.toHexString(" 0x", dataBuffer));
    }
    


}
