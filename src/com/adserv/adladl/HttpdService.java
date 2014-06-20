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
	private static String droidId;
	private static NetChangeReceiver netReceiver = null;
	private static Thread downloadThread = null;
	private static boolean interuptAllow = false;
	
//	private static boolean tstflg = true;
	
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 
	        return START_STICKY;
	    }
	 
	 
	@Override
	public IBinder onBind(Intent intent) {
	
		droidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		System.out.println("In HttpdService onBind id : " + droidId);
		
		startdb();
	 	turnServerOn(this);		//Must be after database is open
	 	
	 	SystemClock.sleep(250);

	 	
	 	if (netReceiver == null)
	 	{	 	
	 		netReceiver = new NetChangeReceiver();
		 	turnAdlComOn(this, droidId);		//This needs to come from DB someday
		 	netReceiver.register(this);
	 	}
	 	
		return new LocalBinder();
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

			stopHttpdServer();
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
	
	
	private void startdb(){
		stopDb();
		AdserverDb = new SQLHelper(this);
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

   		 if ( interuptAllow && downloadThread != null && !downloadThread.isInterrupted()){
   			 System.out.println("downloadThread interupt sent");
   			downloadThread.interrupt();
   		 }
   	 }
   	 

   	 public void register(Context context) {

   	 IntentFilter intentFilter = new IntentFilter();
   	 intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
 //  	 intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
   	 context.registerReceiver(this, intentFilter);
   	 }
 	}
 
    
	public  void turnAdlComOn(final Context context, final String deviceId) {

	if (downloadThread == null) {
		downloadThread = new Thread(new Runnable() {
			public void run() {
				int attempts = 0;
				
				while (true) {
					System.out.println("downloadThread running");
					interuptAllow = false;
				try {
						attempts = 0;
						while (attempts < 20 && !Util.isWifiConected(context)) {
//							System.out.println("Delay loop : "+attempts);
							Thread.sleep(CONNECT_DELAY);
							++attempts;
						}
						attempts = 0;
				
						if (Util.isWifiConected(context) &&
							Util.getTimeNow() - Prefs.getDownloadTime(context) >= POLL_DELAY){
							
							new HttpCom(context, "storeAds").execute("getads/"+deviceId+"/0");
//							Prefs.setDownloadTime is called in storeAds on completion
						}
						
						if (Util.isWifiConected(context)){
							SQLHelper.uploadToAdladl();
						}
						
						interuptAllow = true;
						Thread.sleep(POLL_DELAY + (120*1000));
					}
				 	catch (InterruptedException ex) {
				 		interuptAllow = false;
				 		attempts = 0;
				 		System.out.println("downloadThread exception/interupt caught : " + ex);
				 	}
				}
			}
		});
		downloadThread.start();
	}
	}
	
	
	 public class LocalBinder extends Binder {
	        HttpdService getService() {
	            return HttpdService.this;
	        }
	    }

	 
	 public void resetHttpdServer(){
 		
 		stopHttpdServer();
 		turnServerOn(this);
 	}
	 
	 
	 public void tester(){
		 
		 System.out.println("This is tester service call");
		 
//		 SQLHelper.uploadToAdladl();
//		 SQLHelper.testDone();
		 Util.sendNotofication(this, "http://www.adladl.com", "Visit Adladl");
	 }
}
