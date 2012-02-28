
/**
 * The outdoor data template View
 */
Husio.views.weatherOutdoorData=new Ext.DataView({
    store: 'OutdoorCurrentWeatherStore',
    tpl: Husio.views.observationTpl,
    autoHeight:true,
    multiSelect: false,
    scroll:false,
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
    scroll:false,
    itemSelector:'div.thumb-wrap',
    emptyText: 'No data to display',
    listeners:{
        activate: function(){
	    	Ext.dispatch({controller: 'Weather', action: 'indoor'});
		}		
     }
});

Husio.views.pressureTempHC=new Ext.chart.Chart({
    cls: 'line1',
    theme: 'Demo',
    animate: true,
    insetPadding: 25,
    store: Ext.StoreMgr.get("WeatherHistoryStore"),
    legend: {
        position: {
            portrait: 'right',
            landscape: 'top'
        },
        labelFont: '17px Arial'
    },
    axes: [{
        type: 'Numeric',
        position: 'left',
        title: 'Centigrades',
        fields: ['temp']
    },{
        type: 'Numeric',
        position: 'right',
        title: 'Hecto Pascal',
        fields: ['press']
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['date'],
        dateFormat: 'U',
        label : {
            renderer : function(val) {
                return val.format("G:i");
            },
            rotate: {
                degrees: 45
            }
        },
    }],
    series: [{
        title: 'Temperature',
        type: 'line',
        xField: 'date',
        yField: 'temp',
        showMarkers: false,
        fill: true,
        lineWith: 1,
        axis: 'left',
        smooth: true
    },{
    	title: 'Pressure',
        type: 'line',
        xField: 'date',
        yField: 'press',
        showMarkers: false,
        lineWith: 1,
        axis: 'right',
        smooth: true
    }],
    listeners:{
        activate: function(){
	    	Ext.dispatch({controller: 'Weather', action: 'history'});
		}		
     }
});

Husio.views.windHC=new Ext.chart.Chart({
    cls: 'line1',
    theme: 'Demo',
    animate: true,
    insetPadding: 25,
    store: Ext.StoreMgr.get("WeatherHistoryStore"),
    legend: {
        position: {
            portrait: 'right',
            landscape: 'top'
        },
        labelFont: '17px Arial'
    },
    axes: [{
        type: 'Numeric',
        position: 'right',
        title: 'Knots',
        fields: ['wind','windGust']
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['date'],
        dateFormat: 'U',
        label : {
            renderer : function(val) {
                return val.format("G:i");
            },
            rotate: {
                degrees: 45
            }
        },
    }],
    series: [{
        title: 'Wind',
        type: 'line',
        xField: 'date',
        yField: 'wind',
        showMarkers: false,
        fill: true,
        lineWith: 1,
        axis: 'right',
        smooth: true
    },{
    	title: 'Gust',
        //type: 'scatter',
        type: 'line',
        markerConfig: {
        	type:"cross",
            radius: 5,
            size: 5
        },
        xField: 'date',
        yField: 'windGust',
        //showMarkers: false,
        lineWith: 0,
        axis: 'right',
        smooth: false
    }],
    listeners:{
        activate: function(){
	    	Ext.dispatch({controller: 'Weather', action: 'history'});
		}		
     }
});

Husio.views.rainHC=new Ext.chart.Chart({
    cls: 'line1',
    theme: 'Demo',
    animate: true,
    insetPadding: 25,
    store: Ext.StoreMgr.get("WeatherHistoryStore"),
    legend: {
        position: {
            portrait: 'right',
            landscape: 'top'
        },
        labelFont: '17px Arial'
    },
    axes: [{
        type: 'Numeric',
        position: 'right',
        title: 'Millimiters',
        fields: ['rain']
    }, {
        type: 'Category',
        position: 'bottom',
        fields: ['date'],
        dateFormat: 'U',
        label : {
            renderer : function(val) {
                return val.format("G:i");
            },
            rotate: {
                degrees: 45
            }
        },
    }],
    series: [{
        title: 'Rainfall',
        type: 'column',
        xField: 'date',
        yField: 'rain',
        showMarkers: false,
        fill: true,
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
    items: [Husio.views.weatherOutdoorData,Husio.views.weatherIndoorData,Husio.views.pressureTempHC,Husio.views.rainHC,Husio.views.windHC]
});