package org.husio.api.weather;

import java.util.Date;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

/**
 * A weather observation. Normally the weather station will collect metrics for a certain period of time and
 * release a historic weather entry.
 * 
 * @author rafael
 *
 */
public interface WeatherObservation {
    
    /**
     * The timestamp for this observation.
     */
    public Date getTimestamp();

    /**
     * the duration covered by this observation.
     */
    public Measure<Duration> getDuration();

    /**
     * The collection of measurements that where collected.
     */
    public WeatherObservationList getMeasures();

}
