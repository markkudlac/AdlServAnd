package com.adserv.adladl;

import static com.adserv.adladl.Const.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.database.SQLException;
import android.os.SystemClock;


public class SQLHelper extends SQLiteOpenHelper {

	private static SQLiteDatabase database = null;
	private static Context srvcontext;
	
	public SQLHelper(Context context) {
		
		super(context, DB_NAME, null, CURRENT_DB_VERSION);
		
		srvcontext = context;
		
		try {
		database = getWritableDatabase();
		}
		catch (SQLiteException e) { System.out.println("SQLiteException"); }
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("In onCreate db");
		Util.versionChangeHTML(srvcontext);
		dropAndCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		System.out.println("In onUpgrade db old : "+ oldVersion + " new : " + newVersion);
		
		if (newVersion >= CURRENT_DB_VERSION) {
			Util.versionChangeHTML(srvcontext);
		}
	}

	protected void dropAndCreate(SQLiteDatabase db) {
		db.execSQL("drop table if exists " + TABLE_ADVERTS + ";");
		createTables(db);
	}
	
	protected void createTables(SQLiteDatabase db) {

		System.out.println("In createTables");
		
		try  {
		db.execSQL(
				"create table " + TABLE_DEVICE +" ( " +
				FLD_ID + " integer primary key autoincrement, " +
				FLD_TAG + " text, " +
				FLD_STATUS + " char(1) default \'A\', " +
				FLD_CREATED_AT + " integer default 0, " +
				FLD_UPDATED_AT + " integer default 0 " +
				")"
			);
		
		db.execSQL(
				"create table " + TABLE_AD_LISTS +" ( " +
				FLD_ID + " integer primary key autoincrement, " +
				FLD_ADVERT_ID + " integer default 0 unique, " +
				FLD_ACTION + " integer default 0, " +
				FLD_CREATED_AT + " integer default 0, " +
				FLD_UPDATED_AT + " integer default 0 " +
				")"
			);
		
		db.execSQL(
				"create table " + TABLE_ADVERTS +" ( " +
				FLD_ID + " integer primary key autoincrement, " +
				FLD_GRPCD + " integer default 0, " +
				FLD_ADTYPE + " char(2) default \'" + FLD_ADTYPE_AD + "\', " +
				FLD_URLIMG + " text, " +
				FLD_URLHREF + " text, " +
				FLD_LOCALHREF + " text default \'" + FLD_LOCALHREF_DEF + "\', " +
				FLD_ADL_ID + " integer not null unique, " +		//This should generate index
				FLD_STATUS + " char(1) default \'P\', " +
				FLD_CREATED_AT + " integer default 0 " +
				")"
			);
		
		
		db.execSQL(
				"create table " + TABLE_UPLOADS +" ( " +
				FLD_ID + " integer primary key autoincrement, " +
				FLD_CALL_METHOD + " text not null, " +
				FLD_PARAMS + " text not null, " +
				FLD_STATUS + " char(1) default \'P\', " +
				FLD_CREATED_AT + " integer default 0, " +
				FLD_UPDATED_AT + " integer default 0 " +
				")"
			);
		
		initializeDevice(db);
		
		System.out.println("Out createTables");
		}
		catch (SQLException e) { System.out.println("SQLException create"); }
	}
	
	
	protected void closeDb() {
		database.close();
		database = null;
	}
	
	
	private static void initializeDevice(SQLiteDatabase db){
		ContentValues values = new ContentValues();
		
		System.out.println("nIn initialize Device");
		values.put(FLD_TAG, HttpdService.getDroidId());
        values.put(FLD_CREATED_AT, Util.getTimeNow());
  
        if (-1 == db.insert(TABLE_DEVICE, null, values)){
        	System.out.println("device insert error");
        }
	}
	
	
	protected static String getads(String start){
		
		String msg =  Util.JSONReturn(false);;
		Cursor tmpCursor;
		String[] args = new String[1];
		
		args[0] = start;
		
		tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_ADVERTS +
				" WHERE status = 'A' AND id > ? " + notInAd_List(), args);
		
