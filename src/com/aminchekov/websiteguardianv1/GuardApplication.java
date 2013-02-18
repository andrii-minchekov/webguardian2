package com.aminchekov.websiteguardianv1;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;


public class GuardApplication extends Application implements OnSharedPreferenceChangeListener{

	private static final String TAG = GuardApplication.class.getSimpleName();
	public SharedPreferences prefs;
	private boolean serviceRunning; //

	public boolean isServiceRunning() { //
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) { //
		this.serviceRunning = serviceRunning;
	}
	
	@Override
	public void onCreate() { //
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		Log.i(TAG, "onCreated Guard Application object");
	}
	
	@Override
	public void onTerminate() { //
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
	}
}
