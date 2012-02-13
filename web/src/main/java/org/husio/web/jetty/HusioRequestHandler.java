package org.husio.web.jetty;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.husio.HusioApplication;
import org.husio.web.json.ObservedWeatherMeasureSerializer;
import org.husio.web.json.WeatherObservationSerializer;

import com.j256.ormlite.support.ConnectionSource;

public abstract class HusioRequestHandler extends AbstractHandler {
    
    protected ObjectMapper mapper = new ObjectMapper();
    
    protected ConnectionSource con;
    
    protected HusioRequestHandler() throws Exception{
	super();
	// Default Mapping Settings
	mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	SimpleModule husioModule = new SimpleModule("Husio", new Version(1, 0, 0, null));
	husioModule.addSerializer(new ObservedWeatherMeasureSerializer());
	husioModule.addSerializer(new WeatherObservationSerializer());
	mapper.registerModule(husioModule);
	
	// The database connection
	con=HusioApplication.getDbConnection();
    }



}
