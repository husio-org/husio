package org.husio.weather.service.pws;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.measure.quantity.Pressure;
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
 * Interfaces to PWSWeather.com
 * 
 * Subscribes weather station data and publishes to this site.
 * 
 * @author rafael
 *
 */
public class Driver extends HTTPWeatherService implements WeatherCommunityService {
    
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

    public static final String PWS_DATE_PARAM= "DateUTC";
    public static final DateFormat PWS_DATE_FORMAT = new UtcDateFormat();    
    
    public static final String PWS_PRESSURE_PARAM= "Baromin";
    public static final Unit<Pressure> PWS_PRESSURE_UNIT = WeatherUnits.INCH_OF_MERCURY;

    public static final String PWS_DEWPOINT_PARAM= "DewptF";
    public static final Unit<Temperature> PWS_DEWPOINT_UNIT = WeatherUnits.FAHRENHEIT;

    public static final String PWS_HUMIDITY_PARAM= "Humidity";
    public static final Unit<Humidity> PWS_HUMIDITY_UNIT = WeatherUnits.PERCENT_WATER;
    
    public static final String PWS_TEMPERATURE_PARAM= "TempF";
    public static final Unit<Temperature> PWS_TEMPERATURE_UNIT = WeatherUnits.FAHRENHEIT;
    
    public static final String PWS_WINDGUST_PARAM= "WindGustMPH";
    public static final Unit<Velocity> PWS_WINDGUST_UNIT = WeatherUnits.MILES_PER_HOUR;

    public static final String PWS_WINDSPEED_PARAM= "WindSpeedMPH";
    public static final Unit<Velocity> PWS_WINDSPEED_UNIT = WeatherUnits.MILES_PER_HOUR;
        
    private String stationId;
    private String password;
    private HttpClient httpclient = new DefaultHttpClient();

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
	this.addMeasurement(qparams, weather, PWS_HUMIDITY_PARAM, PWS_HUMIDITY_UNIT, ENVIRONMENT.OUTDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, PWS_PRESSURE_PARAM, PWS_PRESSURE_UNIT, ENVIRONMENT.OUTDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, PWS_TEMPERATURE_PARAM, PWS_TEMPERATURE_UNIT, ENVIRONMENT.OUTDOOR, TYPE.DISCRETE);
	this.addMeasurement(qparams, weather, PWS_WINDSPEED_PARAM, PWS_WINDSPEED_UNIT, ENVIRONMENT.OUTDOOR, TYPE.AVERAGE);
	this.addMeasurement(qparams, weather, PWS_WINDGUST_PARAM, PWS_WINDGUST_UNIT, ENVIRONMENT.OUTDOOR, TYPE.GUST);
	
	// Create the URI
	URI uri = URIUtils.createURI("http",PWS_SERVER , -1, PWS_UPDATE_URL, 
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
