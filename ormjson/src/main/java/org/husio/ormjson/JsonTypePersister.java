package org.husio.ormjson;

import java.io.IOException;
import java.sql.SQLException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;

/**
 * This is, basically, a long string type.
 * 
 * @author rafael
 * 
 */
public class JsonTypePersister extends StringType {
    
    private static ObjectMapper mapper = new ObjectMapper();
    
    private static JsonTypePersister singleton=new JsonTypePersister();
    
    public static JsonTypePersister getSingleton(){
	return singleton;
    }

    public static ObjectMapper getObjectMapper(){
	return mapper;
    }
    
    public JsonTypePersister() {
	super(SqlType.LONG_STRING, new Class<?>[0]);
    }

    @Override
    public Object resultToJava(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
	try {
	    Class<?> c=fieldType.getType();
	    String s=results.getString(columnPos);
	    return mapper.reader(fieldType.getType()).readValue(results.getString(columnPos));
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    throw new SQLException("Could not deserialize from json", e);
	}
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
	try{
	    return mapper.writeValueAsString(javaObject);
	}
	catch(Exception e){
	    throw new SQLException("Could not generate JSON for field storage",e);
	}
    }

    @Override
    public boolean isAppropriateId() {
	return false;
    }

    @Override
    public int getDefaultWidth() {
	return 0;
    }

}
