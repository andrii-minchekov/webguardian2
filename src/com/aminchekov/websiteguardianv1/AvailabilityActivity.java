package com.aminchekov.websiteguardianv1;

import com.aminchekov.websiteguardianv1.database.StatusData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AvailabilityActivity extends Activity{
	
	TextView allCount; 
	TextView failureCount;
	StatusData statusData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.availability);
		statusData = new StatusData(this);
		//displayData();
		
	}
	@Override	
    protected void onResume() {
    	super.onResume();
    	allCount = (TextView) findViewById(R.id.allCount);
    	allCount.setText("All responses count = " + statusData.getAllCount());
    	failureCount = (TextView) findViewById(R.id.failureCount);
    	failureCount.setText("Failure responses count = " + statusData.getFailuresCount());
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
			finish();
			//super.onBackPressed();
		}
		return true;
	}
}
