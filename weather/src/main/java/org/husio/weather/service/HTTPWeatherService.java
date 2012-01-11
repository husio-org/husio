package org.husio.weather.service;

import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.evt.WeatherObservationEvent;

/**
 * Helper Base Class for HTTP Weather Community Services
 * @author rafael
 *
 */
public abstract class HTTPWeatherService {
    
    /**
     * Helper method to add a measurement to the query string, its formatted as float.
     * @param params
     * @param weather
     * @param param
     * @param u
     * @param e
     * @param t
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void addMeasurement(List<NameValuePair> params, WeatherObservationEvent weather, String param, Unit u, ObservedWeatherMeasure.ENVIRONMENT e, ObservedWeatherMeasure.TYPE t ){
	String value=this.getUnkownValue();
	// Get the mesure we are after
	ObservedWeatherMeasure<? extends Quantity> measure=weather.getWeatherObservation().getMeasures().get(u.getDimension(), e, t);
	// If the measure exists and it is valid, add it in the required unit, and as float
	if(measure!=null){
	    if(measure.isValidMetric()){
		float v=measure.getMeasure().floatValue(u);
		value=Float.toString(v);
	    }
	}
	if(value!=null) params.add(new BasicNameValuePair(param, value));
    }
    
    /**
     * What to send for undefined/invalid params. null will not add the parameters at all.
     * @return
     */
    public abstract String getUnkownValue();

}
