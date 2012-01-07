package org.husio.weather.station.wh1080;

import javax.measure.Measure;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;

import org.husio.weather.api.Humidity;
import org.husio.weather.api.WeatherUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * WH1080 EEPROM DATA DEFINITION 
 * 
 * See attached documentation. 
 * 
 * @author rafael
 *
 */
public class HistoryDataEntry extends WH1080Types {
    
    private static final Logger log = LoggerFactory.getLogger(HistoryDataEntry.class);
    
    /**
     * The actual data as stores in the station. 16 byte block. 
     */
    private byte[] data;
    
    private static final int SAMPLE_TIME_ADDRESS=0x0;
    private static final int RELATIVE_HUMIDITY_IN_ADDRESS=0x1;
    private static final int TEMPERATURE_IN_ADDRESS=0x2;
    private static final int RELATIVE_HUMIDITY_OUT_ADDRESS=0x4;
    private static final int TEMPERATURE_OUT_ADDRESS=0x5;
    private static final int ABSOLUTE_PRESURE_ADDRESS=0x7;
    private static final int AVERAGE_WIND_SPEED_ADDRESS=0x9;
    private static final int WIND_SPEED_GUST_ADDRESS=0xA;
    private static final int WIND_SPEED_HIGH_ADDRESS=0xB;
    private static final int WIND_DIRECTION_ADDRESS=0xB;
    private static final int TOTAL_RAIN_ADDRESS_ADDRESS=0xC;
    private static final int STATUS_ADDRESS=0xE;
    
    /**
     * the address of this entry in the memory map
     */
    private int address;
    
    /**
     * The station this entry belongs to
     */
    private WH1080 station;
    
    /**
     * Reads a new history data entry at the given address. it cheks that the address is within range and
     * aligned according to the specification.
     * @param a
     */
    HistoryDataEntry(int a, WH1080 s) throws Exception{
	assert 0x100 <= a & a<= 0x01FFFF: "Histroy data entry address out of range"; 
	assert a % 0x10 ==0 : "History data entry is invalid, not aligned. You may need to reset the data of your station.";
	this.address=a;
	this.station=s;
	this.data=new byte[32]; // the station can't read less that 32, we ignore the other 32
	//TODO: the last data entry might fail
	s.readAddress(address, data, 0);
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
     * Returns the stored indoor temperature.
     * @return the temperature or null if the station has not a valid metric
     */
    public Measure<Temperature> getIndoorTemperature(){
	if(!this.isValidShortMetric(TEMPERATURE_IN_ADDRESS)) return null;
	int value=this.readSignedShort(TEMPERATURE_IN_ADDRESS);
	return Measure.valueOf(value, TEMPERATURE_UNIT);
    }
    
    /**
     * Returns the stored indoor temperature.
     * @return the temperature or null if the station has not a valid metric
     */
    public Measure<Temperature> getOutdoorTemperature(){
	if(!this.isValidShortMetric(TEMPERATURE_OUT_ADDRESS)) return null;
	int value=this.readSignedShort(TEMPERATURE_OUT_ADDRESS);
	return Measure.valueOf(value,TEMPERATURE_UNIT);
    }
    
    
    /**
     * Returns the stored absolute pressure
     */
    public Measure<Pressure> getAbsolutePressure(){
	if(!this.isValidShortMetric(ABSOLUTE_PRESURE_ADDRESS)) return null;
	int value=this.readUnsignedShort(ABSOLUTE_PRESURE_ADDRESS);
	return Measure.valueOf(value,PRESSURE_UNIT);
    }
    
    /**
     * Returns relative humidity outside
     */
    public Measure<Humidity> getOutdoorRelativeHumidity(){
	if(!this.isValidByteMetric(RELATIVE_HUMIDITY_OUT_ADDRESS)) return null;
	int value=this.readUnsignedByte(RELATIVE_HUMIDITY_OUT_ADDRESS);
	return Measure.valueOf(value,HUMIDITY_UNIT);
    }
    
    /**
     * Returns relative humidity outside
     */
    public Measure<Humidity> getIndoorRelativeHumidity(){
	if(!this.isValidByteMetric(RELATIVE_HUMIDITY_IN_ADDRESS)) return null;
	int value=this.readUnsignedByte(RELATIVE_HUMIDITY_IN_ADDRESS);
	return Measure.valueOf(value,HUMIDITY_UNIT);
    }

    public Measure<Velocity> getAverageWind(){
	if(!this.isValidByteMetric(AVERAGE_WIND_SPEED_ADDRESS)) return null;
	int value=this.readUnsignedByte(AVERAGE_WIND_SPEED_ADDRESS);
	return Measure.valueOf(value,WIND_UNIT);
    }
    
    public Measure<Velocity> getWindHigh(){
	if(!this.isValidByteMetric(WIND_SPEED_HIGH_ADDRESS)) return null;
	int value=this.readUnsignedByte(WIND_SPEED_HIGH_ADDRESS);
	return Measure.valueOf(value,WIND_UNIT);
    }
    
    public Measure<Velocity> getWindGust(){
	if(!this.isValidByteMetric(WIND_SPEED_GUST_ADDRESS)) return null;
	int value=this.readUnsignedByte(WIND_SPEED_GUST_ADDRESS);
	return Measure.valueOf(value,WIND_UNIT);
    }   
    
}
