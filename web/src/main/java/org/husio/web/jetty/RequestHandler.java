package org.husio.web.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

public class RequestHandler extends AbstractHandler {
    
    WeatherObservationEvent lastObservation;
    
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    
    RequestHandler(){
	EventBusService.subscribe(this);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	log.debug("Got request target:"+target+" baseRequest:"+baseRequest);
	response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello world from husio</h1>");
        ((Request)request).setHandled(true);
    }
    
    @EventHandler
    public void handleWeatherObservation(WeatherObservationEvent weather){
	this.lastObservation=weather;
    }

}
