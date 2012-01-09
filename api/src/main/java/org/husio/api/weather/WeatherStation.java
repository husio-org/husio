package org.husio.api.weather;

import javax.measure.Measure;
import javax.measure.quantity.Duration;


/**
 * Weather station interface, to be implemented by Weather Station drivers.
 * 
 * The weather server will spawn a new thread to pool the station at required intervals.
 * 
 * 
 * @author rafael
 *
 */
public interface WeatherStation {
    
    /**
     * Status of the weather station.
     * @author rafael
     *
     */
    public static enum STATUS { STOPPED, RUNNING, ERROR, SENSOR_FAILURE };
        
    /**
     * Starts the interface to the weather station.
     * @throws Exception
     */
    public void start() throws Exception;
    
    /**
     * Stops the connection to the weather station.
     * @throws Exception
     */
    public void stop() throws Exception;
    
    /**
     * Get the status of the station.
     */
    public STATUS getStatus();
    
    /**
     * Get the requested measure interval.
     * 
     * Interval at which the station can be pooled. A typical value would be 60 seconds. The server will pool for metrics
     * at the provided interval, and spawn a new thread for that task.
     * 
     */
    
    public Measure<Duration> getPoolingInverval();
    
    
}
