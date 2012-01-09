package org.husio;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HusioApplication {
    
    private static final Logger log = LoggerFactory.getLogger(HusioApplication.class);
    	    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
	Configuration.setupLogSystem();
	log.info("Husio home automation system starting...");
    }


}
