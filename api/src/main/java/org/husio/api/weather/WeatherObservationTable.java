package org.husio.api.weather;

import java.util.Hashtable;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;

public class WeatherObservationTable  extends Hashtable<String,ObservedWeatherMeasure<? extends Quantity>>{
    private static final long serialVersionUID = 1L;
    
    /**
     * Adds a measurement to the table
     * @param m
     */
    public void add(ObservedWeatherMeasure<? extends Quantity> m){
	this.put(m.getKey(),m);
    }
    
    /**
     * Works out the key and retrieves a measurement.
     * @param d the dimension
     * @param e the environment
     * @param t the type
     * @return
     */
    public ObservedWeatherMeasure<? extends Quantity> get(Dimension d, ObservedWeatherMeasure.ENVIRONMENT e, ObservedWeatherMeasure.TYPE t){
	String key=ObservedWeatherMeasure.getKey(d,e,t);
	return super.get(key);
    }

}