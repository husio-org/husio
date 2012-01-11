package org.husio.eventbus;

import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

/**
 * Traces published events on the event bus
 * @author rafael
 *
 */
public class Tracer {
    
    private static final Logger log = LoggerFactory.getLogger(Tracer.class);
    
    public Tracer(){
	log.debug("Created EventBus tracer");
	EventBusService.subscribe(this);
    }

    @EventHandler
    public void handleWeatherObservation(WeatherObservationEvent weather){
	log.debug("Received weather observation event:"+weather.getWeatherObservation());
    }
    
    @EventHandler
    public void handleObject(Object o){
	log.debug("Received object event:"+o);
    }

}
