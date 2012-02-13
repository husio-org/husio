package org.husio.web.jetty;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.eclipse.jetty.server.Request;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.husio.web.json.ObservedWeatherMeasureSerializer;
import org.husio.web.json.WeatherObservationSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

public class CurrentWeatherRequestHandler extends HusioRequestHandler {
    
    private WeatherObservationEvent lastObservation;
    
    private static final Logger log = LoggerFactory.getLogger(CurrentWeatherRequestHandler.class);
    
    CurrentWeatherRequestHandler() throws Exception{
	EventBusService.subscribe(this);
    }

    /**
     * Dispatch incoming requests to specialized handlers
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	if(target.startsWith("/rest/weather")) this.handleWeather(target, baseRequest, request, response);
    }
    
    /**
     * Handle weather requests
     */
    public void handleWeather(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	log.debug("Got request target:"+target+" baseRequest:"+baseRequest);
	response.setContentType("application/json");
	mapper.writeValue(response.getWriter(),this.lastObservation);  
        response.setStatus(HttpServletResponse.SC_OK);
        ((Request)request).setHandled(true);
    }
    
    @EventHandler
    public void handleWeatherObservation(WeatherObservationEvent weather){
	this.lastObservation=weather;
    }

}
