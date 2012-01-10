package org.husio;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/**
 * Manages the husio system configuration. Composes configuration files into a single properties object.
 * Responsible also for initializing the log system, which is used from testing also.
 * @author rafael
 *
 */
public class Configuration {

    private static final Properties configuration = composeConfigurationProperties();
    
    public static void setupLogSystem(){
	PropertyConfigurator.configure(configuration);
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
    
    public static String getProperty(String p){
	return Configuration.configuration.getProperty(p);
    }
    
}
