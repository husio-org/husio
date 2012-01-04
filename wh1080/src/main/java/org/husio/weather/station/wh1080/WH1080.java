package org.husio.weather.station.wh1080;

import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WH1080 {
    
    private static final Logger log = LoggerFactory.getLogger(WH1080.class);
    
    public WH1080() throws SecurityException, UsbException{
	log.debug("Starting WH1080 Driver");
    }


}
