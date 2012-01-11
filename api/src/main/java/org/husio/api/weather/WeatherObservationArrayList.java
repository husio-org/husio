package org.husio.api.weather;

import java.util.ArrayList;

import javax.measure.quantity.Quantity;

public class WeatherObservationArrayList  extends ArrayList<ObservedWeatherMeasure<? extends Quantity>> implements WeatherObservationList{
    private static final long serialVersionUID = 1L;

}
