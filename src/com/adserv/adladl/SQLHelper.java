package com.adserv.adladl;

import static com.adserv.adladl.Const.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.database.SQLException;
import android.text.format.Time;

public class SQLHelper extends SQLiteOpenHelper {

	private SQLiteDatabase database = null;
	private Context srvcontext;
	
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
				FLD_GRPCD + " integer default 2, " +
				FLD_ADTYPE + " text default \'AD\', " +
				FLD_URLIMG + " text, " +
				FLD_URLHREF + " text, " +
				FLD_DESCRIPT + " text, " +
				FLD_CREATED_AT + " integer " +
				")"
			);
		
		initializeAdverts(db);
		
		System.out.println("Out createTables");
		}
		catch (SQLException e) { System.out.println("SQLException create"); }
	}
	
	
	protected void initializeAdverts(SQLiteDatabase db){
		
		ContentValues values = new ContentValues();
		long nowtm = 0;
		
		Time tm = new Time();
		tm.setToNow();
		nowtm = tm.toMillis(true) / 1000;
		
        values.put(FLD_URLIMG, "This is urlimg");
        values.put(FLD_URLHREF, "This is urlhref");
        values.put(FLD_CREATED_AT, nowtm);
        
        if (-1 == db.insert(TABLE_ADVERTS, null, values))
			System.out.println("adverts insert error");
	}
	
	
	protected void updateAdverts(){
		
		ContentValues values = new ContentValues();
		String[] args = {"1"};
		
        values.put(FLD_URLIMG, "/ads/sy.gif");
        values.put(FLD_URLHREF, "/ads/35.jpg");

        
        if (-1 == database.update(TABLE_ADVERTS, values, "id = ?", args))
			System.out.println("adverts insert error");
	}
	
	
	protected void dumpAdvert(){
		Cursor tmpCursor;
		
		System.out.println("In dumpAdvert");
		tmpCursor = database.rawQuery("select * from adverts", null);
		tmpCursor.moveToFirst();
		DatabaseUtils.dumpCurrentRow(tmpCursor);
	}
	
	
	protected void closeDb() {
		database.close();
		database = null;
	}
	
}
