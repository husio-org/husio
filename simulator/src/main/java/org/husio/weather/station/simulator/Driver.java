package org.husio.weather.station.simulator;

import java.util.Timer;
import java.util.TimerTask;

import org.husio.Configuration;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherStation;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;

/**
 * A simple weather station simulator that allows development without an station
 * attached to the computer.
 * 
 * @author rafael
 * 
 */
public class Driver implements WeatherStation {

    static final String POLL_INTERVAL_CONFIG_OPTION = "org.husio.weather.station.simulator.Driver.PollIntervalSeconds";

    private static final Logger log = LoggerFactory.getLogger(Driver.class);

    private Timer timer;
    private STATUS status = STATUS.STOPPED;
    private WeatherStation station;
    private double time=0;

    public Driver() {
	station = this;
	timer = new Timer("Simulator");
	log.info("Initializing the Weather simulator");
    }

    public MODULE_TYPE getModuleType() {
	return MODULE_TYPE.WEATHER_STATION;
    }

    public void start() throws Exception {
	log.info("Starting the weather simulator");
	long delay = Integer.parseInt(Configuration.getProperty(POLL_INTERVAL_CONFIG_OPTION));
	log.info("Will refresh weather information every: " + delay + " seconds");
	timer.scheduleAtFixedRate(new WeatherPublisherTask(), 0, delay * 1000);
	this.status = STATUS.RUNNING;
    }

    public void stop() throws Exception {
	log.info("Stopping the weather station simulator");
	this.status = STATUS.STOPPED;
	timer.cancel();
    }

    public STATUS getStatus() {
	return status;
    }

    /**
     * Generated a simulated observation. TODO: it would be nice if the data
     * simulated was similar to real. for the time been lets generate sin
     * curvers
     * 
     * @return
     */
    public WeatherObservation generateSimulatedWeather() {
	return new SimulatedWeatherObservation(time+=0.1);
    }

    /**
     * This will publish, when required, the weather event.
     * 
     * @author rafael
     * 
     */
    class WeatherPublisherTask extends TimerTask {

	public void run() {
	    EventBusService.publish(new WeatherObservationEvent(station, generateSimulatedWeather()));
	}

    }

}
