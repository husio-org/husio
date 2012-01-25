package org.husio.weather;

import java.util.HashSet;
import java.util.Set;

import javax.measure.MeasureFormat;
import javax.measure.unit.SystemOfUnits;
import javax.measure.unit.Unit;

import org.husio.api.weather.WeatherUnits;

/**
 * The Preferred user metrics. Managed by Configuration.
 * @author rafael
 *
 */
public class UserMetricSystem extends SystemOfUnits{
    
    private Set<Unit<?>> userUnits=new HashSet<Unit<?>>();
    
    public UserMetricSystem(){
	//TODO: Take from configutation
	userUnits.add(WeatherUnits.CELSIUS);
	userUnits.add(WeatherUnits.KNOT);
	userUnits.add(WeatherUnits.HECTO_PASCAL);
	userUnits.add(WeatherUnits.PERCENT_WATER);
	userUnits.add(WeatherUnits.DEGREES_FROM_NORTH);
    }

    @Override
    public Set<Unit<?>> getUnits() {
	return userUnits;
    }
    
    /**
     * Tries to return any of the user preferred units, if not returns
     * the SI
     * 
     * @param u
     * @return
     */
    public Unit<?> getPreferredUnit(Unit<?> u){
	for (Unit<?> i: userUnits){
	    if (u.isCompatible(i)) return i;
	}
	return u.toSI();
    }

}
