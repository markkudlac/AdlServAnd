package com.adserv.adladl;

import android.provider.BaseColumns;

public interface Const extends BaseColumns {
	
	public static final int BASE_BLOCKSIZE = 65536;
	
	public static final String RESOLVER_ADDRESS = "http://www.fullsink.com";
	public static final String LOG_SERVER_PATH = "/api/";			// Add this to Resolver address

	public static final String HTTP_PROT = "http";
	public static final String HTML_DIR = "AdlHtml";
	public static final String USERHTML_DIR = "UserHtml";
	
	public static final String DB_NAME = "adserver";
	public static final String FLD_ID = "id";
	public static final String FLD_CREATED_AT = "created_at";
	public static final int CURRENT_DB_VERSION = 3;
	
	public static final String TABLE_AD_LISTS = "ad_lists";
	
	
	public static final String TABLE_ADVERTS = "adverts";
	public static final String FLD_GRPCD = "grpcd";
	public static final String FLD_ADTYPE = "adtype";
	public static final String FLD_URLIMG = "urlimg";
	public static final String FLD_URLHREF = "urlhref";
	public static final String FLD_DESCRIPT = "descript";
	
}
