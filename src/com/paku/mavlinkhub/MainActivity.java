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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	
	private static final String TAG = "MainActivity";	
	
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

		if (comHUB.mBtConnector.IsConnected()) 
		{
			comHUB.setUiMode(CommunicationHUB.UI_MODE_CONNECTED);			
		}else	
			comHUB.setUiMode(CommunicationHUB.UI_MODE_CREATED);
		

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBtReceiver);
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

				switch (state) {
				case BluetoothAdapter.STATE_CONNECTING:
					Log.d(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTING");										
					break;
				case BluetoothAdapter.STATE_CONNECTED:
					Log.d(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTED");					
					break;
				case BluetoothAdapter.STATE_DISCONNECTING:
					Log.d(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTING");					
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					Log.d(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTED");					
					break;
				}

			}

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_OFF");
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: TURNING_OFF");
					break;
				case BluetoothAdapter.STATE_ON:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_ON");					
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: TURNING_ON");
					break;
				default:
					Log.d(TAG, "BTAdpter [State]: unknown");
					break;
				}

			}

			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				comHUB.setUiMode(CommunicationHUB.UI_MODE_CONNECTED);
			}

			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				comHUB.setUiMode(CommunicationHUB.UI_MODE_DISCONNECTED);
			}

			//recreate fragments for pos 0,1,2			
			ConnectivityFragment fragment = (ConnectivityFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 0);
			fragment.refreshUI();
			//fragment = (ConnectivityFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 1);
			//fragment.refreshUI();
			//fragment = (ConnectivityFragment) mViewPager.getAdapter().instantiateItem(mViewPager, 2);			
			//fragment.refreshUI();
			
			
			//redraw UI
			//mViewPager.getAdapter().notifyDataSetChanged();

			//mViewPager.getAdapter().instantiateItem(mViewPager, 0);

		}
	};



}
