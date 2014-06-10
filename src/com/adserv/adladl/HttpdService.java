package com.adserv.adladl;


import fi.iki.elonen.SimpleWebServer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.widget.Toast;


public class HttpdService extends Service {

	private static SimpleWebServer HttpdServ = null;
	private static SQLHelper AdserverDb = null;
	private static String droidId = null;
	
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
		return new Binder();
	}

	
	
    
	public void onDestroy() {
		super.onDestroy();

		System.out.println("In DESTROY");
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
//						startHttpdServer(8080, "192.168.1.108");
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
}
