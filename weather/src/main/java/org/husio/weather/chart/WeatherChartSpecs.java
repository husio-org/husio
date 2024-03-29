package org.husio.weather.chart;

import java.util.ArrayList;
import java.util.List;

import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.WeatherObservationDataSeriesSpecification;
import org.husio.api.weather.WeatherUnits;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.MEASUREMENT_TYPE;
import org.husio.api.weather.ObservedWeatherMeasure.VARIANT;

/**
 * Definition of data Series specifications used in charts
 * @author rafael
 *
 */
//TODO: this should use user preferences for units.
public class WeatherChartSpecs {
    
    /**
     * Just one simple chart, outdoor temperature
     * @return
     */
    public static List<WeatherObservationDataSeriesSpecification> outdorHistory(){
	List<WeatherObservationDataSeriesSpecification> ret=new ArrayList<WeatherObservationDataSeriesSpecification>();
	String key;
	
	// outdoor temperature
	key=ObservedWeatherMeasure.getKey(MEASUREMENT_TYPE.TEMPERATURE, ENVIRONMENT.OUTDOOR, VARIANT.DISCRETE);	
	ret.add(new WeatherObservationDataSeriesSpecification("temp", key, WeatherUnits.CELSIUS));

	// outdoor pressure
	key=ObservedWeatherMeasure.getKey(MEASUREMENT_TYPE.PRESSURE, ENVIRONMENT.OUTDOOR, VARIANT.ABSOLUTE);	
	ret.add(new WeatherObservationDataSeriesSpecification("press", key, WeatherUnits.HECTO_PASCAL));
	
	// outdoor rain
	key=ObservedWeatherMeasure.getKey(MEASUREMENT_TYPE.RAINFALL, ENVIRONMENT.OUTDOOR, VARIANT.AGREGATED);	
	ret.add(new WeatherObservationDataSeriesSpecification("rain", key, WeatherUnits.MM_RAINFALL));

	// wind
	key=ObservedWeatherMeasure.getKey(MEASUREMENT_TYPE.WIND_SPEED, ENVIRONMENT.OUTDOOR, VARIANT.AVERAGE);	
	ret.add(new WeatherObservationDataSeriesSpecification("wind", key, WeatherUnits.KNOT));

	// wind
	key=ObservedWeatherMeasure.getKey(MEASUREMENT_TYPE.WIND_SPEED, ENVIRONMENT.OUTDOOR, VARIANT.GUST);	
	ret.add(new WeatherObservationDataSeriesSpecification("windGust", key, WeatherUnits.KNOT));
	
	return ret;
    }


}
