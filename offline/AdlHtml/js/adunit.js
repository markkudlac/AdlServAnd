/* Manage the adunit in offline*/

//var devicetag = "mark";
var prizemode = false;
var app_reg = "apregcode";

var admarker = 0;		//This is temporary
var timer = null;
var prizetimer = null;
var prizecnt = 0;
var prizeobj = null;
var instructflg = 0;
var tapnhold = false; //This is used to resolve conflict of tap and taphold

function beginads(){
	
//	console.log("Begin ads params : "+window.location);
	
	var params = getSearchParameters();
	
	if (typeof params.prize !== 'undefined') {
		prizemode = (params.prize == "true");
//		console.log("Prizemode flag is : "+prizemode)
	}
	
	if (typeof params.app_reg !== 'undefined') {
		app_reg = params.app_reg;
	}
		
	initAjax();
	$("#checkinst").one("change",instructClear)
	getAds(admarker);

}


function beginAdScrolling(){
		
	haltAdScrolling()
	timer = setInterval(loadNextAd, 5000)
}


function haltAdScrolling(){

	if (timer != null) {
		clearInterval(timer)
		timer = null;
	}
}


function beginPrizeScrolling(){
		
	haltPrizeScrolling()
	prizetimer = setInterval(scrollPrize, 3000)
}


function haltPrizeScrolling(){

	if (prizetimer != null) {
		clearInterval(prizetimer)
		prizetimer = null;
	}
	prizecnt = 0;
}


function getAds(adblock){
	
	if (!activePrize()){
		$.getJSON("/api/getads/" + adblock,null,function(data){
			if (data && data.rtn == undefined) { appendAds(data) } else
			{ console.log("getAds data undefined")}
		});
	}
}


function appendAds(data){
	
	var xel = null;
	
	if (data == undefined) return;
	
	for (i=0; i<data.length; i++){
//console.log("advertid : "+data[i].id);

		xel = $('<div id="pg'+data[i].id+'" data-role="page" class="adfind">'+
			'<div data-role="content" style="padding: 0px">'+
				'<a target="_blank" href="' + data[i].urlhref + '" advert_id="' +
				data[i].id +
				'" localhref="'+data[i].localhref+'"><img src="' + data[i].urlimg + 
				'"/><img src="img/adlauncher.png" class="adlauncher" /></a></div></div>')

				xel.page({ defaults: true })
				setEvents(xel)
				$(":mobile-pagecontainer").append(xel)
	}
	
	if (xel != null) {
		$(":mobile-pagecontainer").pagecontainer("change",
		 xel, {allowSamePageTransition: true, transition: "slidedown"});
		 
		 beginAdScrolling();		//Begin scrollong ads
		 
		 if (instructflg == 0){
			 instructNeeded()
		 }
	 }
}

