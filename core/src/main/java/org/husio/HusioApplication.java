package org.husio;

import java.io.IOException;

import org.husio.api.weather.WeatherStation;
import org.husio.eventbus.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;

public class HusioApplication {

    private static final Logger log = LoggerFactory.getLogger(HusioApplication.class);
    private static Tracer tracer;
    private static WeatherStation weatherStation;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
	try{
	    
	Configuration.setupLogSystem();
	log.info("Husio home automation system starting...");
	
	// create the tracing module
	tracer=new Tracer();
	
	// create the weather station
	String stationDriver=System.getProperty("org.husio.weather.stationDriver");
	assert stationDriver!=null: "org.husio.weather.stationDriver not configuted";
	weatherStation=(WeatherStation) Class.forName(Configuration.getProperty("org.husio.weather.stationDriver")).newInstance();
	weatherStation.start();
	}
	catch(Exception e){
	    log.error("Error while starting Husio",e);
	}
    }
}
