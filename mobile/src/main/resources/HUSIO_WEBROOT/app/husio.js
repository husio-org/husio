
/**
 * The Application Object, Launch Function, and Main Structure
 */
Husio=new Ext.Application({
	name: 'Husio',
	launch: function(){
		Husio.views.mainPanel=new Husio.views.MainPanel();
				
		console.log("Husio touch client started");
	}
});