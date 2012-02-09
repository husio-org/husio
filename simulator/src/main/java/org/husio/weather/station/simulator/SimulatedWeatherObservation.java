package org.husio.weather.station.simulator;

import java.util.Date;

import javax.measure.Measure;
import javax.measure.unit.Unit;

import org.husio.Configuration;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.TYPE;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherObservationList;
import org.husio.api.weather.WeatherUnits;

/**
 * A simple simulated weather observation
 * 
 * @author rafael
 * 
 */
class SimulatedWeatherObservation{

    private double time = 0;
    
    private WeatherObservation observation=new WeatherObservation();


    /**
     * Creates a simulated observation.
     * @param t time counter, as a simulation generator.
     */
    SimulatedWeatherObservation(double t) {
	this.time = t;
	observation.setTimestamp(this.timestamp);
	observation.setDuration(this.getDuration());
	observation.setMeasures(this.getMeasures());
    }
    
    public WeatherObservation getObservation(){
	return this.observation;
    }

    private Date timestamp = new Date();


    private Measure getDuration() {
	long delay = Integer.parseInt(Configuration.getProperty(SimulatorDriver.POLL_INTERVAL_CONFIG_OPTION));
	return Measure.valueOf(delay, WeatherUnits.SECOND);
    }

    @SuppressWarnings("unchecked")
    private WeatherObservationList getMeasures() {
	WeatherObservationList wol = new WeatherObservationList();
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.DISCRETE, WeatherUnits.CELSIUS, -10, 20, 0));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.DEW, WeatherUnits.CELSIUS, -12, 17, 0));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.INDOOR, TYPE.DISCRETE, WeatherUnits.CELSIUS, 18, 22, 0));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.DISCRETE, WeatherUnits.PERCENT_WATER, 70, 99.9, 0.2));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.INDOOR, TYPE.DISCRETE, WeatherUnits.PERCENT_WATER, 30, 50, 0.25));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.ABSOLUTE, WeatherUnits.HECTO_PASCAL, 920, 1080, 0.3));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.RELATIVE, WeatherUnits.HECTO_PASCAL, 920, 1080, 0.6));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.AVERAGE, WeatherUnits.KNOT, 0, 35, 0.1));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.GUST, WeatherUnits.KNOT, 0, 50, 0.1));
	wol.add(this.getSimulatedMeasure(ENVIRONMENT.OUTDOOR, TYPE.DISCRETE, WeatherUnits.DEGREES_FROM_NORTH, 0, 360, 0.15));
	return wol;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ObservedWeatherMeasure getSimulatedMeasure(ENVIRONMENT e, TYPE t, Unit u, double min, double max, double offset) {
	ObservedWeatherMeasure ret = new ObservedWeatherMeasure();
	ret.setEnvironment(e);
	ret.setType(t);
	ret.setMeasure(Measure.valueOf(this.getSimulatedValue(time, min, max, offset), u));
	return ret;
    }

    /**
     * Generates a simulated metric that follows a sin curve
     * 
     * @param time
     *            the simulated time
     * @param min
     *            the minimun value
     * @param max
     *            the maximum value
     * @param offset
     *            0-1 time offset in sin cycle, so that not all curves follow
     *            the same shape
     * @return
     */
    private double getSimulatedValue(double time, double min, double max, double offset) {
	offset = offset * 2 * Math.PI;
	double amplitude = max - min;
	return min + amplitude * Math.sin(time + offset);
    }

    @Override
    public String toString() {
	return this.timestamp.toString() + " +" + this.getDuration().toSI() + "=" + this.getMeasures();
    }

}
