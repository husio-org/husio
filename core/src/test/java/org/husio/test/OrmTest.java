package org.husio.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;

import javax.measure.Measure;
import javax.measure.MeasureFormat;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.husio.Configuration;
import org.husio.HusioApplication;
import org.husio.api.weather.ObservedWeatherMeasure;
import org.husio.api.weather.ObservedWeatherMeasure.MEASUREMENT_TYPE;
import org.husio.api.weather.WeatherUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

public class OrmTest {

    private static final Logger log = LoggerFactory.getLogger(OrmTest.class);
    private static final boolean CREATE_TABLE = true;

    private ConnectionSource con;
    private Dao<TestObject, Date> testDao;

    @BeforeTest
    public void init() throws SQLException {
	Configuration.setupLogSystem();
	String db = HusioApplication.dbConnectionString(System.getProperty("user.home") + "/.husio-test.db");

	log.debug("Using connection string:" + db);
	con = new JdbcConnectionSource(db, "sa", "");
	testDao = DaoManager.createDao(con, TestObject.class);
    }

    @Test(groups = "init")
    public void createDb() throws SQLException {
	log.debug("Creating test table");
	if (CREATE_TABLE)
	    TableUtils.dropTable(con, TestObject.class, true);
	TableUtils.createTableIfNotExists(con, TestObject.class);
    }

    @Test
    public void serializationTest() throws IOException {
	int v = 1;
	Measure<Duration> m = Measure.valueOf(v, SI.SECOND);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(bos);
	oos.writeObject(m);
	log.debug("1.Serialized size for " + m + " is " + bos.size());
    }

    /**
     * Lets see if we can get a serialization schema where 1 second is not
     * stored in 259 bytes! ;)
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void serializationTest2() throws IOException, ClassNotFoundException {
	int v = 1;
	Measure<Duration> m = Measure.valueOf(v, SI.SECOND);

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(bos);
	oos.writeObject(m.toSI().getUnit());
	oos.writeFloat(m.getValue().floatValue());
	oos.flush();

	log.debug("2.Serialized size for " + m + " is " + bos.size());
	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
	Unit unit = (Unit) ois.readObject();
	float f = ois.readFloat();
	Measure m2 = Measure.valueOf(f, unit);

	log.debug("The retrieved object is:" + m2);
    }

    @Test
    public void serializationTest3() throws JsonGenerationException, JsonMappingException, IOException {
	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	int v = 1;
	Measure<Duration> m = Measure.valueOf(v, SI.SECOND);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	mapper.writeValue(bos, m);
	bos.flush();
	log.debug("3.Serialized size for " + m + " is " + bos.size());
	log.debug("JSON:"+bos.toString());
    }

    @Test
    public void serializationTest4() throws JsonGenerationException, JsonMappingException, IOException {
	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
	int v = 1;
	Measure<Dimensionless> m = Measure.valueOf(v, WeatherUnits.PERCENT_WATER);
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	mapper.writeValue(bos, m);
	bos.flush();
	log.debug("4.Serialized size for " + m + " is " + bos.size());
	log.debug("JSON:"+bos.toString());
	//Measure m2=mapper.reader(Measure.class).readValue(bos.toString());
	//log.debug("The read object is now:"+m2);
	//assert m2.equals(m):"Written and read measurements are not eauql";
    }
    
    @Test 
    public void serializationTest5() throws ParseException{
	float v = 1.76f;
	Measure<Angle> m = Measure.valueOf(v, WeatherUnits.DEGREES_FROM_NORTH);
	String out=MeasureFormat.getInstance().format(m);
	log.debug("5.Serialized size for " + m + " is " + out.getBytes().length);
	Measure m2=(Measure) MeasureFormat.getInstance().parseObject(out);
	log.debug("Read value is:"+m2);
	assert m2.approximates(m, 0.00001f):"Written and read measurements are not equal";
    }
    
    @Test
    public void serializationWeatherObservationTest() throws IOException, ClassNotFoundException{
	ObservedWeatherMeasure<Temperature> owm=new ObservedWeatherMeasure<Temperature>(MEASUREMENT_TYPE.TEMPERATURE);
	owm.setMeasure(Measure.valueOf(2, SI.CELSIUS));
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(bos);
	oos.writeObject(owm);
	oos.flush();

	log.debug("Serialized size for OWM " + owm + " is " + bos.size());
	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
	ObservedWeatherMeasure owm2 = (ObservedWeatherMeasure) ois.readObject();
	log.debug("The retrieved object is:" + owm2.getKey()+" "+owm2);
	
    }
    
    @Test(groups = "query", dependsOnGroups = "init", enabled = true)
    public void storeTest() throws SQLException {
	log.debug("Storing Objects");
	TestObject o = new TestObject();
	o.setTimeStamp(new Date());
	o.setComment("This is a cool object!");
	o.setData(new Hashtable<String, String>());
	o.getData().put("hola", "mundo");
	o.getData().put("hello", "world");
	o.getData().put("bye", "bye");
	int m = 1;
	o.setMeasure(Measure.valueOf(m, SI.SECOND));
	this.testDao.create(o);
    }

    @Test(dependsOnGroups = "query")
    public void closeDb() throws SQLException {
	log.debug("Closing test table");
	con.close();
    }

    /**
     * Dao Test Object
     * 
     * @author rafael
     */
    @DatabaseTable(tableName = "test")
    public static class TestObject {

	@DatabaseField(id = true)
	private Date timeStamp;
	@DatabaseField
	private String comment;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private Hashtable<String, String> data;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private Measure<Duration> measure;

	public TestObject() {

	}

	public Date getTimeStamp() {
	    return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
	    this.timeStamp = timeStamp;
	}

	public String getComment() {
	    return comment;
	}

	public void setComment(String comment) {
	    this.comment = comment;
	}

	public Hashtable<String, String> getData() {
	    return data;
	}

	public void setData(Hashtable<String, String> data) {
	    this.data = data;
	}

	public void setMeasure(Measure<Duration> measure) {
	    this.measure = measure;
	}

	public Measure<Duration> getMeasure() {
	    return measure;
	}

    }

}
