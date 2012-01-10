package org.husio.api.weather;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.unit.Dimension;
import javax.measure.unit.Unit;

/**
 * 
 * 
 * 
 * @author rafael
 * 
 */
public class CollectedWeatherMeasure<T extends Quantity> {

    /**
     * Where the metric took place.
     * 
     * @author rafael
     * 
     */
    public static enum ENVIRONMENT {
	INDOOR, OUTDOOR, CUSTOM
    };

    /**
     * Type of measurement
     */

    /**
     * Stations will provide a series of metrics for a given measurement period
     * some of those metrics will result as computing many individual
     * measurements during the measurement period: for example, AVG wind, or MAX
     * wind, etc.
     * 
     * In some other cases the measurement is absolute: such as rainfall during
     * that period or simply, the metric remains constant (or is likely to vary
     * very little) or it is considered constant during the period: temperature,
     * pressure, for example. For those cases an DISCRETE measurement is
     * provided that it is supposed to remain constant during the whole period.
     * 
     * (such as maximum
     * 
     */
    public static enum TYPE {
	DISCRETE, MAXIMUM, AVERAGE, NIMIMUM, GUST
    }

    private boolean isValidMetric = true;

    private ENVIRONMENT environment;

    private TYPE type;

    private Measure<T> measure;
    
    public CollectedWeatherMeasure() {

    }

    public CollectedWeatherMeasure(ENVIRONMENT e, TYPE t) {
	this.environment = e;
	this.type = t;
    }

    public CollectedWeatherMeasure(ENVIRONMENT e, TYPE t, Measure<T> m) {
	this.environment = e;
	this.type = t;
	this.measure = m;
    }

    public void setValidMetric(boolean isValidMetric) {
	this.isValidMetric = isValidMetric;
    }

    /**
     * The station has reported the measurement to be invalid, and it is not
     * provided.
     * 
     * @return
     */
    public boolean isValidMetric() {
	return this.isValidMetric;
    }

    public void setMeasure(Measure<T> measure) {
	this.measure = measure;
    }

    /**
     * The measurement made, it includes the unit in which it was measured and
     * it can easily be presented in different units/systems as for JRS 275
     * 
     * @return
     */
    public Measure<? extends Quantity> getMeasure() {
	return measure;
    }

    public void setEnvironment(ENVIRONMENT environment) {
	this.environment = environment;
    }

    public ENVIRONMENT getEnvironment() {
	return environment;
    }

    public void setType(TYPE type) {
	this.type = type;
    }

    public TYPE getType() {
	return type;
    }

    /**
     * Returns the canonycal name of the type used for the measure collection.
     * That will be: Temperature, Pressure, etc.
     * 
     * @return
     * @throws Exception
     */
    public String getDimensionName() {
	Dimension d=measure.getUnit().getDimension();
	if(d.equals(WeatherUnits.CELSIUS.getDimension())) return "Temperature";
	else if(d.equals(WeatherUnits.HECTO_PASCAL.getDimension())) return "Pressure";
	else if(d.equals(WeatherUnits.METERS_PER_SECOND.getDimension())) return "Velocity";
	else if(d.equals(WeatherUnits.SECOND.getDimension())) return "Duration";
	else if(d.equals(WeatherUnits.PERCENT_WATER.getDimension())) return "Humidity";
	return measure.getUnit().getDimension().toString();
    }

    public String toString() {
	String me = this.isValidMetric ? measure.toSI().toString() : "N/A";
	String ret = "";
	ret = this.getDimensionName().toUpperCase() + ":" + environment.toString() + ":" + type.toString() + ":" + me;
	return ret;
    }

}