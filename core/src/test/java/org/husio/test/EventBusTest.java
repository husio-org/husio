package org.husio.test;

import org.husio.Configuration;
import org.husio.eventbus.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.adamtaft.eb.EventBusService;

public class EventBusTest {
    
    private static final Logger log = LoggerFactory.getLogger(EventBusTest.class);
    
    @BeforeTest
    public void init() {
	Configuration.setupLogSystem();
    }

    @Test
    public void testEventBus(){
	// this is supposed to trace the published objects
	Tracer tracer=new Tracer();
	
	// Just publish one object
	EventBusService.publish(new Object());
	
    }
    
}
