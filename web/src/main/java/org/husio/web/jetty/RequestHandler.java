package org.husio.web.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

public class RequestHandler extends AbstractHandler {
    
    
    private WeatherObservationEvent lastObservation;
    private ObjectMapper mapper = new ObjectMapper();
    
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    RequestHandler(){
	mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	EventBusService.subscribe(this);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
