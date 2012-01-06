package org.husio.weather.api;


/**
 * Weather station interface, to be implemented by Weather Station drivers.
 * @author rafael
 *
 */
public interface WeatherStation {
        
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
    
}
