
/**
 * The outdoor data template View
 */
Husio.views.weatherOutdoorData=new Ext.DataView({
    store: 'OutdoorCurrentWeatherStore',
    tpl: Husio.views.observationTpl,
    autoHeight:true,
    multiSelect: false,
    itemSelector:'div.thumb-wrap',
    emptyText: 'No data to display',
    listeners:{
        activate: function(){
	    	Ext.dispatch({controller: 'Weather', action: 'outdoor'});
		}
     }
});

/**
 * The indoor data template view
 */
Husio.views.weatherIndoorData=new Ext.DataView({
    store: 'IndoorCurrentWeatherStore',
    tpl: Husio.views.observationTpl,
    autoHeight:true,
    multiSelect: false,
    itemSelector:'div.thumb-wrap',
    emptyText: 'No data to display',
    listeners:{
        activate: function(){
	    	Ext.dispatch({controller: 'Weather', action: 'indoor'});
		}		
     }
});


Husio.views.historyChart=new Ext.chart.Chart({
    cls: 'line1',
    theme: 'Demo',
    animate: true,
    insetPadding: 25,
    store: Ext.StoreMgr.get("WeatherHistoryStore"),
    axes: [{
        type: 'Numeric',
        position: 'left',
        title: 'Temperature',
        fields: ['temp']
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['date'],
        dateFormat: 'U'
    }],
    series: [{
        type: 'line',
        xField: 'date',
        yField: 'temp',
        showMarkers: false,
        lineWith: 1,
        axis: 'left',
        smooth: true
    }],
    listeners:{
        activate: function(){
	    	Ext.dispatch({controller: 'Weather', action: 'history'});
		}		
     }
});


/**
 * The weather views carousel
 */
Husio.views.weatherCarousel = new Ext.Carousel({
    iconCls: 'weather',
    title: 'Weather',
    defaults: {
        cls: 'card'
    },
    items: [Husio.views.weatherOutdoorData,Husio.views.weatherIndoorData,Husio.views.historyChart]
});