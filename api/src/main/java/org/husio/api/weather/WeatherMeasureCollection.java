package org.husio.api.weather;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

/**
 * A weather measurement collection. Normally the weather station will collect metrics for a certain period of time and
 * release a historic weather entry.
 * 
 * @author rafael
 *
 */
public interface WeatherMeasureCollection {
    
    /**
     * The timestamp for this collection.
     */
    public Date getTimestamp();

    /**
     * the duration covered by this measure collection.
     */
    public Measure<Duration> getDuration();

    /**
     * The collection of measurements that where collected.
     */
    public List<CollectedWeatherMeasure> getMeasures();

}
