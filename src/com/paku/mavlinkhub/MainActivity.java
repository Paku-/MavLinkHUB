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

	private static final int UI_MODE_NEW = 200;
	private static final int UI_MODE_DISCONNECTED = 201;
	private static final int UI_MODE_CONNECTED = 202;

	FragmentsAdapter mFragmentsPagerAdapter;
	ViewPager mViewPager;
	CommunicationHUB comHUB;
	int ui_Mode = UI_MODE_NEW;

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

		if (comHUB.IsConnected()) 
		{
			ui_Mode = UI_MODE_CONNECTED;			
		}else
			ui_Mode = UI_MODE_NEW;		
		

		IntentFilter BtIntentFilter = new IntentFilter();
		// BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		BtIntentFilter
				.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
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
/*
				switch (state) {
				case BluetoothAdapter.STATE_CONNECTING:
					// button.setVisibility(View.INVISIBLE);
					break;
				case BluetoothAdapter.STATE_CONNECTED:
					// button.setVisibility(View.VISIBLE);
					break;
				case BluetoothAdapter.STATE_DISCONNECTING:
					// button.setVisibility(View.VISIBLE);
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					// button.setVisibility(View.INVISIBLE);
					break;
				}
*/
			}

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
/*
				switch (state) {
				case BluetoothAdapter.STATE_OFF:

					break;
				case BluetoothAdapter.STATE_TURNING_OFF:

					break;
				case BluetoothAdapter.STATE_ON:

					break;
				case BluetoothAdapter.STATE_TURNING_ON:

					break;
				}
*/
			}

			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				ui_Mode = UI_MODE_CONNECTED;
			}

			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				ui_Mode = UI_MODE_DISCONNECTED;
			}
			
			//redraw UI
			//mViewPager.getAdapter().notifyDataSetChanged();
			
			ConnectivityFragment fragment = (ConnectivityFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 0);			
			fragment.refreshUI();

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBtReceiver);
	}

	public int getUiMode() {
		return ui_Mode;
		
	}

}
