package org.husio.weather.history;

import java.sql.SQLException;
import java.util.Date;

import org.husio.HusioApplication;
import org.husio.api.Initializable;
import org.husio.api.Module;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adamtaft.eb.EventBusService;
import com.adamtaft.eb.EventHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class WeatherHistory implements Module, Initializable {
    
    private static final Logger log = LoggerFactory.getLogger(WeatherHistory.class);
    private ConnectionSource con;
    // instantiate the dao
    Dao<WeatherObservation, Date> observationDao;
        
    @EventHandler
    public void handleWeatherObservation(WeatherObservationEvent weather){
	log.debug("Storing weather observation event:"+weather.getWeatherObservation());
	try {
	    this.observationDao.create(weather.getWeatherObservation());
	} catch (SQLException e) {
	    log.error("Could not create history record", e);
	}
    }

    @Override
    public void start() throws Exception {
	con=HusioApplication.getDbConnection();
	observationDao=DaoManager.createDao(con, WeatherObservation.class);
	TableUtils.createTableIfNotExists(con, WeatherObservation.class);
	log.debug("Created Weather History Service");
	EventBusService.subscribe(this);	
    }

    @Override
    public void stop() throws Exception {
	EventBusService.unsubscribe(this);
	con.close();
    }

}
