package com.aminchekov.websiteguardianv1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{

	StatusData statusData = new StatusData(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		displayData();
		
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
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startGuard) {
			//start guard process in another Thread
			startService(new Intent(this, GuardService.class)); //
			
		} else if (v.getId() == R.id.stopGuard) {
			//stop guard process and terminate running thread
			stopService(new Intent(this, GuardService.class)); //
		} else if (v.getId() == R.id.refreshDisplay) {
			displayData();
		}
	}
	
	private void displayData() {
		GuardObject guard = statusData.getLatestGuard();
		if (guard != null) {
			new UpdateResponse().execute(guard);
		}
	}
	
	class UpdateResponse extends AsyncTask<GuardObject, Integer, GuardObject> {

		@Override
		protected GuardObject doInBackground(GuardObject... params) {
			GuardObject obj = params[0];
			return obj;
		}
		
		@Override
		protected void onPostExecute(GuardObject result) { //
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.setText("StatusCode: " + result.getResponseCode() + ", Date: " + result.getTimestamp().toString());
		}
	}
}


