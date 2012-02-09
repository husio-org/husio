Husio.views.MainPanel=Ext.extend(Ext.TabPanel,{
	fullscreen: true,
	cardSwitchAnimation: 'fade',
	tabBar: {
        dock: 'bottom',
        ui: 'dark',
        layout: {
            pack: 'center'
        }
    },
	dockedItems: [{
					xtype:'toolbar',
					ui: 'light', 
					dock: 'top', 
					title:'Husio', 
	            	items:[
	            	       {xtype: 'spacer'},
	            	       {text: 'update', handler: function(){
	            	    	   Ext.dispatch({controller: 'Weather', action: 'refreshCurrentWeather'});
	            	    	   }
	            	       }
	            	]
		}],
	items: [
	        Husio.views.weatherCarousel,
	        {html: 'Power Information Comes Here', title: 'Power', iconCls: 'power'},
	        {html: 'Ligts Automation Comes Here', title: 'Lights', iconCls: 'bulb'},
	        {html: 'Security, Alarms Come Here', title: 'Security', iconCls: 'security'}
	]
}); 