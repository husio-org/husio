package org.husio.test.weather;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.husio.Configuration;
import org.husio.weather.service.HTTPWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class HTTPWeatherServiceTest {

    private static final Logger log = LoggerFactory.getLogger(HTTPWeatherServiceTest.class);
    
    // simulated service to test
    private TestWeatherServer ws = new TestWeatherServer();

    @BeforeTest
    public void init() throws SQLException {
	Configuration.setupLogSystem();
    }

    @Test(invocationCount=20)
    public void testWorkingHTTPUrl() throws Exception {
	log.debug("Testing a simulated weather service with working url");
	ws.testWorkingUrl();
    }
    
    @Test(invocationCount=20)
    public void testNonWorkingHTTPUrl() throws Exception {
	log.debug("Testing a simulated weather service with non working url");
	ws.testNonWorkingUrl();
    }
    
    @Test(invocationCount=20)
    public void testMixedWorkingHTTPUrl() throws Exception {
	log.debug("Testing a simulated weather service with both url types");
	ws.testWorkingUrl();
	ws.testNonWorkingUrl();
    }


    // This is a fake service, just to test the underlying HTTP
    // infrastructure
    class TestWeatherServer extends HTTPWeatherService {
	
	int timeoutms=300;
	
	TestWeatherServer(){
	    HttpParams params = httpclient.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, 300);
	    HttpConnectionParams.setSoTimeout(params, 300);
	}

	public void testWorkingUrl() throws Exception {
	    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
	    qparams.add(new BasicNameValuePair("query", "hola"));
	    URI uri = URIUtils.createURI("http", "www.google.com", -1, "/", URLEncodedUtils.format(qparams, "UTF-8"), null);
	    this.submitWeatherInfo(uri);
	}
	
	public void testNonWorkingUrl() throws Exception {
	    List<NameValuePair> qparams = new ArrayList<NameValuePair>();
	    qparams.add(new BasicNameValuePair("query", "hola"));
	    URI uri = URIUtils.createURI("http", "www.google.fail", -1, "/", URLEncodedUtils.format(qparams, "UTF-8"), null);
	    try{
		this.submitWeatherInfo(uri);
	    }
	    catch(Exception e){} // we just assume the exception is being handled
	}

	@Override
	public String getUnkownValue() {
	    return null;
	}

    }

}
