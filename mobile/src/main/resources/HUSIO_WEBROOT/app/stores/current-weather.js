/**
 * The Indoor Current Weather Store
 */
Ext.regStore('IndoorCurrentWeatherStore',{
    model: 'WeatherObservation',
    proxy: {
        type: 'rest',
        url : '/rest/weather',
        reader: {
            type: 'json',
            root: 'weatherObservation.measures'
        }
    },
    filters: [
              {
                  property: 'environment',
                  value   : 'INDOOR'
              }
          ],
    autoLoad: true
});

/**
 * The Indoor Current Weather Store
 */
Ext.regStore('OutdoorCurrentWeatherStore',{
    model: 'WeatherObservation',
    proxy: {
        type: 'rest',
        url : '/rest/weather',
        reader: {
            type: 'json',
            root: 'weatherObservation.measures'
        }
    },
    filters: [
              {
                  property: 'environment',
                  value   : 'OUTDOOR'
              }
          ],
    autoLoad: true
});