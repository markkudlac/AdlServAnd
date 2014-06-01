package com.adserv.adladl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Routes {
	
	public static String parse(String uri){
		String msg = "{\"rtn\":false}";
		
		if (uri.indexOf("/getads/") > 0){
			try {
				List<String> jstr = new ArrayList<String>();
			JSONObject jsob = new JSONObject();
			
			jsob.put("id", 1);
			jsob.put("urlimg", "/ads/kkat300x50.gif");
			jsob.put("urlhref","http://hersheys.com/kitkat");
			jstr.add(jsob.toString().replace("\\", ""));
			
			jsob.put("id", 2);
			jsob.put("urlimg", "/ads/rover300x50.jpg");
			jsob.put("urlhref","http://hersheys.com/landrover.com/us/en/lr");
			jstr.add(jsob.toString().replace("\\", ""));
			
			msg = jstr.toString();
			System.out.println(msg);
			
			}
			
			catch(JSONException ex) {
				        ex.printStackTrace();
				    }

		}
		return(msg);
	}

}