		try {
			JSONArray jArray =  new JSONArray();
			JSONObject jsob;
		
			if (tmpCursor.moveToFirst()){
			
				do {
					jsob = new JSONObject();
					System.out.println("get from DB id : "+tmpCursor.getInt(tmpCursor.getColumnIndex(FLD_ID)));
					jsob.put(FLD_ID, tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_ID)));		
					jsob.put(FLD_URLIMG, tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLIMG)));
					jsob.put(FLD_URLHREF,tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLHREF)));
					jsob.put(FLD_LOCALHREF,tmpCursor.getString(tmpCursor.getColumnIndex(FLD_LOCALHREF)));
					jsob.put(FLD_ADTYPE,tmpCursor.getString(tmpCursor.getColumnIndex(FLD_ADTYPE)));
					jArray.put(jsob);
					
					System.out.println( FLD_LOCALHREF + " is : "+tmpCursor.getString(tmpCursor.getColumnIndex(FLD_LOCALHREF)) );
				} while(tmpCursor.moveToNext());
			}
			msg = jArray.toString().replace("\\", "");
		}	
		catch(JSONException ex) {
	        ex.printStackTrace();
	    }
		tmpCursor.close();
		return(msg);
	}
	
	
	private static String notInAd_List(){
		Cursor tmpCursor;
		String notin = " ";
		Boolean trip = false;
		
		tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_AD_LISTS, null);
		
			if (tmpCursor.moveToFirst()){
				notin = " AND " + FLD_ID + " NOT IN ( ";
				do {
					if (trip) notin = notin + ", ";
					trip = true;
				
					notin = notin + tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_ADVERT_ID));		
				} while(tmpCursor.moveToNext());
				
				notin = notin + " ) ";
			}

		tmpCursor.close();
		System.out.println("notin final : "+notin);
		return(notin);
	}
	
	
	public static void storeAds(JSONArray ads, Context context){
		
		ContentValues values = new ContentValues();
		JSONObject jsob;
		
		long nowtm = Util.getTimeNow();
		
		if (null == database){
			System.out.println("DB not  open");
			return;
		}
		
		try {
			int i,rows;
			String[] args = new String[1];
			
			for (i=0; i<ads.length(); i++){
				jsob = ads.getJSONObject(i);
						
				values.put(FLD_URLIMG, jsob.getString(FLD_URLIMG));
		        values.put(FLD_URLHREF, jsob.getString(FLD_URLHREF));
		        values.put(FLD_ADTYPE, jsob.getString(FLD_ADTYPE));
		        values.put(FLD_ADL_ID, jsob.getLong(FLD_ID));
		        values.put(FLD_CREATED_AT, nowtm);
		        
		        System.out.println( FLD_ADL_ID + " is  : "+ values.getAsLong(FLD_ADL_ID));
//		        System.out.println( FLD_CREATED_AT + " is : "+ values.getAsLong(FLD_CREATED_AT));
//		        System.out.println( FLD_URLHREF + " is : "+ values.getAsString(FLD_URLHREF));
//		        System.out.println( FLD_URLIMG + " is : "+ values.getAsString(FLD_URLIMG));
		
		        // ONLY FOR TESTING  +++++++++++
		        
		        if (values.getAsString(FLD_URLIMG).indexOf("kkat") >0){
		        	values.put(FLD_LOCALHREF, "/pages/kitkat/landvid.html");
		        }
		        
		        args[0] = values.getAsString(FLD_ADL_ID);
		        rows = database.update(TABLE_ADVERTS, values, FLD_ADL_ID + " = ?", args);
		        
		        if (rows == 0 && -1 == database.insert(TABLE_ADVERTS, null, values))
					System.out.println("adverts insert error");	
			}
			
			getAdImg();
			Prefs.setDownloadTime(context, Util.getTimeNow());
		}
		catch(JSONException ex) {
		       ex.printStackTrace();
		}
		
	}
	
	
	protected static void getAdImg(){

		new Thread(new Runnable() {
			public void run() {
				
				Cursor tmpCursor;		
				String urlimg;
				long id;
				
				tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_ADVERTS +
						" WHERE status = 'P'", null);
		
			if (tmpCursor.moveToFirst()){
				do {
					id = tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_ID));
					urlimg = tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLIMG));
					System.out.println("get urlimg : "+urlimg);
	
					Util.downloadFile(srvcontext, id, urlimg);
				} while(tmpCursor.moveToNext());
			}

			tmpCursor.close();
