/**
 * Weather history entry. likely to be a lot of data will 
 * use small field names
 */
Husio.models.WeatherHistoryEntry=Ext.regModel('WeatherHistoryEntry', {
    fields: [
        {name: 'date', type: 'date', dateFormat:'c'},
        {name: 'duration', type: 'float'},
        {name: 'temp', type: 'float'},
        {name: 'press', type: 'float'}
    ]
});