package com.adserv.adladl;


import fi.iki.elonen.SimpleWebServer;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;


public class HttpdService extends Service {

	public static SimpleWebServer HttpdServ = null;
	public static SQLHelper adserverdb = null;
	
	
	 public int onStartCommand(Intent intent, int flags, int startId) {
		 
	        return START_STICKY;
	    }
	 
	 
	@Override
	public IBinder onBind(Intent intent) {
		
		System.out.println("In HttpdService onBind");
		
	 	turnServerOn();
//	 	SystemClock.sleep(500);
	 	stopDb();
	 	adserverdb = new SQLHelper(this);
	 	
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
//			System.out.println("get Files Dir  : " + getFilesDir());
			HttpdServ = new SimpleWebServer(ipadd, httpdPort, getFilesDir());
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
	
	
	public void turnServerOn() {

			new Thread(new Runnable() {
				public void run() {
					try {
						startHttpdServer(8080, "localhost");
//						startHttpdServer(8080, "192.168.1.108");
					} catch (Exception ex) {
						System.out.println("Select thread exception : " + ex);
					}

				}
			}).start();
	}
	
	
	
	private void stopDb(){
		
		if (adserverdb != null){
			adserverdb.closeDb();
			adserverdb = null;
		}
	}
}
