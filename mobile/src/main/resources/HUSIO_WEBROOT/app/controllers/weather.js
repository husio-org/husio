
/**
 * Weather Controller
 */
Husio.controllers.Weather=Ext.regController('Weather', {
	
    ocws: Ext.StoreMgr.get("OutdoorCurrentWeatherStore"),
    icws: Ext.StoreMgr.get("IndoorCurrentWeatherStore"),
		
	// Reloads current weather information
	refreshCurrentWeather: function(){
		console.log("reloading current weather");
		this.ocws.load();
		this.icws.load();
	},
	
	// Outdoor weater
	outdoor: function(){
		Husio.views.mainToolbar.setTitle("Outdoor");
		console.log("showing outdoor weather");
	},
	
	indoor: function(){
		Husio.views.mainToolbar.setTitle("Indoor");
		console.log("showing indoor weather");
	}
});


