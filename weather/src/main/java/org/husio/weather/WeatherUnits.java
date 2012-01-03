package org.husio.weather;

import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

/**
 * This class contains a shortcut to the units used for weather plus some definitions.
 * JSR 275 is used as the API.
 * 
 * @author rafael
 *
 */
public class WeatherUnits {
  
   /** Wind: Meters per second */
   public static final Unit<Velocity> METERS_PER_SECOND = SI.METRES_PER_SECOND;

   /** Wind: Kilometers per hour */
   public static final Unit<Velocity> KILOMETERS_PER_HOUR = NonSI.KILOMETERS_PER_HOUR;
   
   /** Wind: Miles per second */
   public static final Unit<Velocity> MILES_PER_HOUR = NonSI.MILES_PER_HOUR;
   
   /** Wind: Knots */
   public static final Unit<Velocity> KNOT=NonSI.NAUTICAL_MILE.divide(NonSI.HOUR).asType(Velocity.class);

}
