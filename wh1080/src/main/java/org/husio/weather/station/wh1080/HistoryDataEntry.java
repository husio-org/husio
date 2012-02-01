package org.husio.weather.station.wh1080;

import java.util.Date;

import javax.measure.Measure;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;

import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherObservationList;
import org.husio.api.weather.WeatherUnits;


/**
 * WH1080 EEPROM DATA DEFINITION 
 * 
 * See attached documentation. 
 * 
 * @author rafael
 *
 */
public class HistoryDataEntry extends WH1080Types{
        
    /**
     * The actual data as stores in the station. 16 byte block. 
     */
    private byte[] data;
    
    private static final int SAMPLING_TIME_ADDRESS=0x0;
    private static final int RELATIVE_HUMIDITY_IN_ADDRESS=0x1;
    private static final int TEMPERATURE_IN_ADDRESS=0x2;
    private static final int RELATIVE_HUMIDITY_OUT_ADDRESS=0x4;
    private static final int TEMPERATURE_OUT_ADDRESS=0x5;
    private static final int ABSOLUTE_PRESURE_ADDRESS=0x7;
    private static final int AVERAGE_WIND_SPEED_LOW_BITS_ADDRESS=0x9;
    private static final int GUST_WIND_SPEED_LOW_BITS_ADDRESS=0xA;
    private static final int WIND_SPEED_HIGH_NYBBLE_ADDRESS=0xB;
    private static final int WIND_DIRECTION_ADDRESS=0xC;
    private static final int TOTAL_RAIN_ADDRESS_ADDRESS=0xD;
    private static final int STATUS_ADDRESS=0xF;
    
    private WeatherObservation observation=new WeatherObservation();

    /**
     * Where the collected metrics are stored
     */
    private WeatherObservationList measures=new WeatherObservationList();

    
    /**
     * The timestamp of creation for this object, related, but not equal to
     * the data collection timestamp.
     */
    private Date recordCreationTimestamp=new Date();
    
    /**
     * the address of this entry in the memory map
     */
    private int address;
    
    /**
     * The station this entry belongs to
     */
    private WH1080Driver station;
    
    /**
     * Reads a new history data entry at the given address. it checks that the address is within range and
     * aligned according to the specification.
     * @param a
     */
    HistoryDataEntry(int a, WH1080Driver s) throws Exception{
	assert 0x100 <= a & a<= 0x01FFFF: "Histroy data entry address out of range"; 
	assert a % 0x10 ==0 : "History data entry is invalid, not aligned. You may need to reset the data of your station.";
	this.address=a;
	this.station=s;
	this.data=new byte[32]; // the station can't read less that 32, we ignore the other 32

	//TODO: the last data entry might fail
	s.readAddress(address, data, 0);
	
	//create the measures list, ready for retrieval!
	measures.add(this.getIndoorTemperature());
	measures.add(this.getOutdoorTemperature());
	measures.add(this.getAbsolutePressure());
	measures.add(this.getIndoorRelativeHumidity());
	measures.add(this.getOutdoorRelativeHumidity());
	measures.add(this.getAverageWind());
	measures.add(this.getWindGust());
	measures.add(this.getWindDirection());
	measures.add(this.getTotalRainfall());
	
	// create the observation object
	observation.setDuration(this.getDuration());
	observation.setTimestamp(this.getTimestamp());
	observation.setMeasures(this.getMeasures());
    }
    
    public WeatherObservation getObservation(){
	return this.observation;
    }

    /**
     * provides, for convenience, the data to the base class to
     * perform transformations.
     */
    @Override
    protected byte[] data() {
	return data;
    }
    
    /**
     * The data collection timestamp is the date in which this object 
     * was created minus the duration, as the object is created after 
     * the period finalizes. this is actually an approximation that might be 
     * wrong in up to less than another duration period.
     * @return
     */
    private Date getTimestamp(){
	long creation=this.recordCreationTimestamp.getTime();
	long collection=creation-this.getDuration().longValue(WeatherUnits.SECOND.divide(1000));
	return new Date(collection);
    }
    
    /**
     * The duration this record is covering. 
     * @return
     */
    private Measure<Duration> getDuration(){
	int value=this.readUnsignedByte(SAMPLING_TIME_ADDRESS);
	return Measure.valueOf(value,DURATION_UNIT);
    }
    