function setEvents(jqryobj){
	
	$(jqryobj).swiperight(function(event) {  
		
		// delete from add list and exclude from future ad lists
		event.preventDefault()
		
		rm_ad = $(this)
		
		/* This was being used before message to get next ad
		Could be re engaged to not repeat previous ads
		
		xpg = $(this).next(".adfind")
		
		if (xpg.length == 0) { 
			xpg = $("body").children(".adfind:first")
			
			if ( xpg.length == 1) { 	// Removing last item
				xpg = $("#adl0")}
		}
*/
				
		if (!cancelPrize()){		//exit from current prize
		 	manageAd(rm_ad, "exclude")

			$("#adlmess").unbind("swiperight")
			$("#adltext").text("Will Not See This Again")
		
			$(":mobile-pagecontainer").pagecontainer("change",
//			 xpg, {transition: "slide", reverse: true});
				$("#adlmess"), {transition: "slide", reverse: true});
			 beginAdScrolling();		/* This starts at beginning. Might want to get next */
		 }

		})
		
		.swipeleft(function(event) {
			// delete from add list and mark as kept in db
			event.preventDefault()

			if (activePrize()) return;
			
			if (prizemode){
				beginPrize($(this))
			} else {
				keepAd($(this))
			}

		 })
		 
		 .tap(function(event){
				event.preventDefault()
				
				if (typeof window.jsinterface === 'undefined' || 
						window.jsinterface.localHref()) {
					xhref = $(this).find('a').attr("localhref")
					xhref = xhref + "?advert_id="+$(this).find("a").attr("advert_id")
					xhref = xhref + "&urlimg=" + $(this).find('img:first').attr("src")
				} else {
					xhref = $(this).find('a').attr("href")
				}
				console.log("tap event : "+xhref)
				if (!tapnhold) {
					window.open(xhref, "_blank")
				} else {
					tapnhold=false
				}
		 })
/*		 
		.taphold(function(event) {  
				event.preventDefault()
				
				tapnhold = true;
				console.log("tap and hold event")
//				window.open("localhost:8080/coupons/","_blank")
				window.jsinterface.vault();
				if (!activePrize()){
					beginAdScrolling();
				}
		 })
	*/	 
		 .on("vmousedown", function(event){
			 event.preventDefault()
			 haltAdScrolling()
		 })
		 
		 .on("vmouseup", function(event){
			 event.preventDefault()
			 
			 if (!prizemode) {
			 	beginAdScrolling()
				}
		 })
		 
		.on("swipedown", function(event){
			event.preventDefault()
			
			if (activePrize()) return;
			
			loadNextAd()
			beginAdScrolling();
		})
		
		.on("swipeup", function(event) {  	
			event.preventDefault()

			if (activePrize()) return;
			
			xpg = $(this).prev(".adfind")
			if (xpg.length == 0) { 
				xpg = $("body").children(".adfind:last")
			}
			$(":mobile-pagecontainer").pagecontainer("change",
				xpg, {transition: "slideup", allowSamePageTransition: true});
				beginAdScrolling();
		});							
}



function keepAd(keepobj){
	
	$("#adlmess").unbind("swiperight")
	$("#adltext").text("Stored")
	
	$(":mobile-pagecontainer").pagecontainer("change",
		 $("#adlmess"), {transition: "slide"});
	 beginAdScrolling();		/* This starts at beginning. Might want to get next */
	 
	 manageAd(keepobj, "keep")
}



function loadNextAd(){
	
	if (instructflg == 1) {
		instructMess(0)
		return
	}
	
	xpg = $(":mobile-pagecontainer" ).pagecontainer("getActivePage").next(".adfind")
	if (xpg.length == 0) { 
		xpg = $("body").children(".adfind:first")
		
		if ( xpg.length == 0) { 	// Removing last item
			xpg = $("#adl0")
			haltAdScrolling()
		}
	}
	$(":mobile-pagecontainer").pagecontainer("change",
		xpg, {transition: "slidedown", allowSamePageTransition: true});
		
}


function manageAd(jqobj, action){
	
	adid = extractPgId(jqobj)
		
	jqobj.remove()
	$.getJSON("/api/"+ action + "/" + 
		adid, null, function(data){
			if (!data || !data.rtn) {
				alert("Db error")
			}
	});
}


function extractPgId(jqobj){
	
	return jqobj.attr("id").slice(2);
}


function activePrize(){
	
	return(prizeobj != null);
}


function beginPrize(prizesel){
	
//		console.log("prize selected")
	prizeobj = prizesel
	haltAdScrolling()
	
	$("#adlmess").one("swiperight",cancelPrize)
	$("#adltext").text("You Are Playing For")
	$(":mobile-pagecontainer").pagecontainer("change",
		$("#adlmess"), {transition: "slide"});
	
		beginPrizeScrolling();

}


function scrollPrize(){
	var xobj;
	
//	console.log("In scrollPrixe cnt : "+prizecnt)

		$("#adltext").text("Swipe Right to Cancel")

		if (prizecnt > 0 && prizecnt % 4 == 0){
			xobj = $("#adlmess")
		} else if (prizecnt % 2 == 0){
			$("#adltext").text("You Are Playing For")
			xobj = $("#adlmess")
		} else {
			xobj = prizeobj
		}

		$(":mobile-pagecontainer").pagecontainer("change",
					xobj, {transition: "flip"});
	++prizecnt;
}


