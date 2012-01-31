package org.husio.api.weather;

import java.io.Serializable;
import java.util.Date;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.husio.ormjson.JsonTypePersister;

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
    
    @DatabaseField(dataType=DataType.LONG)
    private long durationInSeconds;
    
    /**
     * Please, note that the weather module will also setup
     * JSON artifacts that will tune how the JSON is serialized/deserialized
     */
    @DatabaseField(persisterClass=JsonTypePersister.class)
    private WeatherObservationList measures;
    
    transient private WeatherObservationTable observationTable;
    
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
	return Measure.valueOf(this.durationInSeconds, SI.SECOND);
    }
    
    public void setDuration(Measure<Duration> duration) {
	this.durationInSeconds = duration.longValue(SI.SECOND);
    }

    public WeatherObservationTable getMeasuresAsTable(){
	if(this.observationTable!=null) return this.observationTable;
	else return this.observationTable=new WeatherObservationTable(this.measures);
    }

    public void setMeasures(WeatherObservationList measures) {
	this.measures = measures;
    }
    
    /**
     * The collection of measurements that where collected.
     */
    public WeatherObservationList getMeasures() {
	return measures;
    }
    

}
