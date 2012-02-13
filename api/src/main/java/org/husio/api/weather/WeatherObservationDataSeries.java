package org.husio.api.weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Data series as required by client side in order to generate nice graphics.
 * This is just another way to organize weather information.
 * Multiple values in the same unit format. Timestamped. Several dimensions as specified.
 * Only the measurements requested.
 * 
 * This is a flexible structure that will enable chart generation and efficient wire transfers.
 * 
 * @author rafael
 *
 */
public class WeatherObservationDataSeries extends ArrayList<HashMap<String,Object>>{
    
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static WeatherObservationDataSeries createFrom(List<WeatherObservation> observations,List<WeatherObservationDataSeriesSpecification> seriesSpec){
	WeatherObservationDataSeries ret=new WeatherObservationDataSeries();
	
	// For each weather observation collected
	for (WeatherObservation o:observations){
	    
	    // We assume all observations contain the same measurements and at least one field will be in the seires.
	    HashMap<String,Object> dsEntry=new HashMap<String,Object>();
	    dsEntry.put("date", o.getTimestamp());
	    dsEntry.put("duration", o.getDuration().floatValue(WeatherUnits.SECOND));
	    ret.add(dsEntry);
	    
	    // For each measurement in the observation
	    for(ObservedWeatherMeasure m: o.getMeasures()){
		
		// For each specified dataseries to include
		for(WeatherObservationDataSeriesSpecification spec:seriesSpec){
		    
		    //bingo! this measurement must go in!
		    if(m.getKey().equals(spec.getKey())){
			
			// we add the required entry in the data series in the requested unit
			dsEntry.put(spec.getFieldName(), m.getMeasure().floatValue(spec.getUnit()));
		    }
		    
		}
	    }
	}
	return ret;
    }
}
