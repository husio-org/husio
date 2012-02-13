package org.husio.web.json;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import javax.measure.unit.format.LocalFormat;
import javax.measure.unit.format.SymbolMap;

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
    
    private LocalFormat localFormat=LocalFormat.getInstance(new SymbolMap(ResourceBundle.getBundle("org.husio.api.weather.LocalFormat")));
    
    @Override
    public Class<ObservedWeatherMeasure> handledType(){
	return ObservedWeatherMeasure.class;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void serialize(ObservedWeatherMeasure value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
	jgen.writeStartObject();
	jgen.writeStringField("mtype",value.getMtype().toString());
	jgen.writeStringField("environment",value.getEnvironment().toString());
	jgen.writeStringField("variant",value.getVariant().toString());
	jgen.writeBooleanField("validMetric", value.isValidMetric());
	Unit<?> u=userMetricSystem.getPreferredUnit(value.getMeasure().getUnit());
	jgen.writeNumberField("measuredValue", value.getMeasure().to(u).getValue().floatValue());
	jgen.writeStringField("measuredUnit", localFormat.format(u));
	jgen.writeEndObject();
    }
}
