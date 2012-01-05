package org.husio.test.weather;

import java.util.Locale;

import javax.measure.Measure;
import javax.measure.quantity.Velocity;
import javax.measure.unit.format.LocalFormat;

import org.husio.Configuration;
import org.husio.weather.WeatherApplication;
import org.husio.weather.WeatherUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


/**
 * Weather unit conversion tests
 * @author rafael
 *
 */
public class WeatherUnitTest {
    
    private static final Logger log = LoggerFactory.getLogger(WeatherUnitTest.class);
    
    @BeforeTest
    public void init(){
	Configuration.setupLogSystem();
    }
    
    @Test
    public void windUnitConversionTest(){
	log.debug("Starting wind conversion test");
	LocalFormat lf=LocalFormat.getInstance(Locale.ENGLISH);
	
	Measure<Velocity> windSpeed=Measure.valueOf(10, WeatherUnits.KILOMETERS_PER_HOUR);
	log.debug("10 km/h in m/s is:"+windSpeed.toSI());
	
	// check m/s to km/h
	windSpeed=Measure.valueOf(10, WeatherUnits.METERS_PER_SECOND);
	log.debug(windSpeed.toSI()+" equals "+windSpeed.to(WeatherUnits.KILOMETERS_PER_HOUR));
	assert(windSpeed.doubleValue(WeatherUnits.KILOMETERS_PER_HOUR)==36);
	
	// check m/h
	log.debug(windSpeed.toSI()+" equals "+windSpeed.to(WeatherUnits.MILES_PER_HOUR));
	
	// check Knots
	log.debug(windSpeed.toSI()+" equals "+windSpeed.to(WeatherUnits.KNOT));
	
    }
    
    
    
}
