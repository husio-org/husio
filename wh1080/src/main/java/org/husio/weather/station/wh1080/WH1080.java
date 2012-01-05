package org.husio.weather.station.wh1080;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WH1080 {
    
    public static final short USB_VENDOR_ID=(short) 0x1941;
    
    public static final short USB_PRODUCT_ID=(short) 0x8021;

    
    private static final Logger log = LoggerFactory.getLogger(WH1080.class);
    
    public WH1080(){
	log.debug("Starting WH1080 Driver");
    }


}
