package org.husio.api.weather;


import javax.measure.quantity.Angle;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

/**
 * This class contains a shortcut to SI and NonSI of the units used for weather plus some extra definitions.
 * JSR 275 is used as the API.
 * 
 * @author rafael
 *
 */
public class WeatherUnits {
   
    /** Duration for samples */
   public static final Unit<Duration> SECOND= SI.SECOND;
    
   /** Temperature: expressed in Celsius */ 
   public static final Unit<Temperature> CELSIUS = SI.CELSIUS;
   
   /** Temperature: expressed in Fahrenheit */
   public static final Unit<Temperature> FAHRENHEIT=NonSI.FAHRENHEIT;
  
   /** Wind: Meters per second */
   public static final Unit<Velocity> METERS_PER_SECOND = SI.METRES_PER_SECOND;

   /** Wind: Kilometers per hour */
   public static final Unit<Velocity> KILOMETERS_PER_HOUR = NonSI.KILOMETERS_PER_HOUR;
   
   /** Wind: Miles per second */
   public static final Unit<Velocity> MILES_PER_HOUR = NonSI.MILES_PER_HOUR;
   
   /** Wind: Knots */
   public static final Unit<Velocity> KNOT=NonSI.KNOT;
   
   /** Wind Direction: Degrees */
   public static final Unit<Angle> DEGREES_FROM_NORTH= NonSI.DEGREE_ANGLE;
   
   /** Absolute Pressure: hpa*/
   public static Unit<Pressure> HECTO_PASCAL=NonSI.ATMOSPHERE.divide(1013.25);
   
   /** Absolute Pressure: Milliliters of mercury */
   public static Unit<Pressure> MILLIMETER_OF_MERCURY=NonSI.MILLIMETER_OF_MERCURY;
   
   /** Absolute Pressure: Inches of mercury */
   public static Unit<Pressure> INCH_OF_MERCURY=NonSI.INCH_OF_MERCURY;
   
   /** Humidity: Inches of mercury */
   public static Unit<Humidity> PERCENT_WATER=Humidity.UNIT;

}
