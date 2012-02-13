package org.husio.api.weather;

import java.io.Serializable;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

/**
 * 
 * 
 * 
 * @author rafael
 * 
 */
public class ObservedWeatherMeasure<T extends Quantity> implements Serializable{

    private static final long serialVersionUID = 1L;

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
    
    public static enum MEASUREMENT_TYPE {
	TEMPERATURE,PRESSURE,WIND_SPEED,WIND_DIRECTION,RAINFALL,HUMIDITY, DURATION
    }

    /**
     * The variant of measure that is been made: calculation applied, etc.
     */
    public static enum VARIANT {
	DISCRETE, AGREGATED, MAXIMUM, AVERAGE, NIMIMUM, GUST, ABSOLUTE, RELATIVE, DEW
    }

    private boolean isValidMetric = true;
    
    private MEASUREMENT_TYPE mtype;

    private ENVIRONMENT environment=ENVIRONMENT.OUTDOOR;

    private VARIANT variant=VARIANT.DISCRETE;

    private Measure<T> measure;
    
    public ObservedWeatherMeasure() {

    }
    
    public ObservedWeatherMeasure(MEASUREMENT_TYPE mt) {
	this.mtype=mt;
    }
    
    public ObservedWeatherMeasure(MEASUREMENT_TYPE mt, ENVIRONMENT e) {
	this.mtype=mt;
	this.environment = e;
    }

    public ObservedWeatherMeasure(MEASUREMENT_TYPE mt, ENVIRONMENT e, VARIANT v) {
	this.mtype=mt;
	this.environment = e;
	this.variant = v;
    }

    public ObservedWeatherMeasure(MEASUREMENT_TYPE mt, ENVIRONMENT e, VARIANT v, Measure<T> m) {
	this.mtype=mt;
	this.environment = e;
	this.variant = v;
	this.measure = m;
    }

    public void setMtype(MEASUREMENT_TYPE mtype) {
	this.mtype = mtype;
    }

    public MEASUREMENT_TYPE getMtype() {
	return mtype;
    }

    public void setVariant(VARIANT variant) {
	this.variant = variant;
    }

    public VARIANT getVariant() {
	return variant;
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

    public String toString() {
	String me = this.isValidMetric ? measure.toSI().toString() : "N/A";
	return me;
    }
    
    /**
     * Works out the key for a weather measurement.
     * @param d the dimension of the measurement
     * @param e the environment of the measurement
     * @param t the type of measurement
     * @return
     */
    public static String getKey(MEASUREMENT_TYPE mt, ENVIRONMENT e, VARIANT v){
	return e.toString() +"_"+mt.toString() +"_" + v.toString();
    }
    
    /**
     * Works out the key for this weather measurement.
     * @return
     */
    public String getKey(){
	return getKey(this.mtype,this.environment,this.variant);
    }

}
