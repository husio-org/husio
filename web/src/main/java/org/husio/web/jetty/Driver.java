package org.husio.web.jetty;

import org.eclipse.jetty.server.Server;
import org.husio.Configuration;
import org.husio.api.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The web server connector. this intends to be more of an Restful HTTP interface than the actual
 * frontend. Rich frontends will access this server.
 * 
 * @author rafael
 *
 */
public class Driver implements WebServer {
    
    private static String PORT_CONF_PARAM="org.husio.web.jetty.Driver.port";
    
    private static final Logger log = LoggerFactory.getLogger(Driver.class);
    
    private Server server;

    public Driver(){
	int port=Configuration.getIntProperty(PORT_CONF_PARAM);
	log.info("Initializing Jetty Webserver on port."+port);
	server = new Server(port);
	server.setHandler(new RequestHandler());
    }
    
    @Override
    public void start() throws Exception {
	log.info("Starting Jetty Webserver");
	server.start();
    }

    @Override
    public void stop() throws Exception {
	log.info("Stopping Jetty Webserver");
	server.stop();
    }

    @Override
    public MODULE_TYPE getModuleType() {
	return MODULE_TYPE.WEB_SERVER;
    }

}
