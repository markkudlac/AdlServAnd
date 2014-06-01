package com.adserv.adladl;

import android.provider.BaseColumns;

public interface Const extends BaseColumns {
	
	public static final int BASE_BLOCKSIZE = 65536;
	
	public static final String RESOLVER_ADDRESS = "http://www.fullsink.com";
	public static final String LOG_SERVER_PATH = "/api/";			// Add this to Resolver address

	public static final String HTTP_PROT = "http";
	public static final String HTML_DIR = "AdlHtml";
	public static final String USERHTML_DIR = "UserHtml";
}
