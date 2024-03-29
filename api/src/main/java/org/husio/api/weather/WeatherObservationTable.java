package org.husio.api.weather;

import java.io.Serializable;
import java.util.Hashtable;

import javax.measure.quantity.Quantity;

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
    public ObservedWeatherMeasure<? extends Quantity> get(ObservedWeatherMeasure.MEASUREMENT_TYPE mt, ObservedWeatherMeasure.ENVIRONMENT e, ObservedWeatherMeasure.VARIANT t) {
	String key = ObservedWeatherMeasure.getKey(mt, e, t);
	return super.get(key);
    }

}
