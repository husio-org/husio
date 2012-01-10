package org.husio.api.weather;



/**
 * Weather station interface, to be implemented by Weather Station drivers.
 * 
 * The weather server will spawn a new thread to pool the station at required intervals.
 * 
 * Weather stations must publish HistoryDataEntries as they are produced to the EventBus. Recommended
 * publishing frequency is arround 5 minutes.
 * 
 * Implementers will normally 
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
    
}
