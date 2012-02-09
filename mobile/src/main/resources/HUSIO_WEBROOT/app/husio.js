
/**
 * The Application Object, Launch Function, and Main Structure
 */
Husio=new Ext.Application({
	name: 'Husio',
	launch: function(){
		new Husio.views.MainPanel();
		console.log("Husio touch client started");
	}
});