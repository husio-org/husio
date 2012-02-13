package org.husio.web.jetty;

import java.io.IOException;
import java.io.PrintWriter;

import javax.measure.Measure;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.husio.web.json.ObservedWeatherMeasureSerializer;
import org.husio.web.json.WeatherObservationSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

public class CurrentWeatherRequestHandler extends AbstractHandler {
    
    
    private WeatherObservationEvent lastObservation;
    private ObjectMapper mapper = new ObjectMapper();
    
    private static final Logger log = LoggerFactory.getLogger(CurrentWeatherRequestHandler.class);
    
    CurrentWeatherRequestHandler(){
	mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	SimpleModule husioModule = new SimpleModule("Husio", new Version(1, 0, 0, null));
	husioModule.addSerializer(new ObservedWeatherMeasureSerializer());
	husioModule.addSerializer(new WeatherObservationSerializer());
	mapper.registerModule(husioModule);
	EventBusService.subscribe(this);
    }

    /**
     * Dispatch incoming requests to specialized handlers
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	log.debug("Got request target:"+target+" baseRequest:"+baseRequest);
	if(target.startsWith("/rest/weather")) this.handleWeather(target, baseRequest, request, response);
    }
    
    /**
     * Handle weather requests
     */
    public void handleWeather(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	response.setContentType("application/json");
	String callBack=request.getParameter("callback");
	assert !callBack.isEmpty(): "No callback parameter set in JSON call";
	//PrintWriter out=new JsonCallbackPrintWriter(response.getWriter(),callBack);
	PrintWriter out=response.getWriter();
        response.setStatus(HttpServletResponse.SC_OK);
	mapper.writeValue(out,this.lastObservation);       
        ((Request)request).setHandled(true);
    }
    
    @EventHandler
    public void handleWeatherObservation(WeatherObservationEvent weather){
	this.lastObservation=weather;
    }

}
