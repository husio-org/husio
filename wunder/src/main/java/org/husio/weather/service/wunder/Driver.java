package org.husio.weather.service.wunder;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.husio.Configuration;
import org.husio.api.weather.Humidity;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.TYPE;
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
 * Interfaces to Weather Underground
 * 
 * See http://wiki.wunderground.com/index.php/PWS_-_Upload_Protocol
 * 
 * Subscribes weather station data and publishes to this site.
 * 
 * @author rafael
 *
 */
public class Driver extends HTTPWeatherService implements WeatherCommunityService {
    
    private static final Logger log = LoggerFactory.getLogger(Driver.class);
    
    // Service Configuration
    
    public static final String WU_SERVER = "weatherstation.wunderground.com";
    public static final String WU_UPDATE_URL = "/weatherstation/updateweatherstation.php";
    public static final String WU_SOFTWARE_PARAM= "softwaretype";
    public static final String WU_SOFTWARE_VALUE= "husio";

    // User Configuration Settings
    
    public static final String WU_STATION_ID_PARAM= "ID";
    public static final String WU_STATION_ID_CONF_PARAM="org.husio.weather.service.wunder.Driver.stationId";
    public static final String WU_PASSWORD_PARAM= "PASSWORD";
    public static final String WU_PASSWORD_CONF_PARAM= "org.husio.weather.service.wunder.Driver.password";

    // Weather Observation Configuration
        
    public static final String WU_ACTION_PARAM= "action";
    public static final String WU_ACTION_VALUE= "updateraw";

    public static final String WU_DATE_PARAM= "dateutc";
    public static final DateFormat WU_DATE_FORMAT =  new UtcDateFormat();       
    
    public static final String WU_PRESSURE_PARAM= "baromin";
    public static final Unit<Pressure> WU_PRESSURE_UNIT = WeatherUnits.INCH_OF_MERCURY;

    public static final String WU_HUMIDITY_PARAM= "humidity";
    public static final String WU_INDOOR_HUMIDITY_PARAM= "indoorhumidity";
    public static final Unit<Humidity> WU_HUMIDITY_UNIT = WeatherUnits.PERCENT_WATER;
    
    public static final String WU_TEMPERATURE_PARAM= "tempf";
    public static final String WU_INDOOR_TEMPERATURE_PARAM= "indoortempf";
    public static final Unit<Temperature> WU_TEMPERATURE_UNIT = WeatherUnits.FAHRENHEIT;
    
    public static final String WU_WINDGUST_PARAM= "windgustmph";
    public static final Unit<Velocity> WU_WINDGUST_UNIT = WeatherUnits.MILES_PER_HOUR;

    public static final String WU_WINDSPEED_PARAM= "windspeedmph";
    public static final Unit<Velocity> WU_WINDSPEED_UNIT = WeatherUnits.MILES_PER_HOUR;
    
    public static final String WU_WINDDIR_PARAM= "winddir";
    public static final Unit<Angle> WU_WINDDIR_UNIT = WeatherUnits.DEGREES_FROM_NORTH;

        
    private String stationId;
    private String password;
    private HttpClient httpclient = new DefaultHttpClient();

    
    /**
     * Says hello and registers with the event bus to receive 
     * Weather Observations 
     */
    public Driver(){
	log.info("Starting Weather Underground Community Service Connector");
	EventBusService.subscribe(this);
	this.stationId=Configuration.getProperty(WU_STATION_ID_CONF_PARAM);
	this.password=Configuration.getProperty(WU_PASSWORD_CONF_PARAM);
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
	qparams.add(new BasicNameValuePair(WU_ACTION_PARAM, WU_ACTION_VALUE));
	qparams.add(new BasicNameValuePair(WU_STATION_ID_PARAM, this.stationId));
	qparams.add(new BasicNameValuePair(WU_PASSWORD_PARAM, this.password));
	qparams.add(new BasicNameValuePair(WU_SOFTWARE_PARAM, WU_SOFTWARE_VALUE));
	qparams.add(new BasicNameValuePair(WU_DATE_PARAM, WU_DATE_FORMAT.format(weather.getWeatherObservation().getTimestamp())));

	// Add each of the weather observations
	this.addMeasurement(qparams, weather, WU_HUMIDITY_PARAM, WU_HUMIDITY_UNIT, ENVIRONMENT.OUTDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, WU_INDOOR_HUMIDITY_PARAM, WU_HUMIDITY_UNIT, ENVIRONMENT.INDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, WU_PRESSURE_PARAM, WU_PRESSURE_UNIT, ENVIRONMENT.OUTDOOR, TYPE.ABSOLUTE);
	this.addMeasurement(qparams, weather, WU_TEMPERATURE_PARAM, WU_TEMPERATURE_UNIT, ENVIRONMENT.OUTDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, WU_INDOOR_TEMPERATURE_PARAM, WU_TEMPERATURE_UNIT, ENVIRONMENT.INDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, WU_WINDSPEED_PARAM, WU_WINDSPEED_UNIT, ENVIRONMENT.OUTDOOR, TYPE.AVERAGE);
	this.addMeasurement(qparams, weather, WU_WINDGUST_PARAM, WU_WINDGUST_UNIT, ENVIRONMENT.OUTDOOR, TYPE.GUST);
	this.addMeasurement(qparams, weather, WU_WINDDIR_PARAM, WU_WINDDIR_UNIT, ENVIRONMENT.OUTDOOR, TYPE.DISCRETE);
	
	// Create the URI
	URI uri = URIUtils.createURI("http",WU_SERVER , -1, WU_UPDATE_URL, 
	URLEncodedUtils.format(qparams, "UTF-8"), null);
	HttpGet httpget = new HttpGet(uri);
	log.trace("The URI is: "+httpget.getURI()); //this will log password
	HttpResponse response=this.httpclient.execute(httpget);
	
	// Consume the response
	HttpEntity entity = response.getEntity();
	if (entity != null) entity = new BufferedHttpEntity(entity);
    }

    @Override
    public String getUnkownValue() {
	return null;
    }

}
