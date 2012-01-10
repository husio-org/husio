package org.husio.weather.station.wh1080;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The fixed memory block. 256 bytes with station settings and pointer to current record.
 * 
 * @author rafael
 *
 */
public class FixedMemoryBlock extends WH1080Types{
    
    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(FixedMemoryBlock.class);
    
    // We are mainly interested in setting
    // We are not interested in historic measures (min/max),
    // As those we will build in an station independent way.
    
    private static final int SAMPLING_INTERVAL_SETTING_ADDRESS=0x10;
    private static final int CURRENT_HISTORY_ENTRY_POINTER_ADDRESS=0x1E;

    
    /**
     * the actual memory as mapped from the station
     */
    private byte[] data=new byte[256];
    
    /**
     * The station this memory blcok belongs to
     */
    private Driver station;
    
    /**
     * Loads the memory block
     * @param s
     * @throws Exception
     */
    FixedMemoryBlock(Driver s) throws Exception{
	this.station=s;
	
	for (int i=0; i<256 ; i+=32){
	    s.readAddress(i, data, i);
	}
    }
    
    
    /**
     * Current memory block used by the station. Notes that it is a cycling memory,
     * @return
     */
    public int lastReadAddress(){
	return readUnsignedShort(CURRENT_HISTORY_ENTRY_POINTER_ADDRESS);
    }
    
    /**
     * The sampling interval at which this station will update itself
     */
    public Measure<Duration> getSamplingInterval(){
	int samplingInterval=readUnsignedByte(SAMPLING_INTERVAL_SETTING_ADDRESS);
	return Measure.valueOf(samplingInterval,DURATION_UNIT);
    }

    /**
     * Returns the data block for helper functions to work.
     */
    @Override
    protected byte[] data() {
	return data;
    }
        

}
