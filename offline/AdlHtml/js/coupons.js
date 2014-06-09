var device="mark";

function begincoupons(){
	initAjax();
	loadCoupons();
}


function loadCoupons(){
	
	$.getJSON("/api/get_kept_coupons/"+device, null,
			function(data){popListView(data, "CO")});
	
	$.getJSON("/api/get_kept_ads/"+device, null,
			function(data){popListView(data, "AD")});
}


function popListView(data, adtype){
	var curpage_id;
	
	if (data == undefined || data.rtn != undefined) {
		alert("DB error");
		return;
	}

	curpage_id = $(":mobile-pagecontainer").pagecontainer("getActivePage").attr("id")
	
	for (i=0; i<data.length; i++){
		
		xel = $('<li id="'+adtype+data[i].id+'"><div class="coupon">'+
				'<a target="_blank" href="' + data[i].urlhref +
				'"><img src="'+data[i].urlimg + '"/></a></div></li>')

		xlist = $("#"+adtype+"list")
		xlist.append(xel);
		
		if (curpage_id.indexOf(adtype) >= 0) {
			xlist.listview("refresh");
		}
	}
	
}