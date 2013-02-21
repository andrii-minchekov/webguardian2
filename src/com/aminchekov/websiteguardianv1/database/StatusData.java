package com.aminchekov.websiteguardianv1.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class StatusData { //
	
	private static final String TAG = StatusData.class.getSimpleName();
	public static final String TABLE = "responsedata";
	public static final String C_ID = "_id";
	public static final String C_CREATED_AT = "created_at";
	public static final String C_CODE = "status_code";
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	public static final int STATUS_OK = 200;

	private final DbHelper dbHelper; //

	public StatusData(Context context) { //
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Initialized data");
	}

	public static void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating table: " + TABLE);
		db.execSQL("create table " + TABLE + " (" + C_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + C_CREATED_AT
				+ " INTEGER, " + C_CODE + " INTEGER)");
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table " + TABLE);
		onCreate(db);
	}
	
	public long insertOrIgnore(ContentValues values) { //
		Log.d(TAG, "insertOrIgnore on " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase(); //
		try {
			return db.insertWithOnConflict(TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE); //
		} finally {
			db.close(); //
		}
	}
	

	/**
	 * 
	 * @return Cursor where the columns are _id, created_at, status_code
	 */
	public Cursor getAllResponses() { //
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
		return cursor;
	}
	public Cursor getFailureResponses() { //
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, null, C_CODE + "!= 200", null, null, null, GET_ALL_ORDER_BY);
		return cursor;
	}
	
	public long getAllCount() { //
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "SELECT COUNT(*) FROM " + TABLE;
	    SQLiteStatement statement = db.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    db.close();
	    return count;
	}
	
	public long getFailuresCount() { //
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "SELECT COUNT(*) FROM " + "(SELECT * FROM " + TABLE + " WHERE " + C_CODE + " != " + STATUS_OK +")";
	    SQLiteStatement statement = db.compileStatement(sql);
	    long count = statement.simpleQueryForLong();
	    db.close();
	    return count;
	}

	/**
	 * 
	 * @return Timestamp of the latest status we have it the database
	 */
	/*public GuardObject getLatestGuard() { //
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		GuardObject guardObject = new GuardObject();
		try {
			Cursor cursor = db.query(TABLE, null, C_CREATED_AT + " = (select " + MAX_CREATED_AT_COLUMNS[0] + " from " + TABLE+")", null,
					null, null, null);
			try {
				cursor.moveToNext();
				guardObject.setTimestamp(new Date(cursor.getLong(1)));
				guardObject.setResponseCode(cursor.getInt(2));
				return guardObject;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			finally {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			db.close();
			
		}
	}

	*//**
	 * 
	 * @param id
	 *            of the status we are looking for
	 * @return Text of the status
	 *//*
	public String getResponsesByCode(int code) { //
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_CODE + "=" + code,
					null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
	}
*/	
	public void close() { //
		dbHelper.close();
	}

	public DbHelper getDbHelper() {
		return dbHelper;
	}
	
}
