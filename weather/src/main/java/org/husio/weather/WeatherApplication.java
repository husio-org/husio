package org.husio.weather;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.husio.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherApplication {
    
    private static final Logger log = LoggerFactory.getLogger(WeatherApplication.class);
    	    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
	Configuration.setupLogSystem();
	log.info("Husio home weather station starting...");
    }


}
