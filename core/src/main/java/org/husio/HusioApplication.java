package org.husio;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.husio.api.Initializable;
import org.husio.api.Module;
import org.husio.api.Singleton;
import org.husio.api.Singleton.MODULE_TYPE;
import org.husio.api.weather.WeatherCommunityService;
import org.husio.api.weather.WeatherStation;
import org.husio.eventbus.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Main Application Class.
 * 
 * Instantiates the configured components, registers shutdown hooks, and handles
 * application shutdown.
 * 
 * @author rafael
 * 
 */
@SuppressWarnings("unused")
// They are indeed in use by subscribing the event bus!
public class HusioApplication {

    /**
     * This is how we are going to search for modules in the configuration file,
     * this properties will be searched for single modules, or the same property
     * name terminated by List (for example serviceDriverList) with a separated
     * list of modules
     */
    private static final String[] MODULE_CONFIG_PARAMS = { "org.husio.weather.module","org.husio.web.serverDriver", "org.husio.weather.stationDriver", "org.husio.weather.serviceDriver", "org.husio.optionalModule" };
    
    private static final String DB_DRIVER_CONFIG="org.h2.Driver";
    private static final String DB_STORAGE_PATH="org.husio.db.storagePath";
    private static final String DB_JDBC_PROTOCOL="jdbc:h2:";
    
    private static final Logger log = LoggerFactory.getLogger(HusioApplication.class);
    private static String[] commandLineArgs;

    private static List<Module> modules = new ArrayList<Module>();
    private static Hashtable<MODULE_TYPE, Singleton> singletons = new Hashtable<MODULE_TYPE, Singleton>();
    
    private static HusioApplication singleton;
    
    public HusioApplication(){
	this.singleton=this;
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
	new HusioApplication().launchHusioApplication(args);
    }

    /**
     * Creates and registers configured components Saves components as static
     * variables.
     * 
     * @param args
     */
    private void launchHusioApplication(String[] args) {
	try {
	    List<String> loadableModules = new ArrayList<String>();

	    // Save command line args in case any module is interested.
	    commandLineArgs = args;

	    // Setup the log system and say hi
	    Configuration.setupLogSystem();
	    log.info("Husio home automation system starting...");

	    // Register a shut-down hook
	    Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	    
	    // Load the database driver
	    Class.forName(DB_DRIVER_CONFIG);

	    // Buildup the loadable module list, a list of the classes to load
	    for (String property : MODULE_CONFIG_PARAMS) {
		// Check if the property exists as a List
		String listProperty = property + "List";
		String driverClassList = Configuration.getProperty(listProperty);
		if (driverClassList != null) {
		    String[] driverClassListItems = driverClassList.split(",");
		    for (String driverClassItem : driverClassListItems)
			loadableModules.add(driverClassItem);
		} else {
		    // Check if the property exists as a single item
		    // We ignore the sigle item configuration if there was a
		    // list
		    String driverClass = Configuration.getProperty(property);
		    if (driverClass != null)
			loadableModules.add(driverClass);
		}

	    }

	    // Instantiate the modules and ensure that Singletons are only one
	    // of the kind
	    for (String driverClass : loadableModules) {
		driverClass = driverClass.trim();
		if (!driverClass.isEmpty()) {
		    log.info("Loading module " + driverClass);
		    Module m = (Module) Class.forName(driverClass.trim()).newInstance();
		    modules.add(m);
		    if (m instanceof Singleton) {
			Singleton s = (Singleton) m;
			MODULE_TYPE t = s.getModuleType();
			assert !singletons.contains(t) : "There can only be module of type:" + t;
			singletons.put(t, s);
		    }
		}
	    }

	    // now start all the initializable modules
	    log.info("Initializing modules...");
	    for (Module m : modules) {
		if (m instanceof Initializable) {
		    ((Initializable) m).start();
		}
	    }

	    log.info("Husio home automation system started ok");

	} catch (Exception e) {
	    log.error("Error while starting Husio", e);
	}
    }

    /**
     * Helper Thread class that will be executed on JVM termination ( CTRL+C or
     * Shutdown). It simply stops the components.
     * 
     * @author rafael
     * 
     */
    private class ShutdownHook extends Thread {
	@Override
	public void run() {
	    try {
		// now start all the initializable modules
		log.info("Stoping modules...");
		for (Module m : modules) {
		    if (m instanceof Initializable) {
			((Initializable) m).stop();
		    }
		}
		log.info("Husio shutdown OK");
	    } catch (Exception e) {
		log.warn("Could not shut down Husio cleanly", e);
	    }

	}

    }
    
    /**
     * Generates a connection string, also used during testing
     */
    public static String dbConnectionString(String filePath){
	return DB_JDBC_PROTOCOL+filePath;
    }
    
    /**
     * Builds the connection string according to user preferences.
     * @return
     */
    private String dbConnectionString(){
	return dbConnectionString(Configuration.getProperty(DB_STORAGE_PATH));
    }
    
    /**
     * Access to connections, we expect many modules to use connections if they need to store data.
     * @return
     * @throws SQLException
     */
    public static ConnectionSource getDbConnection() throws SQLException{
	return new JdbcConnectionSource(singleton.dbConnectionString(),"sa","");
    }
    
    

}
