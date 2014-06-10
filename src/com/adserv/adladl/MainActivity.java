package com.adserv.adladl;

import com.adserv.adladl.HttpCom;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;

import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	static private String droidId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		droidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		Intent intent = new Intent();
		intent.setClassName("com.adserv.adladl", "com.adserv.adladl.HttpdService");
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		startService(intent);
		
//		((TextView) findViewById(R.id.ipaddress)).setText(
//				"Upload To : "+Prefs.getUploadDir(this));
		
		Toast.makeText(getBaseContext(), "Upload To : "+Prefs.getUploadDir(this) ,
				Toast.LENGTH_LONG).show();
	}

	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	System.out.println("onServiceConnected in adlserv");
        	
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	System.out.println("onServiceDisConnect in adlserv");
        }
    };
    
    
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
			toSettings(item);
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
			
			((TextView) rootView.findViewById(R.id.ipaddress)).setText(
					"HTTP : "+ Util.getHTTPAddress(container.getContext()));
					
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

	}

	
	public void toSettings(MenuItem item) {
		
		System.out.println("In Settings ");
		
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}


	public void click(View view) {
		int id = view.getId();

		Toast.makeText(getBaseContext(), "Button press",
				Toast.LENGTH_SHORT).show();
		
		switch (id) {
		case R.id.testbut:

			
		 	new SQLHelper(this);
		 	new HttpCom(this, "storeAds").execute("getads/"+droidId+"/0");

			return;
			
		case R.id.vaultbut:
			Intent intent = new Intent(this, CouponActivity.class);
			startActivity(intent);

			return;
		}
	}
	

}
