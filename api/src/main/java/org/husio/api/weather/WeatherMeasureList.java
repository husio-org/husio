package org.husio.api.weather;

import java.util.List;

import javax.measure.quantity.Quantity;

/**
 * Interface that represent a list of generic weather measurements. Its provided to enhance code
 * readability.
 * @author rafael
 *
 */
public interface WeatherMeasureList extends List<CollectedWeatherMeasure<? extends Quantity>>{
    
}