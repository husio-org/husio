package org.husio.api.weather;

import java.util.ArrayList;

import javax.measure.quantity.Quantity;

public class WeatherMeasureArrayList  extends ArrayList<CollectedWeatherMeasure<? extends Quantity>> implements WeatherMeasureList{
    private static final long serialVersionUID = 1L;

}
