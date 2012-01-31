package org.husio.test.weather;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.husio.Configuration;
import org.husio.HusioApplication;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.TYPE;
import org.husio.api.weather.WeatherObservation;
import org.husio.api.weather.WeatherObservationList;
import org.husio.api.weather.WeatherUnits;
import org.husio.weather.db.WeatherHistory;
import org.husio.weather.json.DBObservedWeatherMeasureDeserializer;
import org.husio.weather.json.DBObservedWeatherMeasureSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class WeatherPersistenceTest {
    
    private static final Logger log = LoggerFactory.getLogger(WeatherPersistenceTest.class);
    private static final boolean CREATE_TABLE = true;
    
    private ConnectionSource con;
    private Dao<WeatherObservation, Date> testDao;
    
    private Date testObservationId=new Date();

    @BeforeTest
    public void init() throws SQLException {
	Configuration.setupLogSystem();
	WeatherHistory.configureDbJson();
	String db = HusioApplication.dbConnectionString(System.getProperty("user.home") + "/.husio-test.db");
	log.debug("Using connection string:" + db);
	con = new JdbcConnectionSource(db, "sa", "");
	testDao = DaoManager.createDao(con, WeatherObservation.class);
    }
    
    /**
     * The main problem we have is that serialization into DB costs 8Kbytes per observation!!
     * I don't want a thinner grained DB schema, that's a lot of work, and there is no need in terms of data access.
     * This tests are helping me find a better serialization protocol, that brings down the storage 
     * to something reasonable while still easy to implement.
     * 
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void jsonSerializationTest() throws JsonGenerationException, JsonMappingException, IOException {
	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	SimpleModule husioModule = new SimpleModule("Husio", new Version(1, 0, 0, null));
	husioModule.addSerializer(new DBObservedWeatherMeasureSerializer());
	husioModule.addDeserializer(ObservedWeatherMeasure.class, new DBObservedWeatherMeasureDeserializer());
	mapper.registerModule(husioModule);

	ObservedWeatherMeasure m=new ObservedWeatherMeasure<Duration>();
	m.setMeasure(Measure.valueOf(1, WeatherUnits.HECTO_PASCAL));
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	mapper.writeValue(bos, m);
	bos.flush();
	log.debug("Serialized size for " + m + " is " + bos.size());
	log.debug("JSON:"+bos.toString());
	ObservedWeatherMeasure m2=mapper.reader(ObservedWeatherMeasure.class).readValue(bos.toString());
	log.debug("The read object is now:"+m2);
    }
    
    
    @Test(groups = "init")
    public void createDb() throws SQLException {
	log.debug("Creating test db");
	if (CREATE_TABLE)
	    TableUtils.dropTable(con, WeatherObservation.class, true);
	TableUtils.createTableIfNotExists(con, WeatherObservation.class);
    }
    
    
    @Test(groups="insert",dependsOnGroups = "init")
    public void insertRecord() throws SQLException{
	WeatherObservation o=new WeatherObservation();
	o.setDuration(Measure.valueOf(5, SI.SECOND));
	o.setTimestamp(testObservationId);
	WeatherObservationList wol=new WeatherObservationList();
	ObservedWeatherMeasure<Temperature> m=new ObservedWeatherMeasure<Temperature>();
	m.setMeasure(Measure.valueOf(-2,SI.CELSIUS));
	m.setType(TYPE.AVERAGE);
	wol.add(m);
	ObservedWeatherMeasure<Pressure> m2=new ObservedWeatherMeasure<Pressure>();
	m2.setMeasure(Measure.valueOf(1000,WeatherUnits.HECTO_PASCAL));
	m2.setType(TYPE.ABSOLUTE);
	wol.add(m2);
	o.setMeasures(wol);
	testDao.create(o);
    }
    
    @Test(groups="query",dependsOnGroups = "insert")
    public void retrieveRecord() throws SQLException{
	WeatherObservation o=testDao.queryForId(testObservationId);
	log.debug("Retrieved obsercation is:"+o);
    }
    
    @Test(dependsOnGroups = "query")
    public void closeDb() throws SQLException {
	log.debug("Closing test db");
	con.close();
    }


}
