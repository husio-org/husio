package org.husio.weather.service.pws;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class PwsDateFormat extends SimpleDateFormat{
    private static final long serialVersionUID = 1L;

    PwsDateFormat(){
	super("yyyy-MMM-dd HH:mm:ss");
	this.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

}
