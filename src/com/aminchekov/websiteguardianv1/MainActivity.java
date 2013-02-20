package com.aminchekov.websiteguardianv1;

import java.util.Date;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		CompoundButton.OnCheckedChangeListener {

	StatusData statusData;
	GuardApplication guardApp;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_CODE };
	static final int[] TO = { R.id.responseTime, R.id.responseCode };
	boolean showAll = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		statusData = new StatusData(this);
		guardApp = (GuardApplication) getApplication();
		setContentView(R.layout.activity_main);
		// Find your views
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		// Check whether preferences have been set
		if (guardApp.getPrefs().getString("url", null) == null) { //
			startActivity(new Intent(this, PrefsActivity.class)); //
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG)
					.show(); //
		}
		Switch s = (Switch) findViewById(R.id.switch1);
		if (s != null) {
			s.setOnCheckedChangeListener(this);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Setup List
		displayData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Close the database
		statusData.close(); //
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itemURL) {
			startActivity(new Intent(this, PrefsActivity.class));
		} else if (item.getItemId() == R.id.itemHistory) {
			onResume();
		} else if (item.getItemId() == R.id.itemAvailability) {
			startActivity(new Intent(this, AvailabilityActivity.class));
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startGuard) {
			// start guard process in another Thread
			startService(new Intent(this, GuardService.class)); //

		} else if (v.getId() == R.id.stopGuard) {
			// stop guard process and terminate running thread
			stopService(new Intent(this, GuardService.class)); //
		} else if (v.getId() == R.id.refreshDisplay) {
			displayData();
		}
	}

	// Responsible for fetching data and setting up the list and the adapter
	private void setupAllList() { //
		// Get the data
		cursor = statusData.getAllResponses();
		startManagingCursor(cursor);
		// Setup Adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER); //
		listTimeline.setAdapter(adapter);
	}

	private void setupFailuresList() {
		cursor = statusData.getFailureResponses();
		startManagingCursor(cursor);
		// Setup Adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER); //
		listTimeline.setAdapter(adapter);
	}
	
	private void displayData() {
		if (showAll == true) {
			this.setupAllList();
		} else {
			this.setupFailuresList();
		}
	}

	/*
	 * private void displayData() { GuardObject guard =
	 * statusData.getLatestGuard(); if (guard != null) { new
	 * UpdateResponse().execute(guard); } }
	 * 
	 * 
	 * class UpdateResponse extends AsyncTask<GuardObject, Integer, GuardObject>
	 * {
	 * 
	 * @Override protected GuardObject doInBackground(GuardObject... params) {
	 * GuardObject obj = params[0]; return obj; }
	 * 
	 * @Override protected void onPostExecute(GuardObject result) { // TextView
	 * textView = (TextView) findViewById(R.id.textView1);
	 * textView.setText("StatusCode: " + result.getResponseCode() + ", Date: " +
	 * result.getTimestamp().toString()); } }
	 */

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.switch1) {
			if (isChecked) {
				showAll = true;
				setupAllList();
			} else {
				showAll = false;
				setupFailuresList();
			}
		}
	}

	// View binder constant to inject business logic that converts a timestamp
	// to
	// relative time
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.responseTime)
				return false;
			// Update the created at text to relative time
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils
					.getRelativeTimeSpanString(timestamp, new Date().getTime(),
							DateUtils.MINUTE_IN_MILLIS);
			((TextView) view).setText(relTime);
			return true;
		}
	};
}
