package com.adserv.adladl;

import static com.adserv.adladl.Const.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Routes {
	
	public static String callMethods(String uri){

		String msg = "{\"rtn\":false}";
		
		uri = trimUri(uri,API_PATH);

		if (uri.indexOf("getads/") == 0){
			uri = trimUri(uri,"getads/");
			System.out.println("trimUri : "+uri);			
			msg = SQLHelper.getads(getArg(uri, 1));
			
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
		final Matcher matcher = Pattern.compile("(\\w+)").matcher(uri);
		
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
