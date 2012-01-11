package org.husio.weather.service;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class UtcDateFormat extends SimpleDateFormat{
    private static final long serialVersionUID = 1L;

    public UtcDateFormat(){
	super("yyyy-MMM-dd HH:mm:ss");
	this.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

}