//		System.out.println("getads arg 1 : "+strt);
			}
		}).start();
	}
	
	
	
	protected static void setAdvertStatus(long id, String status){
		ContentValues values = new ContentValues();
		String[] args = new String[1];
		
		args[0] = String.valueOf(id);
        values.put(FLD_STATUS, status);
        
        if (-1 == database.update(TABLE_ADVERTS, values, "id = ?", args))
			System.out.println("advert status update error");
		
	}
	
	
	
	protected static String exclude(String tag, long advert_id){
		
		return(insertAd_List(tag, advert_id, 0));
	}
	
	
	protected static String keep(String tag, long advert_id){
		
		return(insertAd_List(tag, advert_id, 1));
	}
	
	
	private static String insertAd_List(String tag, long advert_id, int action){
		
		ContentValues values = new ContentValues();
		String msg =  Util.JSONReturn(false);
		long nowtm = Util.getTimeNow();
		
//		System.out.println("insertAd_List tag : "+tag+"   advert_id : " + advert_id);
		
		values.put(FLD_ADVERT_ID, advert_id);
		values.put(FLD_ACTION, action);
        values.put(FLD_CREATED_AT, nowtm);
  
        if (-1 != database.insert(TABLE_AD_LISTS, null, values)){
        	msg =  Util.JSONReturn(true);
        } else {
        	System.out.println("ad_lists insert error");	
        }
		return(msg);
	}
	
	
	protected static String clearads(){
		
//		System.out.println("In clearads");
		
        database.delete(TABLE_AD_LISTS, null, null);
        return( Util.JSONReturn(true));

	}
	
	
/*
	protected static String get_instruct(){
		Cursor tmpCursor;
//		System.out.println("In get_instruct");
		
		tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_DEVICE +
				" WHERE status = 'A' AND instruct_cnt >= 0", null);
		
		if (tmpCursor.moveToFirst()) {
			set_instruct(tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_INSTRUCT_CNT)) + 1);
			tmpCursor.close();
			return(Util.JSONReturn(true));
		} else {
			tmpCursor.close();
			return(Util.JSONReturn(false));
		}
	}
	
	
	protected static String set_instruct(long cnt){
		ContentValues values = new ContentValues();
		int err;
		 
        values.put(FLD_INSTRUCT_CNT, cnt);
        err = database.update(TABLE_DEVICE, values, "status = 'A'", null);
        
        if (-1 == err)
			System.out.println("device update error");
		
		return(Util.JSONReturn(true));
	}
*/
	
	
	protected static String get_kept_coupons(){
		
		return(getAdvertByType(FLD_ADTYPE_COUPON));
	}
	
	
	protected static String get_kept_ads(){
		
		return(getAdvertByType(FLD_ADTYPE_AD));
	}
	
	
