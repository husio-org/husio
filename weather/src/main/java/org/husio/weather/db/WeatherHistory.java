package org.husio.weather.db;

import java.sql.SQLException;
import java.util.Date;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.husio.HusioApplication;
import org.husio.api.Initializable;
import org.husio.api.Module;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.evt.WeatherObservationEvent;
import org.husio.ormjson.JsonTypePersister;
import org.husio.weather.json.DBObservedWeatherMeasureDeserializer;
import org.husio.weather.json.DBObservedWeatherMeasureSerializer;
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
	configureDbJson();
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
    
    /**
     * Sets ups ObjectMapping options for efficient json storage in DB.
     */
    public static void configureDbJson(){
	ObjectMapper mapper=JsonTypePersister.getObjectMapper();
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	SimpleModule husioModule = new SimpleModule("HusioDB", new Version(1, 0, 0, null));
	husioModule.addSerializer(new DBObservedWeatherMeasureSerializer());
	husioModule.addDeserializer(ObservedWeatherMeasure.class, new DBObservedWeatherMeasureDeserializer());
	mapper.registerModule(husioModule);
    }

}
