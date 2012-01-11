package org.husio.test;

import javax.measure.Measure;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;

import org.husio.Configuration;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.TYPE;
import org.husio.api.weather.Humidity;
import org.husio.api.weather.WeatherUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ApiTest {
    private static final Logger log = LoggerFactory.getLogger(ApiTest.class);
    
    @BeforeTest
    public void init(){
	Configuration.setupLogSystem();
    }
    
    @Test
    public void measurementTest(){
	log.debug("Testing Weather API: Measurement 1");
	ObservedWeatherMeasure<Temperature> cwm=new ObservedWeatherMeasure<Temperature>();
	cwm.setEnvironment(ENVIRONMENT.INDOOR);
	cwm.setType(TYPE.MAXIMUM);
	cwm.setMeasure(Measure.valueOf(5, WeatherUnits.CELSIUS));
	log.debug("The test measure is "+cwm);
    }
    
    @Test
    public void measurement2Test(){
	log.debug("Testing Weather API: Measurement 2");
	ObservedWeatherMeasure<Pressure> cwm=new ObservedWeatherMeasure<Pressure>();
	cwm.setEnvironment(ENVIRONMENT.OUTDOOR);
	cwm.setType(TYPE.MAXIMUM);
	cwm.setMeasure(Measure.valueOf(5, WeatherUnits.HECTO_PASCAL));
	log.debug("The test measure is "+cwm);
    }
    
    @Test
    public void measurement3Test(){
	log.debug("Testing Weather API: Measurement 3");
	ObservedWeatherMeasure<Humidity> cwm=new ObservedWeatherMeasure<Humidity>();
	cwm.setEnvironment(ENVIRONMENT.OUTDOOR);
	cwm.setType(TYPE.MAXIMUM);
	cwm.setMeasure(Measure.valueOf(5, WeatherUnits.PERCENT_WATER));
	log.debug("The test measure is "+cwm);
    }
    
    @Test
    public void measurementCrossUnitTest(){
	log.debug("Testing Weather API: Cross Unit Measurement 1");
	ObservedWeatherMeasure<Pressure> cwm=new ObservedWeatherMeasure<Pressure>();
	cwm.setEnvironment(ENVIRONMENT.OUTDOOR);
	cwm.setType(TYPE.MAXIMUM);
	cwm.setMeasure(Measure.valueOf(105, NonSI.MILLIMETER_OF_MERCURY));
	log.debug("The test measure is "+cwm);
    }
}
