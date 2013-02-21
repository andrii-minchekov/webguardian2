package com.aminchekov.websiteguardianv1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//DbHelper implementations
public class DbHelper extends SQLiteOpenHelper {

	private static final String TAG = DbHelper.class.getSimpleName();
	static final int VERSION = 1;
	static final String DATABASE = "guardian.db";

	public DbHelper(Context context) {
		super(context, DATABASE, null, VERSION);
		Log.i(TAG, "Creating database: " + DATABASE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StatusData.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		StatusData.onUpgrade(db, oldVersion, newVersion);
		this.onCreate(db);
	}
	
	public void close() { //
		super.close();
	}
}