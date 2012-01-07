package org.husio.weather.station.wh1080;

import javax.measure.quantity.Temperature;
import javax.measure.unit.Unit;

import org.husio.weather.api.WeatherUnits;

/**
 * The fixed memory block. 256 bytes with station settings and pointer to current record.
 * 
 * @author rafael
 *
 */
public class FixedMemoryBlock extends WH1080Types{
    
    /**
     * the actual memory as mapped from the station
     */
    private byte[] data=new byte[256];
    
    /**
     * The station this memory blcok belongs to
     */
    private WH1080 station;
    
    /**
     * Loads the memory block
     * @param s
     * @throws Exception
     */
    FixedMemoryBlock(WH1080 s) throws Exception{
	this.station=s;
	
	for (int i=0; i<256 ; i+=32){
	    s.readAddress(i, data, i);
	}
    }
    
    /**
     * Returns the Unit format of indoor temperature
     */
    Unit<Temperature> getIndoorTemperatureUnit(){
	// the factor 0.1 has been preserved to match station especificacionts. other expressions are possible.
	return isBitSet(0x11,0) ?  WeatherUnits.FAHRENHEIT.times(0.1) : WeatherUnits.CELSIUS.times(0.1);
    }
    
    /**
     * Returns the Unit format of outdoor temperature
     */
    Unit<Temperature> getOutdoorTemperatureUnit(){
	return isBitSet(0x11,1) ?  WeatherUnits.FAHRENHEIT.times(0.1) : WeatherUnits.CELSIUS.times(0.1);
    }
    
    
    /**
     * Last memory block completed by the station. Notes that it is a cycling memory,
     * they, if the current address being read is the first one (256), the previous was
     * the last one.
     * @return
     */
    public int lastReadAddress(){
	int currentAddress=readUnsignedShort(0x1e);
	return currentAddress==256 ? 65520: currentAddress-16;
    }

    /**
     * Returns the data block for helper rutines to work.
     */
    @Override
    protected byte[] data() {
	return data;
    }
        

}
