package org.husio.weather.service.pws;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.husio.Configuration;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.MEASUREMENT_TYPE;
import org.husio.api.weather.ObservedWeatherMeasure.VARIANT;
import org.husio.api.weather.WeatherCommunityService;
import org.husio.api.weather.WeatherUnits;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.husio.weather.service.HTTPWeatherService;
import org.husio.weather.service.UtcDateFormat;
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
public class PwsDriver extends HTTPWeatherService implements WeatherCommunityService {
    
    private static final Logger log = LoggerFactory.getLogger(PwsDriver.class);
    
    // Service Configuration
    
    public static final String PWS_SERVER = "www.pwsweather.com";
    public static final String PWS_UPDATE_URL = "/pwsupdate/pwsupdate.php";
    public static final String PWS_SOFTWARE_PARAM= "softwaretype";
    public static final String PWS_SOFTWARE_VALUE= "husio";

    // User Configuration Settings
    
    public static final String PWS_STATION_ID_PARAM= "ID";
    public static final String PWS_STATION_ID_CONF_PARAM="org.husio.weather.service.pws.PwsDriver.stationId";
    public static final String PWS_PASSWORD_PARAM= "PASSWORD";
    public static final String PWS_PASSWORD_CONF_PARAM= "org.husio.weather.service.pws.PwsDriver.password";

    // Weather Observation Configuration
        
    public static final String PWS_ACTION_PARAM= "action";
    public static final String PWS_ACTION_VALUE= "updateraw";

    public static final String PWS_DATE_PARAM= "dateutc";
    public static final DateFormat PWS_DATE_FORMAT = new UtcDateFormat();    
    
    public static final String PWS_PRESSURE_PARAM= "baromin";
    public static final Unit<Pressure> PWS_PRESSURE_UNIT = WeatherUnits.INCH_OF_MERCURY;

    public static final String PWS_DEWPOINT_PARAM= "dewptf";
    public static final Unit<Temperature> PWS_DEWPOINT_UNIT = WeatherUnits.FAHRENHEIT;

    public static final String PWS_HUMIDITY_PARAM= "humidity";
    public static final Unit<Dimensionless> PWS_HUMIDITY_UNIT = WeatherUnits.PERCENT_WATER;
    
    public static final String PWS_TEMPERATURE_PARAM= "tempf";
    public static final Unit<Temperature> PWS_TEMPERATURE_UNIT = WeatherUnits.FAHRENHEIT;
    
    public static final String PWS_WINDGUST_PARAM= "windgustmph";
    public static final Unit<Velocity> PWS_WINDGUST_UNIT = WeatherUnits.MILES_PER_HOUR;

    public static final String PWS_WINDSPEED_PARAM= "windspeedmph";
    public static final Unit<Velocity> PWS_WINDSPEED_UNIT = WeatherUnits.MILES_PER_HOUR;

    public static final String PWS_WINDDIR_PARAM= "winddir";
    public static final Unit<Angle> PWS_WINDDIR_UNIT = WeatherUnits.DEGREES_FROM_NORTH;

    
    private String stationId;
    private String password;

    // TODO: Still pending implementation
    //<float> rainin:     inches/hour of rain
    //<float> rainday:    total rainfall for day (localtime)
    //<float> rainmonth:  total rainfall for month (localtime)
    //<float> rainyear:   total rainfall for year (localtime)
    //<string> weather:   unknown at this time (email me if you know!)
    
    /**
     * Says hello and registers with the event bus to receive 
     * Weather Observations 
     */
    public PwsDriver(){
	log.info("Starting PSWeather.com Community Service Connector");
	EventBusService.subscribe(this);
	this.stationId=Configuration.getProperty(PWS_STATION_ID_CONF_PARAM);
	this.password=Configuration.getProperty(PWS_PASSWORD_CONF_PARAM);
    }
    
    /**
     * Will submit the weather information to this service
     * @param weather
     */
    @EventHandler
    public void handleWeatherInformation(WeatherObservationEvent weather) throws Exception{
	log.debug("Submitting Weather Information:"+weather.getWeatherObservation());
	List<NameValuePair> qparams = new ArrayList<NameValuePair>();

	// Add basic params
	qparams.add(new BasicNameValuePair(PWS_ACTION_PARAM, PWS_ACTION_VALUE));
	qparams.add(new BasicNameValuePair(PWS_STATION_ID_PARAM, this.stationId));
	qparams.add(new BasicNameValuePair(PWS_PASSWORD_PARAM, this.password));
	qparams.add(new BasicNameValuePair(PWS_SOFTWARE_PARAM, PWS_SOFTWARE_VALUE));
	qparams.add(new BasicNameValuePair(PWS_DATE_PARAM, PWS_DATE_FORMAT.format(weather.getWeatherObservation().getTimestamp())));

	// Add each of the weather observations
	this.addMeasurement(qparams, weather, PWS_HUMIDITY_PARAM, PWS_HUMIDITY_UNIT, MEASUREMENT_TYPE.HUMIDITY,ENVIRONMENT.OUTDOOR, VARIANT.DISCRETE);
	this.addMeasurement(qparams, weather, PWS_PRESSURE_PARAM, PWS_PRESSURE_UNIT, MEASUREMENT_TYPE.PRESSURE, ENVIRONMENT.OUTDOOR, VARIANT.ABSOLUTE);
	this.addMeasurement(qparams, weather, PWS_TEMPERATURE_PARAM, PWS_TEMPERATURE_UNIT, MEASUREMENT_TYPE.TEMPERATURE, ENVIRONMENT.OUTDOOR, VARIANT.DISCRETE);
	this.addMeasurement(qparams, weather, PWS_WINDSPEED_PARAM, PWS_WINDSPEED_UNIT, MEASUREMENT_TYPE.WIND_SPEED, ENVIRONMENT.OUTDOOR, VARIANT.AVERAGE);
	this.addMeasurement(qparams, weather, PWS_WINDGUST_PARAM, PWS_WINDGUST_UNIT, MEASUREMENT_TYPE.WIND_SPEED, ENVIRONMENT.OUTDOOR, VARIANT.GUST);
	this.addMeasurement(qparams, weather, PWS_WINDDIR_PARAM, PWS_WINDDIR_UNIT, MEASUREMENT_TYPE.WIND_DIRECTION, ENVIRONMENT.OUTDOOR, VARIANT.DISCRETE);
	
	// Create the URI
	URI uri = URIUtils.createURI("http",PWS_SERVER , -1, PWS_UPDATE_URL, 
	URLEncodedUtils.format(qparams, "UTF-8"), null);
	this.submitWeatherInfo(uri);    }

    @Override
    public String getUnkownValue() {
	return null;
    }

}
