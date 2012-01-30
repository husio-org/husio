package org.husio.test;

import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;

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
	TableUtils.createTable(con, TestObject.class);
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
	this.testDao.create(o);
    }
    
    @Test(dependsOnGroups="query")
    public void dropDb() throws SQLException{
	log.debug("Dropping test table");
	TableUtils.dropTable(con, TestObject.class, false);
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
	
    }

}
