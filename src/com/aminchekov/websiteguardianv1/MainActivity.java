package com.aminchekov.websiteguardianv1;

import java.util.Date;

import com.aminchekov.websiteguardianv1.database.StatusData;
import com.aminchekov.websiteguardianv1.provider.GuardContentProvider;

import android.os.Bundle;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
		CompoundButton.OnCheckedChangeListener,
		LoaderManager.LoaderCallbacks<Cursor> {

	StatusData statusData;
	GuardApplication guardApp;
	Cursor cursor;
	CursorLoader cursorLoader;
	ListView listTimeline;
	TextView noDataText;
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
		noDataText = (TextView) findViewById(android.R.id.empty);
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
		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.row, null, FROM, TO, 0);
		adapter.setViewBinder(VIEW_BINDER); //
		listTimeline.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshData(showAll);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		} 
		//not needed anymore cause happening automatically notification
		else if (v.getId() == R.id.refreshDisplay) {
			refreshData(showAll);
		}
	}

	public void refreshData(boolean showAll) {
			cursorLoader.setSelection(selectData(showAll));
			cursorLoader.forceLoad();
	}

	public String selectData(boolean showAll) {
		String selection = null;
		if (showAll == false) {
			selection = StatusData.C_CODE + "!=" + StatusData.STATUS_OK;
		}
		return selection;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.switch1) {
			if (isChecked) {
				showAll = true;
				refreshData(showAll);
				// getLoaderManager().restartLoader(id, args, callback);
			} else {
				showAll = false;
				refreshData(showAll);
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Provide here a Cursor data
		// String[] projection = { StatusData.C_CREATED_AT, StatusData.C_CODE };
		cursorLoader = new CursorLoader(getApplicationContext(),
				GuardContentProvider.Responses.CONTENT_URI, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// CursorLoader provides a Cursor
		adapter.swapCursor(cursor);
		adapter.notifyDataSetChanged();
		TextView messageEmptyCursor = (TextView) findViewById(android.R.id.empty);
		if (adapter.getCount() <= 0 ) {
			messageEmptyCursor.setText("Site " + guardApp.getPrefs().getString("url", null) + " " + getString(R.string.no_data));
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Release reference to the Cursor
		adapter.swapCursor(null);
	}
}
