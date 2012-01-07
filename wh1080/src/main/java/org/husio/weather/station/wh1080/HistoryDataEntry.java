package org.husio.weather.station.wh1080;

import javax.measure.Measure;
import javax.measure.quantity.Temperature;

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
	assert a % 0x10 ==0 : "History data entry is invalid, not aligned";
	this.address=a;
	this.station=s;
	this.data=new byte[32]; // the station can't read less that 32, we ignore the other 32
	//TODO: the last data entry might fail
	s.readAddress(address, data, 0);
    }

    @Override
    protected byte[] data() {
	return data;
    }
    
    /**
     * Returns the stored indoor temperature.
     * @return
     */
    public Measure<Temperature> getIndoorTemperature(){
	int value=this.readSignedShort(0x2);
	log.debug("Temperature value is:"+value);
	return Measure.valueOf(value, fmb().getIndoorTemperatureUnit());
    }
    
    /**
     * Returns the stored indoor temperature.
     * @return
     */
    public Measure<Temperature> getOutdoorTemperature(){
	int value=this.readSignedShort(0x5);
	return Measure.valueOf(value, fmb().getIndoorTemperatureUnit());
    }
    
    /**
     * helper method to get the fixed memory block
     */
    private FixedMemoryBlock fmb(){
	return this.station.fmb();
    }
}
