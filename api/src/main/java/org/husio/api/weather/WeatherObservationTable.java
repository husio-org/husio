package org.husio.api.weather;

import java.io.Serializable;
import java.util.Hashtable;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Dimension;

public class WeatherObservationTable extends Hashtable<String, ObservedWeatherMeasure<? extends Quantity>> implements Serializable {
    private static final long serialVersionUID = 1L;

    public WeatherObservationTable(WeatherObservationList measures) {
	for (ObservedWeatherMeasure<? extends Quantity> m : measures) {
	    if (m.isValidMetric())
		this.put(m.getKey(), m);
	}
    }


    /**
     * Works out the key and retrieves a measurement.
     * 
     * @param d
     *            the dimension
     * @param e
     *            the environment
     * @param t
     *            the type
     * @return
     */
    public ObservedWeatherMeasure<? extends Quantity> get(Dimension d, ObservedWeatherMeasure.ENVIRONMENT e, ObservedWeatherMeasure.TYPE t) {
	String key = ObservedWeatherMeasure.getKey(d, e, t);
	return super.get(key);
    }

}
