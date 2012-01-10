package org.husio.api.weather.evt;

import org.husio.api.weather.WeatherMeasureCollection;
import org.husio.api.weather.WeatherStation;

/**
 * Simple wrapper event to be published when weather has been collected.
 * 
 * @author rafael
 *
 */
public class WeatherInformationCollectedEvent {
    private WeatherMeasureCollection weatherMeasureCollection;
    
    private WeatherStation weatherStation;
    
    public WeatherInformationCollectedEvent(WeatherStation st, WeatherMeasureCollection wms){
	this.weatherStation=st;
	this.weatherMeasureCollection=wms;
    }

    public void setWeatherMeasureCollection(WeatherMeasureCollection weatherMeasureCollection) {
	this.weatherMeasureCollection = weatherMeasureCollection;
    }

    public WeatherMeasureCollection getWeatherMeasureCollection() {
	return weatherMeasureCollection;
    }

    public void setWeatherStation(WeatherStation weatherStation) {
	this.weatherStation = weatherStation;
    }

    public WeatherStation getWeatherStation() {
	return weatherStation;
    }

}
