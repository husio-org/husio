package org.husio.weather.station.wh1080;

/**
 * The fixed memory block. 256 bytes with station settings and pointer to current record.
 * 
 * @author rafael
 *
 */
public class FixedMemoryBlock {
    
    /**
     * the actual memory as mapped from the station
     */
    private byte[] data=new byte[256];
    
    /**
     * The station this memory blcok belongs to
     */
    private WH1080 station;
    
    FixedMemoryBlock(WH1080 s) throws Exception{
	this.station=s;
	
	for (int i=0; i<256 ; i+=32){
	    s.readAddress(i, data, i);
	}
    }

}
