package org.husio.weather.json;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.measure.Measure;
import javax.measure.MeasureFormat;
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
 * This will serialize JSR 275 metrics in a more DB friendly way for 
 * frontend processing
 * 
 * @author rafael
 *
 */
@SuppressWarnings("rawtypes")
public class DBObservedWeatherMeasureSerializer extends JsonSerializer<ObservedWeatherMeasure>{

    private static MeasureFormat measureFormat=MeasureFormat.getInstance();
    
    @Override
    public Class<ObservedWeatherMeasure> handledType(){
	return ObservedWeatherMeasure.class;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void serialize(ObservedWeatherMeasure value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
	jgen.writeStartObject();
	jgen.writeNumberField("e",value.getEnvironment().ordinal());
	jgen.writeNumberField("t",value.getType().ordinal());
	jgen.writeNumberField("v", value.isValidMetric()?1:0);
	jgen.writeStringField("m", measureFormat.format(value.getMeasure().toSI()));
	jgen.writeEndObject();
    }
}
