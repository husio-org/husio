package org.husio.web.jetty;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.ENVIRONMENT;
import org.husio.api.weather.ObservedWeatherMeasure.MEASUREMENT_TYPE;
import org.husio.api.weather.ObservedWeatherMeasure.VARIANT;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherObservationDataSeries;
import org.husio.api.weather.WeatherObservationDataSeriesSpecification;
import org.husio.api.weather.WeatherUnits;
import org.husio.weather.chart.WeatherChartSpecs;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class WeatherHistoryRequestHandler extends HusioRequestHandler {

    public static enum HQUERY_TERM {
	DAY, WEEK, MONTH, QUARTER, YEAR, ALL
    };

    private static final Logger log = LoggerFactory.getLogger(WeatherHistoryRequestHandler.class);

    // the weather observation dao
    private Dao<WeatherObservation, Date> observationDao;

    public WeatherHistoryRequestHandler() throws Exception {
	observationDao = DaoManager.createDao(con, WeatherObservation.class);
	List<WeatherObservationDataSeriesSpecification> chartSpec=new ArrayList<WeatherObservationDataSeriesSpecification>();	
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	if (target.startsWith("/rest/whistory"))
	    this.handleHistory(target, baseRequest, request, response);
    }

    private void handleHistory(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException {
	HQUERY_TERM term = HQUERY_TERM.ALL;
	try {
	    DateTime dt=new DateTime();
	    QueryBuilder <WeatherObservation,Date> qb=observationDao.queryBuilder();
	    PreparedQuery<WeatherObservation> query=qb.where().ge("timeStamp",dt.minusDays(1).toDate()).prepare();
	    List<WeatherObservation> observations = observationDao.query(query);
	    response.setContentType("application/json");
	    WeatherObservationDataSeries chart=WeatherObservationDataSeries.createFrom(observations, WeatherChartSpecs.outdorHistory());
	    mapper.writeValue(response.getWriter(), chart);
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (Exception e) {
	    log.error("Could not serve weather history", e);
	    throw new ServletException("Could not serve weather history", e);
	}

	((Request) request).setHandled(true);
    }
    

}
