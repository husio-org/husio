package org.husio.api.weather;

import javax.measure.quantity.Quantity;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.Unit;

public class Humidity implements Quantity{
    
    static Unit<Humidity> UNIT=new BaseUnit<Humidity>("%");

}