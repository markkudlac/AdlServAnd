package com.adserv.adladl;

import com.adserv.adladl.HttpCom;
import com.adserv.adladl.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
