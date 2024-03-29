package org.husio.usb;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;
import javax.usb.util.UsbUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsbUtils {
    
    private static final Logger log = LoggerFactory.getLogger(UsbUtils.class);
    
    /**
     * Helps us find a given USB device given its vendor and product IDs.
     * @param vendor
     * @param product
     * @return
     * @throws Exception
     */
    public static UsbDevice findDevice(short vendor, short product) throws Exception{
	UsbServices services = UsbHostManager.getUsbServices();
	log.info("Searching for USB device "+UsbUtil.toHexString(vendor)+":"+UsbUtil.toHexString(product));
	return searchDeviceFrom(services.getRootUsbHub(),vendor,product);
    }
    
    /**
     * Recursive helper, looks for the requested device
     * @param usbDevice
     * @param vendor
     * @param product
     * @return
     * @throws Exception
     */
    private static UsbDevice searchDeviceFrom(UsbDevice usbDevice, short vendor, short product) throws  Exception {
	UsbHub usbHub = null;

	/* If this is not a UsbHub, just display device and return. */
	if (!usbDevice.isUsbHub()) {
	    // this is not a Hub, then check if it is the one we are looking for!
	    UsbDeviceDescriptor dd=usbDevice.getUsbDeviceDescriptor();
	    if(dd.idProduct()==product && dd.idVendor()==vendor) return usbDevice;
	    log.trace("Skiping found device "+UsbUtil.toHexString(dd.idVendor())+":"+UsbUtil.toHexString(dd.idProduct()));
	} else {
	    /* We know it's a hub, so cast it. */
	    usbHub = (UsbHub) usbDevice;
	    
	    List attachedUsbDevices = usbHub.getAttachedUsbDevices();
	    for (int i = 0; i < attachedUsbDevices.size(); i++) {
		UsbDevice device = (UsbDevice) attachedUsbDevices.get(i);
		UsbDevice found= searchDeviceFrom(device, vendor,product );
		if (found!=null) return found;
	    }	
	}
	return null;
    }


    public static String toHexString(byte[] data,int offset, int len){
	String ret="";
	for(int i=0;i<len;i++){
	    ret+=" "+UsbUtil.toHexString(data[offset+i]);
	}
	return ret;
    }
    
    /**
     * Converts and int into an unsigned byte
     * @param i
     * @return
     */
    public static byte intToUnsignedByte(int i){
	return (byte) (i&0xFF);
    }
    
}
