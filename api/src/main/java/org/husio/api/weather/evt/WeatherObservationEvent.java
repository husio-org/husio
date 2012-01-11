package org.husio.api.weather.evt;

import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherStation;

/**
 * Simple wrapper event to be published when weather has been collected.
 * 
 * @author rafael
 *
 */
public class WeatherObservationEvent {
    private WeatherObservation weatherObservation;
    
    private WeatherStation weatherStation;
    
    public WeatherObservationEvent(WeatherStation st, WeatherObservation wms){
	this.weatherStation=st;
	this.weatherObservation=wms;
    }

    public void setWeatherObservation(WeatherObservation weatherObservation) {
	this.weatherObservation = weatherObservation;
    }

    public WeatherObservation getWeatherObservation() {
	return weatherObservation;
    }

    public void setWeatherStation(WeatherStation weatherStation) {
	this.weatherStation = weatherStation;
    }

    public WeatherStation getWeatherStation() {
	return weatherStation;
    }

}
