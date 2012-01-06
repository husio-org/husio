package org.husio.test.usb;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbPort;
import javax.usb.UsbServices;
import javax.usb.util.UsbUtil;

import org.husio.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Usb subsystem tests. This includes javax-usb and native bindings.
 * @author rafael
 *
 */
public class UsbTest {

    private static final Logger log = LoggerFactory.getLogger(UsbTest.class);
    
    @BeforeTest
    public void init() {
	Configuration.setupLogSystem();
    }

    @Test
    public void usbServicesTest() throws Exception {
	log.debug("Services Test: Opening USB driver");
	UsbServices services = UsbHostManager.getUsbServices();
	UsbHub root = services.getRootUsbHub();
	List devices = root.getAttachedUsbDevices();
	List ports = root.getUsbPorts();
	log.debug("The number of devices connected is:" + devices.size());
	log.debug("The number of ports is:" + ports.size());
    }

    @Test
    public void usbTreeDisplayPorts() throws Exception {
	log.debug("Display Ports: Opening USB driver");
	UsbServices services = UsbHostManager.getUsbServices();
	UsbHub root = services.getRootUsbHub();
	this.processUsingGetUsbPorts(root, "");
    }

    
    @Test
    public void usbTreeDisplayDevices() throws Exception {
	log.debug("Display Devices: Opening USB driver");
	UsbServices services = UsbHostManager.getUsbServices();
	UsbHub root = services.getRootUsbHub();
	this.processUsingGetAttachedUsbDevices(root, "");
    }
    
    /**
     * Test helper, recursive function, based on ShowTopology.java javax-usb example
     * @param usbDevice
     * @param prefix
     * @throws Exception 
     */
    private void processUsingGetAttachedUsbDevices(UsbDevice usbDevice, String prefix) throws  Exception {
	UsbHub usbHub = null;

	/* If this is not a UsbHub, just display device and return. */
	if (!usbDevice.isUsbHub()) {
	    log.debug(prefix + "Device "+this.describeUsb(usbDevice));
	    return;
	} else {
	    /* We know it's a hub, so cast it. */
	    usbHub = (UsbHub) usbDevice;
	}

	if (usbHub.isRootUsbHub()) {
	    /* This is the virtual root UsbHub. */
	    log.debug(prefix + "Virtual root UsbHub");
	} else {
	    /* This is not the virtual root UsbHub. */
	    log.debug(prefix + "UsbHub");
	}

	/* Now let's process each of this hub's devices. */
	List attachedUsbDevices = usbHub.getAttachedUsbDevices();

	for (int i = 0; i < attachedUsbDevices.size(); i++) {
	    /*
	     * We know all objects in the list are UsbDevice objects; casting is
	     * safe.
	     */
	    UsbDevice device = (UsbDevice) attachedUsbDevices.get(i);

	    /* Recursively handle this device. */
	    processUsingGetAttachedUsbDevices(device, prefix );
	}
    }

    /**
     * Test helper, recursive function, based on ShowTopology.java javax-usb example
     * @param usbDevice
     * @param prefix
     */
    private void processUsingGetUsbPorts(UsbDevice usbDevice, String prefix) throws Exception{
	UsbHub usbHub = null;

	if (!usbDevice.isUsbHub()) {
	    log.debug(prefix + "Device "+this.describeUsb(usbDevice));
	    return;
	} else {
	    /* We know it's a hub, so cast it. */
	    usbHub = (UsbHub) usbDevice;
	}

	if (usbHub.isRootUsbHub()) {
	    /* This is the virtual root UsbHub. */
	    log.debug(prefix + "Virtual root UsbHub");
	} else {
	    /* This is not the virtual root UsbHub. */
	    log.debug(prefix + "UsbHub");
	}

	/* Now let's process each of this hub's ports. */
	List usbPorts = usbHub.getUsbPorts();

	for (int i = 0; i < usbPorts.size(); i++) {
	    /*
	     * We know all objects in the list are UsbPort objects; casting is
	     * safe.
	     */
	    UsbPort port = (UsbPort) usbPorts.get(i);

	    /* If this doesn't have a device attached, just process the port. */
	    if (!port.isUsbDeviceAttached()) {
		log.debug(prefix + "UsbPort");
		continue;
	    } else {
		/* There is a device attached, so we'll process it. */
		processUsingGetUsbPorts(port.getUsbDevice(), prefix);
	    }
	}
    }
    
    private String describeUsb(UsbDevice d) throws Exception{
	UsbDeviceDescriptor dd=d.getUsbDeviceDescriptor();
	String vendor=UsbUtil.toHexString(dd.idVendor());
	String product=UsbUtil.toHexString(dd.idProduct());
	String manufacturer=d.getManufacturerString()!=null? d.getManufacturerString() : "Unknown"; 
	String productDesc=d.getProductString()!=null? d.getProductString() : "Unknown";
	String serial=d.getSerialNumberString()!=null? d.getSerialNumberString() : "Unknown";
	return "["+vendor+":"+product+"] "+manufacturer+" - "+productDesc+ " - S/N "+serial;
    }

}
