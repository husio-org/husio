package org.husio.usb.test;

import java.util.List;

import javax.usb.UsbDevice;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbPort;
import javax.usb.UsbServices;

import org.husio.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class UsbTest {

    private static final Logger log = LoggerFactory.getLogger(UsbTest.class);

    @BeforeTest
    public void init() {
	Configuration.setupLogSystem();
    }

    @Test
    public void usbServicesTest() throws Exception {
	log.debug("Opening USB driver");
	UsbServices services = UsbHostManager.getUsbServices();
	UsbHub root = services.getRootUsbHub();
	List devices = root.getAttachedUsbDevices();
	List ports = root.getUsbPorts();
	log.debug("The number of devices connected is:" + devices.size());
	log.debug("The number of ports is:" + ports.size());
    }

    @Test
    public void usbTreeDisplay() throws Exception {
	log.debug("Opening USB driver");
	UsbServices services = UsbHostManager.getUsbServices();
	UsbHub root = services.getRootUsbHub();
	this.processUsingGetUsbPorts(root, "");
    }

    private void processUsingGetUsbPorts(UsbDevice usbDevice, String prefix) {
	UsbHub usbHub = null;

	if (!usbDevice.isUsbHub()) {
	    log.debug(prefix + "Device");
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

}
