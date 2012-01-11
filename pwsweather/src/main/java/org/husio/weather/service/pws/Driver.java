package org.husio.weather.service.pws;

import java.text.DateFormat;

import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.husio.api.weather.Humidity;
import org.husio.api.weather.WeatherCommunityService;
import org.husio.api.weather.WeatherUnits;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

/**
 * Interfaces to PWSWeather.com
 * 
 * Subscribes weather station data and publishes to this site.
 * 
 * @author rafael
 *
 */
public class Driver implements WeatherCommunityService{
    
    private static final Logger log = LoggerFactory.getLogger(Driver.class);
    
    // Service Configuration
    
    public static final String PWS_SERVER = "www.pwsweather.com";
    public static final String PWS_UPDATE_URL = "/pwsupdate/pwsupdate.php";
    public static final String PWS_SOFTWARE_PARAM= "softwaretype";
    public static final String PWS_SOFTWARE_VALUE= "husio";

    // User Configuration Settings
    
    public static final String PWS_STATION_ID_PARAM= "ID";
    public static final String PWS_STATION_ID_CONF_PARAM="org.husio.weather.service.pws.Driver.stationId";
    public static final String PWS_PASSWORD_PARAM= "PASSWORD";
    public static final String PWS_PASSWORD_CONF_PARAM= "org.husio.weather.service.pws.Driver.password";

    // Weather Observation Configuration
        
    public static final String PWS_ACTION_PARAM= "action";
    public static final String PWS_ACTION_VALUE= "updateraw";

    public static final String PWS_DATE_PARAM= "dateutc";
    public static final DateFormat PWS_DATE_FORMAT = new PwsDateFormat();    
    
    public static final String PWS_PRESSURE_PARAM= "pressure";
    public static final Unit<Pressure> PWS_PRESSURE_UNIT = WeatherUnits.MILLIMETER_OF_MERCURY;

    public static final String PWS_DEWPOINT_PARAM= "dewpoint";
    public static final Unit<Temperature> PWS_DEWPOINT_UNIT = WeatherUnits.FAHRENHEIT;

    public static final String PWS_HUMIDITY_PARAM= "humidity";
    public static final Unit<Humidity> PWS_HUMIDITY_UNIT = WeatherUnits.PERCENT_WATER;
    
    public static final String PWS_TEMPERATURE_PARAM= "tempf";
    public static final Unit<Temperature> PWS_TEMPERATURE_UNIT = WeatherUnits.FAHRENHEIT;
    
    public static final String PWS_WINDGUST_PARAM= "windgust";
    public static final Unit<Velocity> PWS_WINDGUST_UNIT = WeatherUnits.MILES_PER_HOUR;

    public static final String PWS_WINDSPEED_PARAM= "windspeed";
    public static final Unit<Velocity> PWS_WINDSPEED_UNIT = WeatherUnits.MILES_PER_HOUR;
    
    public static final String PWS_UNKNOWN_VALUE= "NA";


    // TODO: Still pending implementation
    //<float> rainin:     inches/hour of rain
    //<float> rainday:    total rainfall for day (localtime)
    //<float> rainmonth:  total rainfall for month (localtime)
    //<float> rainyear:   total rainfall for year (localtime)
    //<float> winddir:    in degrees, between 0.0 and 360.0
    //<string> weather:   unknown at this time (email me if you know!)
    
    /**
     * Says hello and registers with the event bus to receive 
     * Weather Observations 
     */
    public Driver(){
	log.info("Starting PSWeather.com Community Service Connector");
	EventBusService.subscribe(this);
    }
    
    /**
     * Will submit the weather information to this service
     * @param weather
     */
    @EventHandler
    public void handleWeatherInformation(WeatherObservationEvent weather){
	log.debug("Submitting Weather Information:"+weather.getWeatherObservation());
    }

}
