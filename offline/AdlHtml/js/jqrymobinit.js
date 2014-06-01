/* This file loads before JQMobile is loaded 
	 Initialize Jquery mobile to increase swipe sensitivity 
	 For up/down, value is hard coded */

$( document ).on( "mobileinit", function() {

		$.event.special.swipe.horizontalDistanceThreshold = 5;

});


