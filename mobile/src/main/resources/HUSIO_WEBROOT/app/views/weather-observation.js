/**
 * The observation template
 */
var observationTpl = new Ext.XTemplate(
	    '<tpl for=".">',
    	'<div class="observation {[values.mtype.toLowerCase()]} {[values.environment.toLowerCase()]} {[values.variant.toLowerCase()]}">',
        	'<span class="observation value">{[values.measuredValue.toFixed(1)]}</span>',
        	'<span class="observation unit"> {measuredUnit}</span>',
        '</div>',
    '</tpl>'
);