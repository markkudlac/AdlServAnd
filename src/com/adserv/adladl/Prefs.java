package com.adserv.adladl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import static com.adserv.adladl.Const.*;


public class Prefs extends PreferenceFragment implements OnSharedPreferenceChangeListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}
	
	 @Override
     public void onResume() {
         super.onResume();
         getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	 }
	 
     @Override
     public void onPause() {
         super.onPause();
         getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
     }
     
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
 //       if (key.equals(getResources().getString(R.string.prizemode))) {
        	
 //           boolean pmode = sharedPreferences.getBoolean(key, false);
 //           System.out.println("In SettingsActivity Prizemode : "+pmode);
  //          MainActivity.changePrizeMode();
 //       }
    }
    
    public static boolean getIPaddress(Context context) {

		boolean ipmode = PreferenceManager.getDefaultSharedPreferences(
				context).getBoolean(
				context.getString(R.string.ipaddress), false);
//		System.out.println("In getPrizeMode : "+pzmode);
		return(ipmode);
	}

    
    public static String getUploadDir(Context context) {
		  
		String xstr = PreferenceManager
				.getDefaultSharedPreferences(context).getString(
						context.getString(R.string.uploadto), USERHTML_DIR);
		return(xstr);
	}
	
    
	public static void setInstructCnt(Context context, int count) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		prefs.edit().putInt(INSTRUCTCNT, count)
				.commit();
	}

	
	public static int getInstructCnt(Context context) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs.getInt(INSTRUCTCNT, 0); 
	}
	
	
	protected static String get_instruct(Context context){

		int cnt =  getInstructCnt(context);
		System.out.println("In get_instruct cnt : "+cnt);		
		if (cnt >= 0) {
			setInstructCnt(context, cnt + 1);
			return(Util.JSONReturn(true));
		} else {
			return(Util.JSONReturn(false));
		}
	}
	
	
	protected static String set_instruct(Context context, int cnt){		 

		setInstructCnt(context, cnt);
		return(Util.JSONReturn(true));
	}
	
	
	public static void setDownloadTime(Context context, long time) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		prefs.edit().putLong(UPLOADTIME, time)
				.commit();
	}

	
	public static long getDownloadTime(Context context) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		return prefs.getLong(UPLOADTIME, 0); 
	}
	
	
	protected static String getuploaddir(Context context){

//		System.out.println("In getuploaddir");
		
			return("{\"dir\":\"" + getUploadDir(context) + "\"}");

	}
	
}
