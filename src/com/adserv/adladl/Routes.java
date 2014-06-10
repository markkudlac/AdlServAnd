package com.adserv.adladl;

import static com.adserv.adladl.Const.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;


public class Routes {
	
	public static String callMethods(String uri, Context context){

		String msg = "{\"rtn\":false}";
		
		uri = trimUri(uri,API_PATH);

		if (uri.indexOf(API_GETADS) == 0){
			uri = trimUri(uri,API_GETADS);	
			msg = SQLHelper.getads(getArg(uri, 1));
			
		} else if (uri.indexOf(API_EXCLUDE) == 0){
			uri = trimUri(uri,API_EXCLUDE);	
			msg = SQLHelper.exclude(getArg(uri, 0), Long.valueOf(getArg(uri, 1)).longValue());
			
		} else if (uri.indexOf(API_KEEP) == 0){
			uri = trimUri(uri,API_KEEP);		
			msg = SQLHelper.keep(getArg(uri, 0), Long.valueOf(getArg(uri, 1)).longValue());
			
		} else if (uri.indexOf(API_CLEARADS) == 0){		
			msg = SQLHelper.clearads();
			
		} else if (uri.indexOf(API_GET_INSTRUCT) == 0){		
			msg = Prefs.get_instruct(context);
			
		} else if (uri.indexOf(API_SET_INSTRUCT) == 0){
			uri = trimUri(uri,API_SET_INSTRUCT);		
			msg = Prefs.set_instruct(context, Integer.valueOf(getArg(uri, 1)).intValue());
			
		} else if (uri.indexOf(API_GET_KEPT_COUPONS) == 0){		
			msg = SQLHelper.get_kept_coupons();
			
		} else if (uri.indexOf(API_GET_KEPT_ADS) == 0){		
			msg = SQLHelper.get_kept_ads();
			
		} else if (uri.indexOf(API_GETUPLOADDIR) == 0){		
			msg = Prefs.getuploaddir(context);
			
		}
		return(msg);
	}

	
	private static String trimUri(String uri, String lead){
		
		String strout = "";
		
		final Matcher matcher = Pattern.compile(lead).matcher(uri);
		if(matcher.find()){
		    strout = uri.substring(matcher.end()).trim();
		}
		
		return(strout);
	}
	
	
	private static String getArg(String uri, int indx){
		
		String xx = null;
		int i = 0;
		final Matcher matcher = Pattern.compile("(-?\\w+)").matcher(uri);
		
		while (matcher.find()) {
			if (i == indx) {
				xx = matcher.group(0);
				break;
			}
			++i;
		}
		
		return(xx);
	}
}
