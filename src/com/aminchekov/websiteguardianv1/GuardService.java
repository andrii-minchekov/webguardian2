package com.aminchekov.websiteguardianv1;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GuardService extends Service {
	public final static String TAG = GuardService.class.getSimpleName();
	public static int refresh_period = 1; 
	//private Context context;
	GuardApplication guardApp;
	Timer timer = new Timer();
	MyTimerTask timerTask = new MyTimerTask();
	Guardian guardian;
	private boolean runFlag = false; //
	//DbHelper dbHelper;
	//SQLiteDatabase db;
	StatusData statusData;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.guardApp = (GuardApplication) getApplication(); //
		this.guardian = this.new Guardian();
		statusData = new StatusData(guardApp);
		//dbHelper = statusData.dbHelper;
		Log.d(TAG, "onCreated");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (this.runFlag == false) {
			this.runFlag = true;
			this.guardian.start();
			this.guardApp.setServiceRunning(true); //
			Log.d(TAG, "onStarted");
		} else {
			Log.d(TAG, "already Started");
		}
	
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.runFlag = false;
		if (timer != null) {
			timer.cancel();
		}
		if (this.guardian != null) {
			this.guardian.interrupt();
			this.guardian = null;
		}
		this.guardApp.setServiceRunning(false); //
		Log.d(TAG, "onDestroyed");
	}

	private class MyTimerTask extends TimerTask {
		public void run() {
			Log.i("MyTimer is", "Called");
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(guardApp.getPrefs().getString("url", "http://sysiq.com"));
			HttpResponse response = null;
			try {
				response = client.execute(request);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("statusCode = " + statusCode);
			// Open the database for writing
			ContentValues values = new ContentValues(); //
			values.clear(); //
			values.put(StatusData.C_CREATED_AT, System.currentTimeMillis());
			values.put(StatusData.C_CODE, statusCode);
			statusData.insertOrIgnore(values);
			//activityInstance.new UpdateResponse().execute(statusData.getLatestGuard());
		} 
	}


	private class Guardian extends Thread {
		@Override
		public void run() {
			GuardService guardService = GuardService.this;
			if (guardService.runFlag) {
				timer.schedule(timerTask, 5*1000, refresh_period*60*1000);
			}
		}
	}
}

