package org.husio.web.json;

import java.io.IOException;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.WeatherUnits;
import org.husio.weather.UserMetricSystem;

/**
 * This will serialize JSR 275 metrics in a more javascript friendly way for 
 * frontend processing
 * 
 * @author rafael
 *
 */
@SuppressWarnings("rawtypes")
public class ObservedWeatherMeasureSerializer extends JsonSerializer<ObservedWeatherMeasure>{
    
    private UserMetricSystem userMetricSystem=new UserMetricSystem();
    
    @Override
    public Class<ObservedWeatherMeasure> handledType(){
	return ObservedWeatherMeasure.class;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void serialize(ObservedWeatherMeasure value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
	jgen.writeStartObject();
	jgen.writeStringField("dimensionName",value.getDimensionName());
	jgen.writeStringField("environment",value.getEnvironment().toString());
	jgen.writeStringField("type",value.getType().toString());
	jgen.writeBooleanField("validMetric", value.isValidMetric());
	Unit<?> u=userMetricSystem.getPreferredUnit(value.getMeasure().getUnit());
	jgen.writeNumberField("measuredValue", value.getMeasure().to(u).getValue().floatValue());
	jgen.writeStringField("measuredUnit", u.toString());
	jgen.writeEndObject();
    }
}
