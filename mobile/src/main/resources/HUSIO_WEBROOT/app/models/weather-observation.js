/**
 * The Weather Observation Record Model
 */
Husio.models.WeatherObservation=Ext.regModel('WeatherObservation', {
    fields: [
        {name: 'mtype', type: 'string'},
        {name: 'environment', type: 'string'},
        {name: 'variant', type: 'string'},
        {name: 'measuredValue', type: 'float'},
        {name: 'measuredUnit', type: 'string'},
        {name: 'validMetric', type: 'boolean'}
    ]
});