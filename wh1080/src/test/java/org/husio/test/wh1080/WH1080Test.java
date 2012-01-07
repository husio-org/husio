package org.husio.test.wh1080;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;

import org.apache.commons.io.EndianUtils;
import org.husio.Configuration;
import org.husio.usb.UsbUtils;
import org.husio.weather.api.WeatherUnits;
import org.husio.weather.station.wh1080.HistoryDataEntry;
import org.husio.weather.station.wh1080.WH1080;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	UsbDevice device=UsbUtils.findDevice(WH1080.USB_VENDOR_ID, WH1080.USB_PRODUCT_ID);
	if(device!=null) log.debug("The device was found!!");
	else log.debug("The device was not found");
    }
    
    @Test(enabled=false)
    public void findStationDeviceInfo() throws Exception{
	log.debug("Finding Information about Station WH1080");
	UsbDevice device=UsbUtils.findDevice(WH1080.USB_VENDOR_ID, WH1080.USB_PRODUCT_ID);
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
    
    @Test(enabled=false)
    public void readDevideData() throws Exception{
	WH1080 station=new WH1080();
	station.start();
	HistoryDataEntry data=station.readLastDataEntry();
	log.debug("The read indoor temperature is: "+data.getIndoorTemperature().to(WeatherUnits.CELSIUS));
	log.debug("The read outdoor temperature is: "+data.getOutdoorTemperature().to(WeatherUnits.CELSIUS));
	station.stop();
    }
    
    /**
     * Not that type conversion will break, but this is test driven development ;)
     */
    @Test(enabled=true)
    public void typeUnsignedShortConversionTest(){
	log.debug("Testing type conversion");
	// sample data
	byte[] data={
		(byte) 0xec,
		(byte) 0x00
	};
	short val=EndianUtils.readSwappedUnsignedShort(data, 0);
	log.debug("The value of "+UsbUtil.toHexString(" 0x", data)+" decoded as: "+val);
	assert val==236: "conversion failed";	
    }
    
    /**
     * Not that type conversion will break, but this is test driven development ;)
     */
    @Test(enabled=true)
    public void typeSignedShortConversionTest(){
	log.debug("Testing type conversion");
	// sample data
	byte[] data={
		(byte) 0x17,
		(byte) 0x80
	};
	short val=EndianUtils.readSwappedShort(data, 0);
	log.debug("The value of "+UsbUtil.toHexString(" 0x", data)+" decoded as: "+val);
	assert val==236: "conversion failed";	
    }
    
}
