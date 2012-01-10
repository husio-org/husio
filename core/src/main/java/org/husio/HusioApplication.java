package org.husio;

import java.io.IOException;

import org.husio.api.weather.WeatherStation;
import org.husio.eventbus.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;

/**
 * Main Application Class.
 * 
 * Instantiates the configured components, registers shutdown hooks, and handles application shutdown.
 * 
 * @author rafael
 *
 */
public class HusioApplication {

    private static final Logger log = LoggerFactory.getLogger(HusioApplication.class);
    private static Tracer tracer;
    private static WeatherStation weatherStation;
    private static String[] commandLineArgs;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
	new HusioApplication().launchHusioApplication(args);
    }

    /**
     * Creates and registers configured components
     * Saves components as static variables.
     * @param args
     */
    private void launchHusioApplication(String[] args) {
	try {    
	    // Save command line args in case any module is interested.
	    commandLineArgs=args;
	    
	    // Setup the log system and say hi
	    Configuration.setupLogSystem();
	    log.info("Husio home automation system starting...");

	    // Register a shut-down hook
	    Runtime.getRuntime().addShutdownHook(new ShutdownHook());

	    // create the tracing module
	    tracer = new Tracer();

	    // create the weather station
	    String stationDriver = System.getProperty("org.husio.weather.stationDriver");
	    assert stationDriver != null : "org.husio.weather.stationDriver not configuted";
	    weatherStation = (WeatherStation) Class.forName(Configuration.getProperty("org.husio.weather.stationDriver")).newInstance();
	    weatherStation.start();
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
		weatherStation.stop();
		log.info("Husio shutdown OK");
	    } catch (Exception e) {
		log.warn("Could not shut down Husio cleanly",e);
	    }

	}

    }

}
