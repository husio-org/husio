
/**
 * The outdoor data template View
 */
Husio.views.weatherOutdoorData=new Ext.DataView({
    store: 'OutdoorCurrentWeatherStore',
    tpl: observationTpl,
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
    tpl: observationTpl,
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


/**
 * The weather views carousel
 */
Husio.views.weatherCarousel = new Ext.Carousel({
    iconCls: 'weather',
    title: 'Weather',
    defaults: {
        cls: 'card'
    },
    items: [Husio.views.weatherOutdoorData,Husio.views.weatherIndoorData,
    {
        xtype: 'chart',
        animate: 'true',
        store: 'CurrentWeatherStore',
        flex: 1,
        insetPadding: 25,
        axes: [{
            type: 'gauge',
            position: 'gauge',
            minimum: 0,
            maximum: 100,
            steps: 10,
            margin: 7
        }],
        series: [{
            type: 'gauge',
            field: 'measuredValue',
            donut: 30,
            colorSet: ['#82B525', '#ddd']
        }]
    }
    ]
});