package org.husio.weather.station.wh1080;

import javax.usb.util.UsbUtil;

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
    
    /**
     * returns true is the bit number 0-7 is set in the passed byte.
     * @param b
     * @param num
     * @return
     */
    public static boolean isBitSet(byte b, int num){
	byte mask=(byte) (2*num);
	return (b & mask)>0;
    }
    
    /**
     * Method added for convenience, and easy code reading.
     * @param address the address of the byte in the memory entry, as stated in the documentation
     * @param bit the bit number from 0-7
     * @return
     */
    protected boolean isBitSet(int address, int bit){
	return this.isBitSet(data()[address], bit);
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
    
    protected int readUnsignedShort(int address){
	return readUnsignedShort(data(),address);
    }

    
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
     * Enables access to WH1080 memory chunk
     * @return
     */
    protected abstract byte[] data();
    
}
