package org.husio.weather.station.wh1080;


/**
 * WH1080 EEPROM DATA DEFINITION
 * 
 * 1) History Data is saved at the address between 00100H and 01FFFFH for total of 8176 sets of records. If I2C bus is busy during data transfer, wait until the bus is free.
 * 2) One set history data take 16bytes of EEPROM space, and it is organized as follow:
 * 
 * Sampling time --- the time interval between two data sets in minutes
 * IN RH --- HEX value，one byte in the range of 1%~99%.if not valid，0FFH will be saved instead；
 * IN T --- HEX value，two bytes（-40.0℃~60.0℃），FFFFH will be used when not valid data available，bit7 of the MSB is sign bit:0 –positive sign，1 –negative sign.
 * OUT RH --- HEX value, one byte for range 1% - 99%. If not valid data available, save 0FFH instead.
 * OUT T --- HEX value，two bytes（-40.0℃~60.0℃），FFFFH will be used when not valid data available，bit7 of the MSB is sign bit:0 –positive sign，1 –negative sign.
 * Absolute Pressure --- HEX value, two bytes for the range of 920.0 – 1080.0 Hpa. If not valid data, save FFFFH instead.
 * Average Wind Speed Low Byte Value --- HEX value, one and half byte for range of 0-50.0m/s. If no valid data available, FFH will be saved instead.
 * Gust Wind Speed Low Byte Value --- HEX value, one and half byte for range of 0-50.0m/s. If no valid data available, FFH will be saved instead.
 * Average and Gust High Nibble Value --- the high nibble is for gust wind speed, the low nibble is for average wind speed.
 * remark: the above saved value is from the wind speed transducer counter value, multiplying 0.38 factor to get real wind speed value.
 * Wind Direction --- HEX value, one byte. 0 for N, 4 for E , 8 for S, 12 for W. bit 7 set to one for not valid wind direction.
 * Total Rain --- HEX value, two bytes. Current rain counter value.
 * remark: the above saved value is from the rain transducer counter value, multiplying 0.3 factor to get real total rainfall value.
 * Status byte --- HEX value, one byte.
 * Bit6 =0 rain counter value is valid, =1 rain counter value is not valid.
 * Bit7 =0 no overflow rain counter value happened. =1 for rain counter value overflow happened.
 * 
 * 
 * @author rafael
 *
 */
public class HistoryDataEntry {
    
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

}
