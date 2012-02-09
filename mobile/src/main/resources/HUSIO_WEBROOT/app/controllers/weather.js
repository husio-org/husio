
/**
 * Weather Controller
 */
Husio.controllers.Weather=Ext.regController('Weather', {
	
	store: Ext.StoreMgr.get("CurrentWeatherStore"),
	
	// Reloads current weather information
	refreshCurrentWeather: function(){
		console.log("reloading current weather");
		this.store.load();
	},
	
	// Outdoor weater
	outdoor: function(){
		this.store.clearFilter();
		this.store.filter("environment","OUTDOOR");
		console.log("filtering to outdoor weather");
	},
	
	indoor: function(){
		this.store.clearFilter();
		this.store.filter("environment","INDOOR");
		console.log("filtering to indoor weather");
	}
});


