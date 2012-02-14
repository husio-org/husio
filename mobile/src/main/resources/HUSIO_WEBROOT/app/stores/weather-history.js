/**
 * Store used for charthing weather information
 */
Ext.regStore('WeatherHistoryStore',{
    model: 'WeatherHistoryEntry',
    proxy: {
        type: 'rest',
        url : '/rest/whistory',
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
});
