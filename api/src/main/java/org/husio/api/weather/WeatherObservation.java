package org.husio.api.weather;

import java.io.Serializable;
import java.util.Date;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A weather observation. Normally the weather station will collect metrics for a certain period of time and
 * release a historic weather entry.
 * 
 * @author rafael
 *
 */
@DatabaseTable(tableName = "observations")
public class WeatherObservation implements Serializable{
    
    private static final long serialVersionUID = 1L;

    @DatabaseField(id = true)
    private Date timeStamp;
    
    @DatabaseField(dataType=DataType.SERIALIZABLE, width=256)
    private Measure<Duration> duration;
    
    @DatabaseField(dataType=DataType.SERIALIZABLE,width=2048)
    private WeatherObservationTable observationTable;
    
    public WeatherObservation(){
    }
    
    /**
     * The timestamp for this observation.
     */
    public Date getTimestamp(){
	return this.timeStamp;
    }
    
    public void setTimestamp(Date t){
	this.timeStamp=t;
    }
    
    /**
     * the duration covered by this observation.
     */
    public Measure<Duration> getDuration(){
	return this.duration;
    }
    
    public void setDuration(Measure<Duration> duration) {
	this.duration = duration;
    }

    /**
     * The collection of measurements that where collected.
     */
    public WeatherObservationTable getMeasures(){
	return this.observationTable;
    }
    
    public void setMeasures(WeatherObservationTable t){
	this.observationTable=t;
    }

}
