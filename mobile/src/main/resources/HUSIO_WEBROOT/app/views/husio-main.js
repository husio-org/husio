Husio.views.mainToolbar = new Ext.Toolbar({
    dock : 'top',
    title: 'Husio',
	ui: 'light', 
	items:[
	       {xtype: 'spacer'},
	       {text: 'update', handler: function(){
	    	   Ext.dispatch({controller: 'Weather', action: 'refreshCurrentWeather'});
	    	   }
	       }
	]
});

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
	dockedItems: [Husio.views.mainToolbar],
	items: [
	        Husio.views.weatherCarousel,
	        {html: 'Power Information Comes Here', title: 'Power', iconCls: 'power'},
	        {html: 'Ligts Automation Comes Here', title: 'Lights', iconCls: 'bulb'},
	        {html: 'Security, Alarms Come Here', title: 'Security', iconCls: 'security'}
	]
}); 