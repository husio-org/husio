package org.husio.test.wh1080;

import java.util.List;

import javax.measure.quantity.Quantity;
import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;

import org.husio.Configuration;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherObservationTable;
import org.husio.usb.UsbUtils;
import org.husio.weather.station.wh1080.WH1080Driver;
import org.husio.weather.station.wh1080.WH1080Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class WH1080Test {
    
    private static final Logger log = LoggerFactory.getLogger(WH1080Test.class);

    @BeforeTest
    public void init() {
	Configuration.setupLogSystem();
    }
    
    @Test(enabled=false)
    public void findStationDevice() throws Exception{
	log.debug("Finding Station WH1080 in virtual USB device hub");
	UsbDevice device=UsbUtils.findDevice(WH1080Driver.USB_VENDOR_ID, WH1080Driver.USB_PRODUCT_ID);
	if(device!=null) log.debug("The device was found!!");
	else log.debug("The device was not found");
    }
    
    @Test(enabled=false)
    public void findStationDeviceInfo() throws Exception{
	log.debug("Finding Information about Station WH1080");
	UsbDevice device=UsbUtils.findDevice(WH1080Driver.USB_VENDOR_ID, WH1080Driver.USB_PRODUCT_ID);
	assert device!=null : "Device not found";
	log.debug("Device configured:"+device.isConfigured());
	List confs=device.getUsbConfigurations();
	log.debug("Number of configurations active:"+confs.size());
	UsbConfiguration conf=device.getActiveUsbConfiguration();
	List interfaces=conf.getUsbInterfaces();
	log.debug("Number of interfaces in active configuration:"+interfaces.size());
	UsbInterface ui=(UsbInterface) interfaces.get(0);
	log.debug("The Interface is active:"+ui.isActive());
	log.debug("The Interface is claimed:"+ui.isClaimed());
	log.debug("The Interface has num settings:"+ui.getNumSettings());
	log.debug("The Interface class:"+ui.getUsbInterfaceDescriptor().bInterfaceClass());
	log.debug("The Interface sub-class:"+ui.getUsbInterfaceDescriptor().bInterfaceSubClass());
	log.debug("The Interface protocol:"+ui.getUsbInterfaceDescriptor().bInterfaceProtocol());

	List endpoints=ui.getUsbEndpoints();
	log.debug("Number of endpoints are:"+endpoints.size());
	UsbEndpoint uep=(UsbEndpoint) endpoints.get(0);
	log.debug("The enpoint type is:"+uep.getType());
	log.debug("The enpoint direction is:"+uep.getDirection());
	log.debug("The enpoint max packet size is:"+uep.getUsbEndpointDescriptor().wMaxPacketSize());

	UsbPipe pipe=uep.getUsbPipe();
	log.debug("The pipe is open:" +pipe.isOpen());
	log.debug("The pipe is active:"+pipe.isActive());
    }
    
    @Test(enabled=true)
    public synchronized void readDevideData() throws Exception{
	WH1080Driver station=new WH1080Driver();
	station.start();
	WeatherObservation data=station.readLastDataEntry();
	log.debug("Collected Observation is:"+data);
	station.stop();
    }
    
    @Test(enabled=true)
    public void testSignedShortTypeCoversion(){
	byte[] data={
		(byte) 0xFF,
		(byte) 0xFF
	};
	int val=WH1080Types.readSignedShort(data, 0);
	log.debug("The value for signed short "+UsbUtil.toHexString(" 0x", data)+" is: "+val);
	assert val==-32767:"Conversion Failed";
    }

    @Test(enabled=true)
    public void testSignedShortTypeCoversion2(){
	byte[] data={
		(byte) 0xFF,
		(byte) 0x7F
	};
	int val=WH1080Types.readSignedShort(data, 0);
	log.debug("The value for signed short "+UsbUtil.toHexString(" 0x", data)+" is: "+val);
	assert val==32767:"Conversion Failed";
    }

    @Test(enabled=true)
    public void testSignedShortTypeCoversion3(){
	byte[] data={
		(byte) 0x01,
		(byte) 0x80
	};
	int val=WH1080Types.readSignedShort(data, 0);
	log.debug("The value for signed short "+UsbUtil.toHexString(" 0x", data)+" is: "+val);
	assert val==-1:"Conversion Failed";
    }
    
    @Test(enabled=true)
    public void testUnignedShortTypeCoversion(){
	byte[] data={
		(byte) 0xFF,
		(byte) 0xFF
	};
	int val=WH1080Types.readUnsignedShort(data, 0);
	log.debug("The value for unsigned short "+UsbUtil.toHexString(" 0x", data)+" is: "+val);
	assert val==65535:"Conversion Failed";
    }    
    
    @Test(enabled=true)
    public void testUnignedShortTypeCoversion2(){
	byte[] data={
		(byte) 0x01,
		(byte) 0x00
	};
	int val=WH1080Types.readUnsignedShort(data, 0);
	log.debug("The value for unsigned short "+UsbUtil.toHexString(" 0x", data)+" is: "+val);
	assert val==1:"Conversion Failed";
    }    
    
    @Test(enabled=true)
    public void testUnignedShortTypeCoversion3(){
	byte[] data={
		(byte) 0x00,
		(byte) 0x80
	};
	int val=WH1080Types.readUnsignedShort(data, 0);
	log.debug("The value for unsigned short "+UsbUtil.toHexString(" 0x", data)+" is: "+val);
	assert val==32768:"Conversion Failed";
    }    
    
    
    @Test(enabled=true)
    public void testNybbleHigh(){
	byte[] data={
		(byte) 0xAA,
		(byte) 0x12
	};
	int result=WH1080Types.readByteAndHalf(data, 0, 1, true);
	log.debug("The result is:"+UsbUtil.toHexString(result));
	Assert.assertEquals(UsbUtil.toHexString(result), "000001aa");
    }
    
    @Test(enabled=true)
    public void testNybbleHigh2(){
	byte[] data={
		(byte) 0xFA,
		(byte) 0x12
	};
	int result=WH1080Types.readByteAndHalf(data, 0, 1, true);
	log.debug("The result is:"+UsbUtil.toHexString(result));
	Assert.assertEquals(UsbUtil.toHexString(result), "000001fa");
    }
    
    @Test(enabled=true)
    public void testNybbleLow(){
	byte[] data={
		(byte) 0xAA,
		(byte) 0x12
	};
	int result=WH1080Types.readByteAndHalf(data, 0, 1, false);
	log.debug("The result is:"+UsbUtil.toHexString(result));
	Assert.assertEquals(UsbUtil.toHexString(result), "000002aa");
    }
    
    @Test(enabled=true)
    public void testNybbleLow2(){
	byte[] data={
		(byte) 0xFA,
		(byte) 0x12
	};
	int result=WH1080Types.readByteAndHalf(data, 0, 1, false);
	log.debug("The result is:"+UsbUtil.toHexString(result));
	Assert.assertEquals(UsbUtil.toHexString(result), "000002fa");
    }
    
}
