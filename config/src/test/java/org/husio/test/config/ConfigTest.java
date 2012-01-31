package org.husio.test.config;

import org.husio.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class ConfigTest {

    private static final Logger log = LoggerFactory.getLogger(ConfigTest.class);
    
    @Test
    public void testBooleanConfigOption(){
	log.debug("Testing boolean configuration options");
	String testProperty="org.husio.weather.station.wh1080.WH1080Driver.usbForceClaim";
	boolean t=Configuration.getBooleanProperty(testProperty);
	assert t: "Option not found or not true";
    }

}
