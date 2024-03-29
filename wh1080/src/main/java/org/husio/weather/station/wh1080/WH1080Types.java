package org.husio.weather.station.wh1080;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;
import javax.usb.util.UsbUtil;

import org.husio.api.weather.WeatherUnits;

/**
 * Utility base class that maps primitive types from WH 1080 to Java and
 * special unit declarations for measurements to jsr 275.
 * 
 * As a base class it makes code more readable. primitive functions are static so that they can be tested.
 * 
 * @author rafael
 *
 */
//TODO: find a cleaner library that deals with byte operations of different representation platforms.
// that should make it easy to generate ports for other stations.
public abstract class WH1080Types {
    
    /** Useful for scheduling the publisher task */
    public static final Unit<Duration> MILLISECONDS=WeatherUnits.SECOND.divide(1000);
    /** Measurement duration as stored by the station */
    public static final Unit<Duration> DURATION_UNIT=WeatherUnits.SECOND.times(60);
    /** Temperature unit as stored in the station records*/
    public static final Unit<Temperature> TEMPERATURE_UNIT=WeatherUnits.CELSIUS.times(0.1);
    /** Wind speed unit as store in the station records */
    public static final Unit<Velocity> WIND_UNIT=WeatherUnits.METERS_PER_SECOND.times(0.1).times(0.38);
    /** Wind direction unit as store in the station records */
    public static final Unit<Angle> WIND_DIRECTION_UNIT=WeatherUnits.DEGREES_FROM_NORTH.times(22.5);
    /** Absolute Pressure as store in the station records */
    public static final Unit<Pressure> PRESSURE_UNIT=WeatherUnits.HECTO_PASCAL.times(0.1);
    /** Humidity as stored in the station records*/
    public static final Unit<Dimensionless> HUMIDITY_UNIT=WeatherUnits.PERCENT_WATER;
    
    /** Rainfall as stored in the station records*/
    public static final Unit<Length> RAINFALL_UNIT=WeatherUnits.MM_RAINFALL.times(0.3);

    /**
     * returns true is the bit number 0-7 is set in the passed byte.
     * @param b
     * @param num
     * @return
     */
    public static boolean isBitSet(byte b, int num){
	byte mask=(byte) (1<<num);
	return (b & mask)>0;
    }
    
    /**
     * Method added for convenience, and easy code reading.
     * @param address the address of the byte in the memory entry, as stated in the documentation
     * @param bit the bit number from 0-7
     * @return
     */
    protected boolean isBitSet(int address, int bit){
	return isBitSet(data()[address], bit);
    }
    
    /**
     * reads an unsigned byte 
     * @param data
     * @param address
     * @return
     */
    public static int readUnsignedByte(byte[] data, int address){
	return UsbUtil.unsignedInt(data[address]);
    }
    
    /**
     * For convenience and easy code readying.
     * @param address
     * @return
     */
    protected int readUnsignedByte(int address){
	return readUnsignedByte(data(),address);
    }
    
    /**
     * reads an unsigned short, deals with byte order and sign issues.
     * @param address
     * @return
     */
    public static int readUnsignedShort(byte[] data, int address){
	int lo=UsbUtil.unsignedInt(data[address]);
	int hi=UsbUtil.unsignedInt(data[address+1]);
	return hi*256+lo;
    }
    
    /**
     * Convenience method.
     * @param address
     * @return
     */
    protected int readUnsignedShort(int address){
	return readUnsignedShort(data(),address);
    }

    
    /**
     * Reads a signed short stored as two bytes, Big Endian, 
     * Complement 1, and highest value bit for sign.
     * 
     * @param data
     * @param address
     * @return
     */
    public static int readSignedShort(byte[] data,int address){
	int sign=(data[address+1] & 0x80)>0? -1:1; 
	int lo=UsbUtil.unsignedInt(data[address]);
	int hi=UsbUtil.unsignedInt((byte) (data[address+1] & 0x7F));
	return (hi*256+lo)*sign;
    }
    
    protected int readSignedShort(int address){
	return readSignedShort(data(),address);
    }

    /**
     * Returns true if there is a valid metric for the given byte, according to 
     * WH1080 specification. Some entries are marked as invalid, or not existent,
     * where there is a 0xFF content
     * @param address
     * @return
     */
    public boolean isValidByteMetric(int address){
	return data()[address]!=0xFF;
    }
    
    /**
     * retruns 
     * @param address
     * @return
     */
    public boolean isValidShortMetric(int address){
	return isValidByteMetric(address) && isValidByteMetric(address+1);
    }
    
    /**
     * reads a byte and half, where the nibble is most significant. the nibble is storead 
     * either on the half high bits (or lower) of a second byte
     * @param data the array that has the data
     * @param byte address the address where the data is stored
     * @param nybble address where the nyble is stored
     * @param isHighNibbleBits if the 4 nyble bigs are the most significant (or the less significant).
     */
    public static int readByteAndHalf(byte[] data, int byteAddress, int nybbleAddress, boolean isHighNybbleBits){
	int ret=readUnsignedByte(data,byteAddress);
	byte nybble;
	if(isHighNybbleBits) nybble=(byte) ((data[nybbleAddress] & (byte) 0xF0)>>4);
	else nybble=(byte) (data[nybbleAddress] & (byte) 0x0F);
	return ret + (nybble << 8);
    }
    
    public int readByteAndHalf(int byteAddress, int nybbleAddress, boolean isHighNybbleBits){
	return readByteAndHalf(this.data(),byteAddress,nybbleAddress,isHighNybbleBits);
    }

    /**
     * Enables access to WH1080 memory chunk
     * @return
     */
    protected abstract byte[] data();
    
}
