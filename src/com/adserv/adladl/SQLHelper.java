package com.adserv.adladl;

import static com.adserv.adladl.Const.*;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.database.SQLException;
import android.os.SystemClock;
import android.text.format.Time;

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
				"create table " + TABLE_ADVERTS +" ( " +
				FLD_ID + " integer primary key autoincrement, " +
				FLD_GRPCD + " integer default 0, " +
				FLD_ADTYPE + " char(2) default \'AD\', " +
				FLD_URLIMG + " text, " +
				FLD_URLHREF + " text, " +
				FLD_ADL_ID + " integer not null unique, " +		//This should generate index
				FLD_STATUS + " char(1) default \'P\', " +
				FLD_CREATED_AT + " integer " +
				")"
			);

		
//		initializeAdverts(db);
		
		System.out.println("Out createTables");
		}
		catch (SQLException e) { System.out.println("SQLException create"); }
	}
	

	/*
	protected void initializeAdverts(SQLiteDatabase db){
		
		ContentValues values = new ContentValues();
		long nowtm = 0;
		
		Time tm = new Time();
		tm.setToNow();
		nowtm = tm.toMillis(true) / 1000;
		
        values.put(FLD_URLIMG, "/ads/kkat300x50.gif");
        values.put(FLD_URLHREF, "http://hersheys.com/kitkat");
        values.put(FLD_ADL_ID, 10);
        values.put(FLD_CREATED_AT, nowtm);
        
        if (-1 == db.insert(TABLE_ADVERTS, null, values))
			System.out.println("adverts insert error");
        
        values.put(FLD_URLIMG, "/ads/rover300x50.jpg");
        values.put(FLD_URLHREF, "http://landrover.com/us/en/lr");
        values.put(FLD_ADL_ID, 23);
        
        if (-1 == db.insert(TABLE_ADVERTS, null, values))
			System.out.println("adverts insert error");
	}
	*/
	
	/*
	protected void updateAdverts(){
		
		ContentValues values = new ContentValues();
		String[] args = {"1"};
		
        values.put(FLD_URLIMG, "/ads/kkat300x50.gif");
        values.put(FLD_URLHREF, "http://hersheys.com/kitkat");

        
        if (-1 == database.update(TABLE_ADVERTS, values, "id = ?", args))
			System.out.println("adverts insert error");
	}
	
	
	*/
	
	protected void closeDb() {
		database.close();
		database = null;
	}
	
	
	
	protected static String getads(String strt){
		
		String msg = "{\"rtn\":false}";
		Cursor tmpCursor;
		String[] args = new String[1];
		

		args[0] = strt;
		tmpCursor = database.rawQuery("SELECT * FROM " + TABLE_ADVERTS +
				" WHERE status == 'A' AND id > ?", args);
		
		try {
			List<String> jstr = new ArrayList<String>();		//Should be changed to JSONArry
			JSONObject jsob = new JSONObject();
		
			if (tmpCursor.moveToFirst()){
			
				do {
					System.out.println("get from DB id : "+tmpCursor.getInt(tmpCursor.getColumnIndex(FLD_ID)));
					jsob.put(FLD_ID, tmpCursor.getLong(tmpCursor.getColumnIndex(FLD_ID)));		
					jsob.put(FLD_URLIMG, tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLIMG)));
					jsob.put(FLD_URLHREF,tmpCursor.getString(tmpCursor.getColumnIndex(FLD_URLHREF)));
					jstr.add(jsob.toString().replace("\\", ""));
					
				} while(tmpCursor.moveToNext());
			}
			msg = jstr.toString();
		}	
		catch(JSONException ex) {
	        ex.printStackTrace();
	    }
		tmpCursor.close();
//		System.out.println("getads arg 1 : "+strt);

		return(msg);
	}
	
	
	public static void storeAds(JSONArray ads){
		
		ContentValues values = new ContentValues();
		JSONObject jsob;
		
		Time tm = new Time();
		tm.setToNow();
		long nowtm = tm.toMillis(true) / 1000;
		
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
		        values.put(FLD_ADL_ID, jsob.getLong(FLD_ID));
		        values.put(FLD_CREATED_AT, nowtm);
		        
		        System.out.println( FLD_ADL_ID + " is  : "+ values.getAsLong(FLD_ADL_ID));
//		        System.out.println( FLD_CREATED_AT + " is : "+ values.getAsLong(FLD_CREATED_AT));
//		        System.out.println( FLD_URLHREF + " is : "+ values.getAsString(FLD_URLHREF));
//		        System.out.println( FLD_URLIMG + " is : "+ values.getAsString(FLD_URLIMG));
		        
		        args[0] = values.getAsString(FLD_ADL_ID);
		        rows = database.update(TABLE_ADVERTS, values, FLD_ADL_ID + " = ?", args);
		        
		        if (rows == 0 && -1 == database.insert(TABLE_ADVERTS, null, values))
					System.out.println("adverts insert error");	
			}
		}
		catch(JSONException ex) {
		       ex.printStackTrace();
		}
		getAdImg();
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
}
