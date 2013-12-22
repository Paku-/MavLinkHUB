package com.paku.mavlinkhub;


import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.fragments.FragmentsStatePagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	FragmentsStatePagerAdapter mFragmentsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mFragmentsPagerAdapter = new FragmentsStatePagerAdapter(
				this, getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mFragmentsPagerAdapter);
		
		

  	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//PreferenceManager.setDefaultValues(this, R.xml.preferences, false);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	            
	        	// Display the fragment as the main content.
	            //getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
	            
	            //Intent i = new Intent(this, SettingsFragment.class);	            	            
	            //startActivity(i);	            
	            
	            
	            Intent intent = new Intent();
	            intent.setClass(MainActivity.this, SettingsActivity.class);
	            startActivityForResult(intent, 0);	            
	            
	    	
	            return true;
//	        case R.id.help:
//	            showHelp();
//	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	

}