protected static String getAdvertByType(String adtype){
		
		String msg =  Util.JSONReturn(false);;
		Cursor tmpCursor;
		String[] args = new String[1];
		
		args[0] = adtype;
		
		tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_ADVERTS + " INNER JOIN " + TABLE_AD_LISTS +
				" ON adverts.id = ad_lists.advert_id " +
				" WHERE adverts.status = 'A' AND ad_lists.action = 1 AND adverts.adtype = ? ", args);
		
		try {
			JSONObject jsob;
			JSONArray jArray =  new JSONArray();
			
			if (tmpCursor.moveToFirst()){
			
				do {
					jsob = new JSONObject();
					System.out.println("get by adtype : "+adtype+"   id : "+tmpCursor.getInt(tmpCursor.getColumnIndex(FLD_ID)));
					jsob.put(FLD_ID, tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_ID)));		
					jsob.put(FLD_URLIMG, tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLIMG)));
					jsob.put(FLD_URLHREF,tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLHREF)));
					jsob.put(FLD_LOCALHREF,tmpCursor.getString(tmpCursor.getColumnIndex(FLD_LOCALHREF)));
					jArray.put(jsob);
					
				} while(tmpCursor.moveToNext());
			}
			msg = jArray.toString().replace("\\", "");
		}	
		catch(JSONException ex) {
	        ex.printStackTrace();
	    }
		tmpCursor.close();
		return(msg);
	}


	protected static String formupload(String qryString){
		
		ContentValues values = new ContentValues();
		String msg =  Util.JSONReturn(false);
		long nowtm = Util.getTimeNow();
		
		System.out.println("insert uploadForm qryString : "+qryString);
		
		values.put(FLD_CALL_METHOD, FORMUPLOAD);
		values.put(FLD_PARAMS, qryString);
        values.put(FLD_CREATED_AT, nowtm);
  
        if (-1 != database.insert(TABLE_UPLOADS, null, values)){
        	msg =  Util.JSONReturn(true);
        } else {
        	System.out.println("formupload insert error");	
        }
		return(msg);
	}

	
	protected static void uploadToAdladl(){

		Cursor tmpCursor;		
		String params, cmethod;
		long id;
		
		System.out.println("In uploadToAdladl 1");
		
		if  (database == null) {
			return;
		}
		
		tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_UPLOADS +
						" WHERE status = 'P'", null);
				
		if (tmpCursor.moveToFirst()){
				
			do {	
					id = tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_ID));
					cmethod = tmpCursor.getString(tmpCursor.getColumnIndex(FLD_CALL_METHOD));
					params = tmpCursor.getString(tmpCursor.getColumnIndex(FLD_PARAMS));
					System.out.println("UPLOAD  : "+cmethod+"?"+params);
	
					new HttpCom(srvcontext,"uploadDone").execute(cmethod+"?"+params+"&id="+id);
					
					SystemClock.sleep(500);
				} while(tmpCursor.moveToNext());
		}
			
		System.out.println("Out uploadToAdladl");
		tmpCursor.close();
	}
	
	
	public static void uploadDone(JSONArray ads, Context context){
		
		ContentValues values = new ContentValues();
		JSONObject jsob;
		
		System.out.println("In uploadDone");
		
		if (null == database){
			System.out.println("DB not  open");
			return;
		}
		
		 values.put(FLD_STATUS, "D");
		 values.put(FLD_UPDATED_AT, Util.getTimeNow());
		 
		try {
			int i;
			String[] args = new String[1];
			
			for (i=0; i<ads.length(); i++){
				jsob = ads.getJSONObject(i);
						
				args[0] = jsob.getString(FLD_ID);
				
				System.out.println("update uploads with this id : " + args[0]);
		        if (-1 == database.update(TABLE_UPLOADS, values, "id = ?", args))
					System.out.println("advert status update error");
			}
		}
		catch(JSONException ex) {
			ex.printStackTrace();
		} 
	}
	
	
	public static void testDone(){
		int i;
		ContentValues values = new ContentValues();
		
		System.out.println("In testDone");
		
		if (null == database){		//TESTING ***********
			System.out.println("DB not  open");
			return;
		}
		
		 values.put(FLD_STATUS, "P");
		 values.put(FLD_UPDATED_AT, Util.getTimeNow());
		 
			String[] args = new String[1];
						
				args[0] = "3";
				
				System.out.println("update uploads with this id : " + args[0]);
		        i = database.update(TABLE_UPLOADS, values, "id = ?", args);
					System.out.println("Update uploads count : "+i);
		}
		
}
