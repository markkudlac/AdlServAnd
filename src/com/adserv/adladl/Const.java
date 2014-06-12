package com.adserv.adladl;

import android.provider.BaseColumns;

public interface Const extends BaseColumns {
	
	public static final int BASE_BLOCKSIZE = 65536;
	
	public static final String SOURCE_ADDRESS = "www.adladl.com";
	public static final String API_PATH = "/api/";

	public static final String HTTP_PROT = "http";
	public static final String HTML_DIR = "AdlHtml";
	public static final String USERHTML_DIR = "UserHtml";
	
	public static final String API_GETADS = "getads/";
	public static final String API_CLEARADS = "clearads/";
	public static final String API_EXCLUDE = "exclude/";
	public static final String API_KEEP = "keep/";
	public static final String API_GET_INSTRUCT = "get_instruct/";
	public static final String API_SET_INSTRUCT = "set_instruct/";
	public static final String API_GET_KEPT_COUPONS = "get_kept_coupons/";
	public static final String API_GET_KEPT_ADS = "get_kept_ads/";
	
	public static final String API_GETUPLOADDIR = "getuploaddir/";
	public static final long DOWNLOAD_POLL = 1000*3600*4;
	public static final long CONNECT_DELAY = 1000*10;
	
	public static final String INSTRUCTCNT = "com.adserv.adladl.instructcnt";
	public static final String UPLOADTIME = "com.adserv.adladl.uploadtime";
	
	public static final String DB_NAME = "adserver";
	public static final String FLD_ID = "id";
	public static final String FLD_STATUS = "status";
	public static final String FLD_CREATED_AT = "created_at";
	public static final String FLD_UPDATED_AT = "updated_at";
	public static final int CURRENT_DB_VERSION = 1;
	
	public static final String TABLE_DEVICE = "device";
	public static final String FLD_TAG= "tag";

	public static final String TABLE_AD_LISTS = "ad_lists";
	public static final String FLD_ADVERT_ID = "advert_id";
	public static final String FLD_ACTION = "action";
	
	
	public static final String TABLE_ADVERTS = "adverts";
	public static final String FLD_GRPCD = "grpcd";
	public static final String FLD_ADTYPE = "adtype";
		public static final String FLD_ADTYPE_COUPON = "CO";
		public static final String FLD_ADTYPE_AD = "AD";
	public static final String FLD_URLIMG = "urlimg";
	public static final String FLD_URLHREF = "urlhref";
	public static final String FLD_ADL_ID = "adl_id";
	
}
