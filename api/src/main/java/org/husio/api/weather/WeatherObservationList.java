package org.husio.api.weather;

import java.io.Serializable;
import java.util.ArrayList;

import javax.measure.quantity.Quantity;

public class WeatherObservationList extends ArrayList<ObservedWeatherMeasure<? extends Quantity>> implements Serializable{

    private static final long serialVersionUID = 1L;

}
