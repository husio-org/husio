package org.husio;

import java.io.File;
import java.io.FileReader;
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
	Properties user, system, def;
	try {
	    // Read the default properties shipped in the jar
	    def=readJarProperties("org/husio/config/husio.properties");
	    // Now read the system properties, set the previous as defaults.
	    system=readFileProperties(new File("/etc","husio.properties"),def);
	    // Now read the user porperties and set the previous as default
	    user=readFileProperties(new File(System.getProperty("user.home"),".husio.properties"),system);
	} catch (IOException e) {
	    throw new Error("Could not build configuration",e);   
	}
	return user;
    }

    /**
     * Reads a property file from the classpath.
     * @param jarPath
     * @return
     * @throws IOException
     */
    private static Properties readJarProperties(String jarPath) throws IOException{
	Properties ret= new Properties();
	ret.load(ClassLoader.getSystemClassLoader().getResourceAsStream(jarPath));
	return ret;
    }
    
    private static Properties readFileProperties(File f, Properties defaultProperties) throws IOException{
	Properties ret=defaultProperties!=null? new Properties(defaultProperties): new Properties();
	if(f.canRead()){
	    ret.load(new FileReader(f));
	}
	return ret;
    }
    
    /**
     * Returns a configuration property
     * @param p
     * @return
     */
    public static String getProperty(String p){
	return Configuration.configuration.getProperty(p);
    }
    
    /**
     * Returns a property as boolean.
     * @param p
     * @return
     */
    public static boolean getBooleanProperty(String p){
	return Boolean.getBoolean(getProperty(p));
    }
    
    /**
     * Returns a property as int.
     * @param p
     * @return
     */
    public static int getIntProperty(String p){
	return Integer.parseInt(getProperty(p));
    }
    
}
