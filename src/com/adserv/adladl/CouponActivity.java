package com.adserv.adladl;

import com.adserv.adladl.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;



public class CouponActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        WebView webarg = (WebView)findViewById(R.id.vaultview);
        WebSettings webSettings = webarg.getSettings();
		webSettings.setJavaScriptEnabled(true);		
		
		webarg.loadUrl("http://localhost:8080/AdlHtml/coupons.html");
    }
	
}
