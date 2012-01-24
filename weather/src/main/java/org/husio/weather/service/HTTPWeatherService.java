package org.husio.weather.service;

import java.net.URI;
import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper Base Class for HTTP Weather Community Services
 * @author rafael
 *
 */
public abstract class HTTPWeatherService {
    
    private static final Logger log = LoggerFactory.getLogger(HTTPWeatherService.class);
    
    protected HttpClient httpclient = new DefaultHttpClient();

    
    /**
     * Helper method to add a measurement to the query string, its formatted as float.
     * @param params
     * @param weather
     * @param param
     * @param u
     * @param e
     * @param t
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void addMeasurement(List<NameValuePair> params, WeatherObservationEvent weather, String param, Unit u, ObservedWeatherMeasure.ENVIRONMENT e, ObservedWeatherMeasure.TYPE t ){
	String value=this.getUnkownValue();
	// Get the mesure we are after
	ObservedWeatherMeasure<? extends Quantity> measure=weather.getWeatherObservation().getMeasures().get(u.getDimension(), e, t);
	// If the measure exists and it is valid, add it in the required unit, and as float
	if(measure!=null){
	    if(measure.isValidMetric()){
		float v=measure.getMeasure().floatValue(u);
		value=Float.toString(v);
	    }
	}
	if(value!=null) params.add(new BasicNameValuePair(param, value));
    }
    
    /**
     * Don't ask me why exception handling for HTTPCLient is so unfriendly.
     * 
     * @param uri
     */
    protected void submitWeatherInfo(URI uri) throws Exception {
	HttpGet httpget = new HttpGet(uri);
	try{
	    
	log.trace("The URI is: "+httpget.getURI()); //this will log password
	HttpResponse response=this.httpclient.execute(httpget);
	
	// Consume the response
	HttpEntity entity = response.getEntity();
	EntityUtils.consume(entity);
	}
	finally{
	    httpget.abort();
	}
    }

    
    /**
     * What to send for undefined/invalid params. null will not add the parameters at all.
     * @return
     */
    public abstract String getUnkownValue();

}
