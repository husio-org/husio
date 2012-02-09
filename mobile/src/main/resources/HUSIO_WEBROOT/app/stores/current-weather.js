/**
 * The Current Weather Store
 */
Husio.stores.CurrentWeatherStore=Ext.regStore('CurrentWeatherStore',{
    model: 'WeatherObservation',
    proxy: {
        type: 'rest',
        url : '/rest/weather',
        reader: {
            type: 'json',
            root: 'weatherObservation.measures'
        }
    },
    autoLoad: true
});