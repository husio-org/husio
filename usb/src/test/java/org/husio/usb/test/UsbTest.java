package org.husio.usb.test;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbServices;

import org.husio.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class UsbTest {
    
    private static final Logger log = LoggerFactory.getLogger(UsbTest.class);
    
    @BeforeTest
    public void init(){
	Configuration.setupLogSystem();
    }
    
    @Test
    public void usbServicesTest() throws Exception {
	log.debug("Opening USB driver");
	UsbServices services = UsbHostManager.getUsbServices();
	UsbHub root=services.getRootUsbHub();
	List devices=root.getAttachedUsbDevices();
	List ports=root.getUsbPorts();
	log.debug("The number of devices connected is:"+devices.size());
	log.debug("The number of ports is:"+ports.size());
    }

}
