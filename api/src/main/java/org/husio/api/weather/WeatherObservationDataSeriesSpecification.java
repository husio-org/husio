package org.husio.api.weather;

import javax.measure.unit.Unit;

/**
 * Weather data series store multiple observations with the same unit format. This
 * class represents the specification of a data series.
 * @author rafael
 *
 */
public class WeatherObservationDataSeriesSpecification {
    
    // field name to use in the data series
    private String fieldName; 
    
    // The key used for the observations
    private String key;
    
    // The unit desired as representation.
    @SuppressWarnings("rawtypes")
    private Unit unit;
    
    public WeatherObservationDataSeriesSpecification(String fieldName, String key, Unit unit){
	this.key=key;
	this.unit=unit;
	this.fieldName=fieldName;
    }
    
    public void setKey(String key) {
	this.key = key;
    }
    
    public String getKey() {
	return key;
    }

    public void setUnit(Unit unit) {
	this.unit = unit;
    }

    public Unit getUnit() {
	return unit;
    }

    public void setFieldName(String fieldName) {
	this.fieldName = fieldName;
    }

    public String getFieldName() {
	return fieldName;
    }

}
