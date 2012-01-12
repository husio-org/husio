package org.husio.api;

/**
 * Marker interface that signals that only one module of this type can be instantiated.
 * @author rafael
 *
 */
public interface Singleton {
    /**
     * The type of sigleton modules, only one per type is allowed in the system.
     * @author rafael
     *
     */
    public static enum MODULE_TYPE {WEATHER_STATION}
    
    /**
     * Singleton modules must report with type they implement.
     * @return
     */
    public MODULE_TYPE getModuleType();
}
