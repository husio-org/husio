
/**
 * Weather Controller
 */
Husio.controllers.weather=Ext.regController('Weather', {
	
    ocws: Ext.StoreMgr.get("OutdoorCurrentWeatherStore"),
    icws: Ext.StoreMgr.get("IndoorCurrentWeatherStore"),
    hws: Ext.StoreMgr.get("WeatherHistoryStore"),

		
	// Reloads current weather information
	refreshCurrentWeather: function(){
		console.log("reloading current weather");
		this.ocws.load();
		this.icws.load();
		this.hws.load();
	},
	
	// Outdoor weater
	outdoor: function(){
		Husio.views.mainToolbar.setTitle("Outdoor");
		console.log("showing outdoor weather");
	},
	
	indoor: function(){
		Husio.views.mainToolbar.setTitle("Indoor");
		console.log("showing indoor weather");
	},
	
	history: function(){
		Husio.views.mainToolbar.setTitle("History");
		console.log("showing temperature history");
	},
	
});


