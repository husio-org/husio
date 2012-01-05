package org.husio.test.wh1080;

import javax.usb.UsbDevice;

import org.husio.Configuration;
import org.husio.usb.UsbUtils;
import org.husio.weather.station.wh1080.WH1080;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class WH1080Test {
    
    private static final Logger log = LoggerFactory.getLogger(WH1080Test.class);

    @BeforeTest
    public void init() {
	Configuration.setupLogSystem();
    }
    
    @Test
    public void findStationDevice() throws Exception{
	log.debug("Finding Station WH1080 in virtual USB device hub");
	UsbDevice device=UsbUtils.findDevice(WH1080.USB_VENDOR_ID, WH1080.USB_PRODUCT_ID);
	if(device!=null) log.debug("The device was found!!");
	else log.debug("The device was not found");
    }
    
}
