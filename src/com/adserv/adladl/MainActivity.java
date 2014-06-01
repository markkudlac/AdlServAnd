package com.adserv.adladl;


import fi.iki.elonen.SimpleWebServer;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends Activity {

	public static SimpleWebServer HttpdServ = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		Util.versionChangeHTML(this);
		turnServerOn();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("Got configuration change : Landscape");
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

			System.out.println("Got configuration change : Portrait");
		}
	}
	
	public void onDestroy() {
		super.onDestroy();

		System.out.println("In DESTROY");

		stopHttpdServer();

		System.out.println("Destroy OUT");

	}

	
	public void startHttpdServer(int httpdPort, String ipadd) {

		try {
			stopHttpdServer();
			System.out.println("get Files Dir  : " + getFilesDir());
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
}
