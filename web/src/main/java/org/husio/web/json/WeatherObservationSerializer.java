package org.husio.web.json;

import java.io.IOException;

import javax.measure.Measure;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.husio.api.weather.WeatherObservation;

/**
 * Observation serializer, for our convenience, we prefer the hash table to be delivered as an array.
 * @author rafael
 *
 */
public class WeatherObservationSerializer  extends JsonSerializer<WeatherObservation>{
    
    @Override
    public Class<WeatherObservation> handledType(){
	return WeatherObservation.class;
    }

    @Override
    public void serialize(WeatherObservation value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
	jgen.writeStartObject();
	//jgen.writeObjectField("timestamp", value.getTimestamp());
	jgen.writeObjectField("measures",value.getMeasures().elements());
	jgen.writeEndObject();
    }

}
