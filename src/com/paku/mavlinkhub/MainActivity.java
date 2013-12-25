package com.paku.mavlinkhub;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.CommunicationHUB;
import com.paku.mavlinkhub.fragments.ConnectivityFragment;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	FragmentsAdapter mFragmentsPagerAdapter;
	ViewPager mViewPager;
	CommunicationHUB comHUB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mFragmentsPagerAdapter = new FragmentsAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mFragmentsPagerAdapter);

		comHUB = (CommunicationHUB) this.getApplication();
		comHUB.Init(this);

		IntentFilter BtIntentFilter = new IntentFilter();
				//BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		BtIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		BtIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		BtIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		BtIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);		
		registerReceiver(mBtReceiver, BtIntentFilter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:

			Intent intent = new Intent();
			intent.setClass(MainActivity.this, SettingsActivity.class);
			startActivityForResult(intent, 0);

			return true;
		case R.id.menu_select_bt:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_CONNECTION_STATE,
						BluetoothAdapter.ERROR);
				ConnectivityFragment fragment = (ConnectivityFragment) getSupportFragmentManager()
						.findFragmentByTag("ConnectivityFragment");
				fragment.refreshUI(action, state);
			}

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				ConnectivityFragment fragment = (ConnectivityFragment) getSupportFragmentManager()
						.findFragmentByTag("ConnectivityFragment");
				fragment.refreshUI(action, state);
			}
			
			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				//final int state = intent.getIntExtra(
				//		BluetoothDevice.EXTRA_DEVICE, BluetoothDevice.ERROR);
				ConnectivityFragment fragment = (ConnectivityFragment) getSupportFragmentManager()
						.findFragmentByTag("ConnectivityFragment");
				//fragment.refreshUI(action, state);
				fragment.refreshUI(action, 0);
			}
			
			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				//final int state = intent.getIntExtra(
				//		BluetoothDevice.EXTRA_DEVICE, BluetoothDevice.ERROR);
				ConnectivityFragment fragment = (ConnectivityFragment) getSupportFragmentManager()
						.findFragmentByTag("ConnectivityFragment");
				//fragment.refreshUI(action, state);
				fragment.refreshUI(action, 0);
			}
			
			
		

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBtReceiver);
	}

}
