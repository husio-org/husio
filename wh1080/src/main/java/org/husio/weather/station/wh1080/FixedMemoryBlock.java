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
    
    static final int SAMPLING_INTERVAL_SETTING_ADDRESS=0x10;
    static final int CURRENT_HISTORY_ENTRY_POINTER_ADDRESS=0x1E;
    static final int DATA_REFRESHED_SETTING_ADDRESS=0x1a;
    
    static final int FIRST_HISTORY_ENTRY_ADDRESS=0x100;
    // documentation is not clear about the last address
    // the following address has been checked empirically. 
    static final int LAST_HISTORY_ENTRY_ADDRESS=0xFFF0;
    static final int HISTORY_ENTRY_SIZE=0x10;
    static final byte SETTING_HAS_CHANGED_VALUE=(byte) 0xaa;
    
    /**
     * the actual memory as mapped from the station
     */
    private byte[] data=new byte[256];
    
    /**
     * The station this memory blcok belongs to
     */
    private WH1080Driver station;
    
    /**
     * Loads the memory block
     * @param s
     * @throws Exception
     */
    FixedMemoryBlock(WH1080Driver s) throws Exception{
	this.station=s;
	
	for (int i=0; i<256 ; i+=32){
	    s.readAddress(i, data, i);
	}
    }
    
    
    /**
     * Last Completed Memory Block by the Station. Notes that it is a cycling memory with a min and max address,
     * plus each entry has a certain size.
     * @return
     */
    public int lastCompletedEntryAddress(){
	int ret;
	int currentEntryAddress=readUnsignedShort(CURRENT_HISTORY_ENTRY_POINTER_ADDRESS);
	if(currentEntryAddress==FIRST_HISTORY_ENTRY_ADDRESS) ret=LAST_HISTORY_ENTRY_ADDRESS;
	else ret=currentEntryAddress-HISTORY_ENTRY_SIZE;
	return ret;
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
