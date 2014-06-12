package com.adserv.adladl;


import fi.iki.elonen.SimpleWebServer;

import static com.adserv.adladl.Const.*;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.widget.Toast;



public class HttpdService extends Service {

	private static SimpleWebServer HttpdServ = null;
	private static SQLHelper AdserverDb = null;
	private static String droidId = null;
	private static NetChangeReceiver netReceiver = null;
	private static Thread downloadThread = null;
	
//	private static boolean tstflg = true;
	
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 
	        return START_STICKY;
	    }
	 
	 
	@Override
	public IBinder onBind(Intent intent) {
	
		droidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		System.out.println("In HttpdService onBind id : " + droidId);
		
	 	stopDb();
	 	AdserverDb = new SQLHelper(this);
//	 	new HttpCom(this, "storeAds").execute("getads/nexusS/0");
	 	turnServerOn(this);		//Must be after database is open
	 	
	 	SystemClock.sleep(250);

	 	
	 	if (netReceiver == null)
	 	{	 	
	 		netReceiver = new NetChangeReceiver();
		 	turnDownloadOn(this, droidId, netReceiver);		//This needs to come from DB someday
	 	}
	 	
		return new Binder();
	}

	
    
	public void onDestroy() {
		super.onDestroy();

		System.out.println("In DESTROY");
		unregisterReceiver(netReceiver);
		netReceiver = null;
		stopHttpdServer();
		stopDb();
	}
	
	
	
	public void startHttpdServer(int httpdPort, String ipadd) {

		try {
			stopHttpdServer();
			HttpdServ = new SimpleWebServer(ipadd, httpdPort, getFilesDir(), this);
			HttpdServ.start();
			System.out.println("HttpdServ started Add : " + ipadd + "  Port : "
					+ httpdPort);
		} catch (Exception ex) {
			System.out.println("HttpdServer error  : " + ex);
		}
	}
	
	
	public void stopHttpdServer() {

		try {
			if (HttpdServ != null) {
				HttpdServ.stop();
				HttpdServ = null;
			}

		} catch (Exception ex) {
			System.out.println("HttpdServer stop : " + ex);
		}
	}
	
	
	public  void turnServerOn(final Context ctx) {

			new Thread(new Runnable() {
				public void run() {
					try {
						String ipad = Util.getHTTPAddress(ctx);
						startHttpdServer(8080, ipad);
						
					} catch (Exception ex) {
						System.out.println("Select thread exception : " + ex);
					}
				}
			}).start();
	}
	
	
	
	private void stopDb(){
		
		if (AdserverDb != null){
			AdserverDb.closeDb();
			AdserverDb = null;
		}
	}
	
	
	public static String getDroidId(){
		return(droidId);
	}
	
    
    public class NetChangeReceiver extends BroadcastReceiver { 
   	 
   	 public void onReceive( Context context, Intent intent ) {
   		 System.out.println("Inside Broadcast Reciever");

   		 if ( downloadThread != null && !downloadThread.isInterrupted()){
 //  			 System.out.println("downloadThread interupt sent");
   			 
   			 if (Util.getTimeNow() - Prefs.getDownloadTime(context) > DOWNLOAD_POLL){
   				 downloadThread.interrupt();
   			 }
   		 }

   	 }
   	 

   	 public void register(Context context) {

   	 IntentFilter intentFilter = new IntentFilter();
   	 intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
 //  	 intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
   	 context.registerReceiver(this, intentFilter);
   	 }
 	}
 
    
	public  void turnDownloadOn(final Context context, final String deviceId,
			final NetChangeReceiver netreceiver) {

	if (downloadThread == null) {
		downloadThread = new Thread(new Runnable() {
			public void run() {
				long timer = 0;
				long lastinterupt = Util.getTimeNow();
				int attempts = 0;
				boolean regwifi = true;
				
				while (true) {
					System.out.println("downloadThread running");
					
				try {
						if (Util.getTimeNow() - Prefs.getDownloadTime(context) > DOWNLOAD_POLL){
							if (Util.isWifiConected(context))  {
								new HttpCom(context, "storeAds").execute("getads/"+deviceId+"/0");
								timer = CONNECT_DELAY * 10;
							} else {
								timer = timer + CONNECT_DELAY;
							}
						
							++attempts;
							if (attempts > 20){
								attempts = 0;
								timer = DOWNLOAD_POLL;
							}
						} else {
							attempts = 0;
							timer = DOWNLOAD_POLL;
						}
						
						Thread.sleep(timer);
						
						if (regwifi) {
							netreceiver.register(context);
							regwifi = false;
						}
					}
				 catch (InterruptedException ex) {
					 if (Util.getTimeNow() - lastinterupt > CONNECT_DELAY){
						 lastinterupt = Util.getTimeNow();
						 timer = 0;
						 attempts = 0;
					 }
					System.out.println("downloadThread exception/interupt caught : " + ex);
				}
				}
			}
		});
		downloadThread.start();
	}
}


}
