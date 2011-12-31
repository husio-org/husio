package org.husio.weather;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherApplication {
    
    private static final Logger log = LoggerFactory.getLogger(WeatherApplication.class);
    
    private static final Properties configuration = composeConfigurationProperties();
	    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
	PropertyConfigurator.configure(configuration);
	log.info("Husio home weather station starting...");
    }
    

    /**
     * Composes the configuration properties by merging User Properties with System Properties and Default Properties.
     * Properties are located in $HOME, /etc and the claspath respectively. User Configuration take precedence.
     * @return
     * @throws IOException
     */
    private static Properties composeConfigurationProperties(){
	Properties ret;
	try {
	    ret=readJarProperties("org/husio/config/husio.properties");
	} catch (IOException e) {
	    throw new Error("Could not build configuration",e);   
	}
	return ret;
    }

    /**
     * Reads a property file from the classpath.
     * @param jarPath
     * @return
     * @throws IOException
     */
    private static Properties readJarProperties(String jarPath) throws IOException{
	Properties ret=new Properties();
	ret.load(ClassLoader.getSystemClassLoader().getResourceAsStream(jarPath));
	return ret;
    }
}
