package org.husio.weather.json;

import java.io.IOException;
import java.text.ParseException;

import javax.measure.Measure;
import javax.measure.MeasureFormat;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.StdDeserializer;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.TYPE;

@SuppressWarnings("rawtypes")
public class DBObservedWeatherMeasureDeserializer extends StdDeserializer<ObservedWeatherMeasure> {
    
    private static MeasureFormat measureFormat=MeasureFormat.getInstance();

    public DBObservedWeatherMeasureDeserializer() {
	super(ObservedWeatherMeasure.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ObservedWeatherMeasure deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	ObservedWeatherMeasure result = new ObservedWeatherMeasure();
	while (jp.nextToken() != JsonToken.END_OBJECT) {
	    String fieldName = jp.getCurrentName();
	    // Let's move to value
	    jp.nextToken();
	    if (fieldName.equals("e")) {
		result.setEnvironment(ENVIRONMENT.values()[jp.getIntValue()]);
	    } else if (fieldName.equals("t")) {
		result.setType(TYPE.values()[jp.getIntValue()]);
	    } else if (fieldName.equals("v")) {
		result.setValidMetric(jp.getIntValue()==1);
	    } else if (fieldName.equals("m")) {
		try {
		    result.setMeasure((Measure) measureFormat.parseObject(jp.getText()));
		} catch (ParseException e) {
		    throw new IOException("Could not parse mesure",e);
		}
	    } else { // ignore, or signal error?
		throw new IOException("Unrecognized field '" + fieldName + "'");
	    }
	}
	return result;
    }

}