function prizewon(){
//	console.log("In prize won")
	
	if (activePrize()){
		haltPrizeScrolling();
		manageAd(prizeobj, "keep")
		$("#adltext").text("WON")
		$(":mobile-pagecontainer").pagecontainer("change",
					$("#adlmess"), {transition: "slide"});
		prizeobj = null
	
		beginAdScrolling();
	}
}


function cancelPrize(){
	
	if (activePrize()){		//exit from current prize
		haltPrizeScrolling()
		prizeobj = null
		$("#adlmess").unbind("swiperight")
		$("#adlmess2").unbind("swiperight")
		$("#adltext2").text("CANCELLED")
		
		$(":mobile-pagecontainer").pagecontainer("change",
		 $("#adlmess2"), {transition: "slide", reverse: true});
		 beginAdScrolling();
		return true;
	} else {
		return false;
	}
}


function clearads(){

	$.getJSON("/api/clearads",null,function(data){
		if (data && data.rtn == true) {
			 console.log("ads cleared")
			 
			 haltAdScrolling();
			 $(":mobile-pagecontainer").remove(".adfind");
			 getAds(admarker);
	 	} else {
			console.log("clear ads failed")
		}
	});
}


function instructMess(cnt){
	
	console.log("In instructMess cnt : "+cnt)
	
	$("#adlmess").unbind("swiperight")
	$("#adlmess2").unbind("swiperight")
	haltAdScrolling();
	
	if (cnt == 0){
		$("#adltext").text("These ads can be swiped")
		
		$(":mobile-pagecontainer").pagecontainer("change",
		 $("#adlmess"), {transition: "slidedown"});
	} else if (cnt  == 1) {
		$("#adltext2").text("Swipe right to remove")
		
		$(":mobile-pagecontainer").pagecontainer("change",
		 $("#adlmess2"), {transition: "slide", reverse: true});
		
	} else if (cnt  == 2){
		$("#adltext").text("Swipe left to store")
		
		$(":mobile-pagecontainer").pagecontainer("change",
		 $("#adlmess"), {transition: "slide"});
	 } else if (cnt  == 3){
		$("#adltext2").text("Use Adladl app for store")
		
		$(":mobile-pagecontainer").pagecontainer("change",
		 $("#adlmess2"), {transition: "slidedown"});
	 } else {
		
 		$(":mobile-pagecontainer").pagecontainer("change",
 		 $("#clrinst"), {transition: "slidedown"});
		 instructflg = 2;
		 beginAdScrolling();
		return;
	}
	
	++cnt;
	setTimeout("instructMess(" + cnt + ")",3000)
}


function getSearchParameters() {
      var prmstr = window.location.search.substr(1);
//			console.log("Search params : "+prmstr)
      return prmstr != null && prmstr != "" ? transformToAssocArray(prmstr) : {};
}

function transformToAssocArray( prmstr ) {
    var params = {};
    var prmarr = prmstr.split("&");
    for ( var i = 0; i < prmarr.length; i++) {
        var tmparr = prmarr[i].split("=");
        params[tmparr[0]] = tmparr[1];
    }
    return params;
}



function instructClear(){
	$.mobile.loading("show")
	instructSet(-1);
	instructflg = 2
}


function instructSet(cnt){
	
	$.getJSON("/api/set_instruct/" + "/"+cnt,null,function(data){
		if (data && data.rtn == true) {
//			 console.log("Instruction stopped") 
	 	} 
	});
}


function instructNeeded(){
	
	$.getJSON("/api/get_instruct",null,function(data){
		if (data && data.rtn == true) {
			 instructflg = 1
	 	} else {
			instructflg = 2;
		}
	});
}

