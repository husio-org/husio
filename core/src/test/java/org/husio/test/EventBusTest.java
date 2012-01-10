package org.husio.test;

import java.util.Date;

import org.husio.Configuration;
import org.husio.eventbus.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;

public class EventBusTest {
    
    private static final Logger log = LoggerFactory.getLogger(EventBusTest.class);
    
    private Tracer tracer;
    
    @BeforeTest
    public void init() {
	Configuration.setupLogSystem();
	// this is supposed to trace the published objects
	tracer=new Tracer();
	EventBusService.subscribe(this);
    }

    @Test
    public void testEventBus(){
	// Just publish one object
	EventBusService.publish(new Object());
    }
    
    @Test
    public void testEventBusBaseClass(){	
	// Just publish one object
	EventBusService.publish((Object) new Date());
    }
    
    @EventHandler
    public void sampleHandler(Date d){
	log.debug("Subscrived Object:"+d);
    }
    
}
