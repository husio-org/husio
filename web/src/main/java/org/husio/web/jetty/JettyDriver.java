package org.husio.web.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
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
public class JettyDriver implements WebServer {
    
    private static String PORT_CONF_PARAM="org.husio.web.jetty.Driver.port";
    
    private static final Logger log = LoggerFactory.getLogger(JettyDriver.class);
    
    private Server server;

    public JettyDriver(){
	int port=Configuration.getIntProperty(PORT_CONF_PARAM);
	log.info("Initializing Jetty Webserver on port."+port);
	server = new Server(port);
	
	// create the handler for static content in jars
	ResourceHandler staticHandler=new ResourceHandler();
	staticHandler.setDirectoriesListed(true);
	String rb=this.getClass().getClassLoader().getResource("HUSIO_WEBROOT").toString();
	log.debug("Resource base is:"+rb);

	staticHandler.setResourceBase(rb);
	HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { staticHandler, new RequestHandler() });

        server.setHandler(handlers);

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
