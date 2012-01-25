

WeatherObsercation=Ext.regModel('WeatherObsercation', {
    fields: [
        {name: 'dimensionName',        type: 'string'},
        {name: 'environment', type: 'string'},
        {name: 'type',   type: 'string'},
        {name: 'measuredValue',   type: 'float'},
        {name: 'measuredUnit',   type: 'string'},
        {name: 'validMetric',    type: 'boolean'}
    ]
});

new Ext.Application({
	name: 'Husio',
	launch: function(){
		
		var currentWeatherStore = new Ext.data.Store({
		    model: 'WeatherObsercation',
		    storeId: 'currentWeatherStore',
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
		
		var observationTpl = new Ext.XTemplate(
			    '<tpl for=".">',
			    	'<div class="observation {[values.dimensionName.toLowerCase()]} {[values.environment.toLowerCase()]} {[values.type.toLowerCase()]}">',
			        	'<span class="observation value">{[values.measuredValue.toFixed(1)]}</span>',
			        	'<span class="observation unit"> {measuredUnit}</span>',
			        '</div>',
			    '</tpl>'
		);
		
        var drawComponent = new Ext.draw.Component({
            items: [{
              type: 'circle',
              fill: '#00c',
              radius: 100,
              x: 150,
              y: 150
            }]
        });
        
        var weatherData=new Ext.DataView({
            store: currentWeatherStore,
            tpl: observationTpl,
            autoHeight:true,
            multiSelect: false,
            itemSelector:'div.thumb-wrap',
            emptyText: 'No data to display'
        })
        
        var refreshWeatherData= function(){
        	currentWeatherStore.load();
        }

		var weatherCarousel = new Ext.Carousel({
            iconCls: 'weather',
            title: 'Weather',
            defaults: {
                cls: 'card'
            },
            items: [weatherData,
            {
                xtype: 'chart',
                animate: 'true',
                store: currentWeatherStore,
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
            },drawComponent,
            {
                title: 'Tab ',
                html: '<p>Weather SCR 3</p>'
            }]
        });

		new Ext.TabPanel({
			fullscreen: true,
			cardSwitchAnimation: 'fade',
			tabBar: {
		        dock: 'bottom',
		        ui: 'dark',
		        layout: {
		            pack: 'center'
		        }
		    },
			dockedItems: [
			              {xtype:'toolbar', ui: 'light', dock: 'top', title:'Husio', 
			            	  items:[{xtype: 'spacer'},{text: 'update', handler: refreshWeatherData }]
			              },
          	],
			items: [
			        weatherCarousel,
			        {html: 'Power Information Comes Here', title: 'Power', iconCls: 'power'},
			        {html: 'Ligts Automation Comes Here', title: 'Lights', iconCls: 'bulb'},
			        {html: 'Security, Alarms Come Here', title: 'Security', iconCls: 'security'}
			]
		});
 
        drawComponent.surface.renderFrame();
	}
});