    /**
     * Returns the stored indoor temperature.
     * @return the temperature or null if the station has not a valid metric
     */
    private ObservedWeatherMeasure<Temperature> getIndoorTemperature(){
	ObservedWeatherMeasure<Temperature> ret=new ObservedWeatherMeasure<Temperature>();
	ret.setType(ObservedWeatherMeasure.TYPE.DISCRETE);
	ret.setEnvironment(ObservedWeatherMeasure.ENVIRONMENT.INDOOR);
	if(!this.isValidShortMetric(TEMPERATURE_IN_ADDRESS)){
	    ret.setValidMetric(false);
	}
	else{
	    int value=this.readSignedShort(TEMPERATURE_IN_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value, TEMPERATURE_UNIT));
	}
	return ret;
    }
    
    /**
     * Returns the stored indoor temperature.
     * @return the temperature or null if the station has not a valid metric
     */
    private ObservedWeatherMeasure<Temperature> getOutdoorTemperature(){
	ObservedWeatherMeasure<Temperature> ret=new ObservedWeatherMeasure<Temperature>();
	ret.setType(ObservedWeatherMeasure.TYPE.DISCRETE);
	ret.setEnvironment(ObservedWeatherMeasure.ENVIRONMENT.OUTDOOR);
	if(!this.isValidShortMetric(TEMPERATURE_OUT_ADDRESS)) ret.setValidMetric(false);
	else {
	    int value=this.readSignedShort(TEMPERATURE_OUT_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value,TEMPERATURE_UNIT));
	}
	return ret;
    }
    
    /**
     * Returns the stored absolute pressure
     */
    private ObservedWeatherMeasure<Pressure> getAbsolutePressure(){
	ObservedWeatherMeasure<Pressure> ret=new ObservedWeatherMeasure<Pressure>();
	ret.setType(ObservedWeatherMeasure.TYPE.ABSOLUTE);
	ret.setEnvironment(ObservedWeatherMeasure.ENVIRONMENT.OUTDOOR);
	if(!this.isValidShortMetric(ABSOLUTE_PRESURE_ADDRESS)) ret.setValidMetric(false);
	else{
	    int value=this.readUnsignedShort(ABSOLUTE_PRESURE_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value,PRESSURE_UNIT));
	}
	return ret;
    }
    
    /**
     * Returns relative humidity outside
     */
    private ObservedWeatherMeasure<Dimensionless> getOutdoorRelativeHumidity(){
	ObservedWeatherMeasure<Dimensionless> ret=new ObservedWeatherMeasure<Dimensionless>();
	ret.setType(ObservedWeatherMeasure.TYPE.DISCRETE);
	ret.setEnvironment(ObservedWeatherMeasure.ENVIRONMENT.OUTDOOR);	
	if(!this.isValidByteMetric(RELATIVE_HUMIDITY_OUT_ADDRESS)) ret.setValidMetric(false);
	else{
	    int value=this.readUnsignedByte(RELATIVE_HUMIDITY_OUT_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value,HUMIDITY_UNIT));
	}
	return ret;    
    }
    
    /**
     * Returns relative humidity outside
     */
    private ObservedWeatherMeasure<Dimensionless> getIndoorRelativeHumidity(){
	ObservedWeatherMeasure<Dimensionless> ret=new ObservedWeatherMeasure<Dimensionless>();
	ret.setType(ObservedWeatherMeasure.TYPE.DISCRETE);
	ret.setEnvironment(ObservedWeatherMeasure.ENVIRONMENT.INDOOR);
	if(!this.isValidByteMetric(RELATIVE_HUMIDITY_IN_ADDRESS)) ret.setValidMetric(false);
	else{
	    int value=this.readUnsignedByte(RELATIVE_HUMIDITY_IN_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value,HUMIDITY_UNIT));
	}
	return ret;
    }

    private ObservedWeatherMeasure<Velocity> getAverageWind(){
	ObservedWeatherMeasure<Velocity>  ret=new ObservedWeatherMeasure<Velocity> ();
	ret.setType(ObservedWeatherMeasure.TYPE.AVERAGE);	
	if(!this.isValidByteMetric(AVERAGE_WIND_SPEED_LOW_BITS_ADDRESS)) ret.setValidMetric(false);
	else{
	    int value=this.readByteAndHalf(AVERAGE_WIND_SPEED_LOW_BITS_ADDRESS, WIND_SPEED_HIGH_NYBBLE_ADDRESS, false);
	    ret.setMeasure(Measure.valueOf(value,WIND_UNIT));
	}
	return ret;
    }
    
    private ObservedWeatherMeasure<Velocity> getWindGust(){
	ObservedWeatherMeasure<Velocity>  ret=new ObservedWeatherMeasure<Velocity> ();
	ret.setType(ObservedWeatherMeasure.TYPE.GUST);
	if(!this.isValidByteMetric(GUST_WIND_SPEED_LOW_BITS_ADDRESS)) ret.setValidMetric(false);
	else{
	    int value=this.readByteAndHalf(GUST_WIND_SPEED_LOW_BITS_ADDRESS, WIND_SPEED_HIGH_NYBBLE_ADDRESS, true);
	    ret.setMeasure(Measure.valueOf(value,WIND_UNIT));
	}
	return ret;
    }
    
    private ObservedWeatherMeasure<Angle> getWindDirection(){
	ObservedWeatherMeasure<Angle>  ret=new ObservedWeatherMeasure<Angle> ();
	if(this.isBitSet(WIND_DIRECTION_ADDRESS, 7))ret.setValidMetric(false);
	else{
	    int value=this.readUnsignedByte(WIND_DIRECTION_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value,WIND_DIRECTION_UNIT));
	}
	return ret;
    }
    
    private ObservedWeatherMeasure<Length> getTotalRainfall(){
	ObservedWeatherMeasure<Length>  ret=new ObservedWeatherMeasure<Length> ();
	ret.setType(ObservedWeatherMeasure.TYPE.AGREGATED);
	if(this.isBitSet(TOTAL_RAIN_ADDRESS_ADDRESS, 6))ret.setValidMetric(false);
	else{
	    int value=this.readUnsignedByte(TOTAL_RAIN_ADDRESS_ADDRESS);
	    ret.setMeasure(Measure.valueOf(value,RAINFALL_UNIT));
	}
	return ret;
    }
    

    private WeatherObservationList getMeasures() {
	return this.measures;
    }   
    
    @Override
    public String toString(){
	return this.getTimestamp().toString()+" +"+this.getDuration().toSI()+"="+this.getMeasures();
    }
    
}
