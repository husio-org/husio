package org.husio.web.jetty;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.husio.api.weather.WeatherObservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class WeatherHistoryRequestHandler extends HusioRequestHandler {

    public static enum HQUERY_TERM {
	DAY, WEEK, MONTH, QUARTER, YEAR, ALL
    };

    private static final Logger log = LoggerFactory.getLogger(WeatherHistoryRequestHandler.class);

    // the weather observation dao
    private Dao<WeatherObservation, Date> observationDao;

    public WeatherHistoryRequestHandler() throws Exception {
	observationDao = DaoManager.createDao(con, WeatherObservation.class);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	if (target.startsWith("/rest/whistory"))
	    this.handleHistory(target, baseRequest, request, response);
    }

    private void handleHistory(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws ServletException {
	HQUERY_TERM term = HQUERY_TERM.ALL;
	try {
	    List<WeatherObservation> result = observationDao.queryForAll();
	    response.setContentType("application/json");
	    mapper.writeValue(response.getWriter(), result);
	    response.setStatus(HttpServletResponse.SC_OK);
	} catch (Exception e) {
	    log.error("Could not serve weather history", e);
	    throw new ServletException("Could not serve weather history", e);
	}

	((Request) request).setHandled(true);
    }

}
