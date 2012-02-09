/**
 * The observation template
 */
var observationTpl = new Ext.XTemplate(
	    '<tpl for=".">',
    	'<div class="observation {[values.dimensionName.toLowerCase()]} {[values.environment.toLowerCase()]} {[values.type.toLowerCase()]}">',
        	'<span class="observation value">{[values.measuredValue.toFixed(1)]}</span>',
        	'<span class="observation unit"> {measuredUnit}</span>',
        '</div>',
    '</tpl>'
);