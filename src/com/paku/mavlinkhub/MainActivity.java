package com.paku.mavlinkhub;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.fragments.ConnectivityFragment;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	AppGlobals comHUB; // global vars and constants object.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		comHUB = (AppGlobals) this.getApplication();
		comHUB.Init(this);

		if (comHUB.mBtConnector.isConnected()
				|| comHUB.getUiMode() == AppGlobals.UI_MODE_CONNECTED) {
			comHUB.setUiMode(AppGlobals.UI_MODE_CONNECTED);
		} else
			comHUB.setUiMode(AppGlobals.UI_MODE_CREATED);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mFragmentsPagerAdapter = new FragmentsAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mFragmentsPagerAdapter);

		IntentFilter BtIntentFilter = new IntentFilter();
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
					Log.d(TAG,
							"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTING");
					break;
				case BluetoothAdapter.STATE_CONNECTED:
					Log.d(TAG,
							"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTED");
					break;
				case BluetoothAdapter.STATE_DISCONNECTING:
					Log.d(TAG,
							"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTING");
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					Log.d(TAG,
							"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTED");
					break;
				}

			}

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
				switch (state) {
				case BluetoothAdapter.STATE_OFF:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_OFF");
					comHUB.setUiMode(AppGlobals.UI_MODE_STATE_OFF);
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: TURNING_OFF");
					comHUB.setUiMode(AppGlobals.UI_MODE_TURNING_OFF);
					break;
				case BluetoothAdapter.STATE_ON:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_ON"); // 2nd
																				// after
																				// turning_on
					comHUB.setUiMode(AppGlobals.UI_MODE_STATE_ON);
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: TURNING_ON"); // 1st
																				// on
																				// bt
																				// enable
					comHUB.setUiMode(AppGlobals.UI_MODE_TURNING_ON);
					break;
				default:
					Log.d(TAG, "BTAdpter [ACTION_STATE_CHANGED]: unknown");
					break;
				}

			}

			if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				Log.d(TAG, "BTDevice [ACTION_ACL_CONNECTED]");
				comHUB.setUiMode(AppGlobals.UI_MODE_CONNECTED);
			}

			if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				Log.d(TAG, "BTDevice [ACTION_ACL_DISCONNECTED]");
				comHUB.setUiMode(AppGlobals.UI_MODE_DISCONNECTED);
			}

			// recreate fragments for pos 0,1,2
			ConnectivityFragment fragment = (ConnectivityFragment) mViewPager
					.getAdapter().instantiateItem(mViewPager, 0);
			fragment.refreshUI();
			// fragment = (ConnectivityFragment)
			// mViewPager.getAdapter().instantiateItem(mViewPager, 1);
			// fragment.refreshUI();
			// fragment = (ConnectivityFragment)
			// mViewPager.getAdapter().instantiateItem(mViewPager, 2);
			// fragment.refreshUI();

			// redraw UI
			// mViewPager.getAdapter().notifyDataSetChanged();

			// mViewPager.getAdapter().instantiateItem(mViewPager, 0);

		}
	};

	public void CloseMe() {

		OnClickListener positiveButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				comHUB.mBtConnector.closeConnection();
				finish();
			}
		};

		OnClickListener negativeButtonClickListener = new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};

		if (comHUB.mBtConnector.isConnected()) {

			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setTitle(getString(R.string.close_dlg_title_mavlink_closing)+"["+comHUB.mBtConnector.getPeerName()+"]");
			dlg.setMessage(R.string.close_dlg_msg_current_connection_will_be_lost);
			dlg.setCancelable(false);
			dlg.setPositiveButton(R.string.close_dlg_positive,
					positiveButtonClickListener);
			dlg.setNegativeButton(R.string.close_dlg_negative,
					negativeButtonClickListener);
			dlg.create();
			dlg.show();

		}
		else finish();

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		CloseMe();

	}

}
