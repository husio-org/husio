package org.husio.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.husio.Configuration;
import org.husio.HusioApplication;
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
    private static final boolean CREATE_TABLE=true;
    
    ConnectionSource con;
    Dao<TestObject,Date> testDao;
    
    @BeforeTest
    public void init() throws SQLException {
	Configuration.setupLogSystem();
	String db=HusioApplication.dbConnectionString(System.getProperty("user.home")+"/.husio-test.db");

	log.debug("Using connection string:"+db);
	con=new JdbcConnectionSource(db,"sa","");
	testDao=DaoManager.createDao(con, TestObject.class);
    }
    
    @Test(groups="init")
    public void createDb() throws SQLException{
	log.debug("Creating test table");
	if(CREATE_TABLE) TableUtils.dropTable(con, TestObject.class, true);
	TableUtils.createTableIfNotExists(con, TestObject.class);
    }
    
    @Test
    public void serializationTest() throws IOException{
	int v=1;
	Measure<Duration> m=Measure.valueOf(v, SI.SECOND);
	ByteArrayOutputStream bos=new ByteArrayOutputStream();
	ObjectOutputStream oos=new ObjectOutputStream(bos);
	oos.writeObject(m);
	log.debug("Serialized size for "+m+" is "+bos.size());
    }
    
    @Test(groups="query",dependsOnGroups="init",enabled=true)
    public void storeTest() throws SQLException{
	log.debug("Storing Objects");
	TestObject o=new TestObject();
	o.setTimeStamp(new Date());
	o.setComment("This is a cool object!");
	o.setData(new Hashtable<String,String>());
	o.getData().put("hola", "mundo");
	o.getData().put("hello", "world");
	o.getData().put("bye", "bye");
	int m=1;
	o.setMeasure(Measure.valueOf(m, SI.SECOND));
	this.testDao.create(o);
    }
    
    @Test(dependsOnGroups="query")
    public void closeDb() throws SQLException{
	log.debug("Closing test table");
	con.close();
    }
    
    /**
     * Dao Test Object
     * @author rafael
     */
    @DatabaseTable(tableName = "test")
    public static class TestObject{
	
	@DatabaseField(id = true)
	private Date timeStamp;
	@DatabaseField
	private String comment;
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	private Hashtable<String,String> data;
	@DatabaseField(dataType=DataType.SERIALIZABLE)
	private Measure<Duration> measure;
	
	public TestObject(){
	    
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
