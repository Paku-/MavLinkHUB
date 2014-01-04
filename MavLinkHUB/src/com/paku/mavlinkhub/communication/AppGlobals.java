package com.paku.mavlinkhub.communication;

import com.paku.mavlinkhub.Logger;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;
import com.paku.mavlinkhub.mavlink.MavLinkCollector;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class AppGlobals extends Application {

	private static final String TAG = "AppGlobals";

	// GUI state machine constants
	public static final int UI_MODE_CREATED = 200;
	public static final int UI_MODE_BT_OFF = 201;
	public static final int UI_MODE_DISCONNECTED = 202;
	public static final int UI_MODE_CONNECTED = 203;
	public static final int UI_MODE_TURNING_ON = 204;
	public static final int UI_MODE_STATE_ON = 205;
	public static final int UI_MODE_STATE_OFF = 206;
	public static final int UI_MODE_TURNING_OFF = 207;
	
	
	// BT Dev List state machine constants
	public static final int LIST_OK = 1;
	public static final int ERROR_NO_ADAPTER = 2;
	public static final int ERROR_ADAPTER_OFF = 3;
	public static final int ERROR_NO_BONDED_DEV = 4;

	// other constants
	public static final int MSG_DATA_READY = 101;
	public static final int REQUEST_ENABLE_BT = 102;

	public Context appContext;
	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	// main BT connector
	public BluetoothConnector mBtConnector;
	public IntentFilter mBtIntentFilter;
	private BroadcastReceiver mBtReceiver;
	
	//MAVLink class holder/object	
	public MavLinkCollector mMavLinkCollector;
	
	//sys log stats holder object


	public Logger logger;

	public int ui_Mode = AppGlobals.UI_MODE_CREATED;


	public void Init(Context mContext) {
		
		//create logger holder		
		logger = new Logger(this);
		//start logging
		logger.restartSysLog();
		logger.restartByteLog();
				

		logger.sys_(TAG, "** MavLinkHUB Init **"); 

		appContext = mContext;

		
		setUiMode(AppGlobals.UI_MODE_CREATED);
		
		// !!! connector has to exist before the MavLink as there is interface to it.
		mBtConnector = new BluetoothConnector();		
		mMavLinkCollector = new MavLinkCollector(appContext);
		
	

		// create BT broadcasts  receiver
		mBtIntentFilter = new IntentFilter();
		mBtIntentFilter
				.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		mBtIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mBtIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		mBtIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

		getBTBroadcasts();

		// register this receiver for intents for BT adapter changes
		registerReceiver(mBtReceiver, mBtIntentFilter);

		/*
		 * if (globalVars.mBtConnector.isConnected() || globalVars.getUiMode()
		 * == AppGlobals.UI_MODE_CONNECTED) {
		 * globalVars.setUiMode(AppGlobals.UI_MODE_CONNECTED); } else
		 * globalVars.setUiMode(AppGlobals.UI_MODE_CREATED);
		 */

	}

	public int getUiMode() {
		return ui_Mode;

	}

	public void setUiMode(int mode) {
		ui_Mode = mode;
	}

	// interface IUiModeChanged
	public IUiModeChanged callerIUiModeChanged = null;

	public void registerForIUiModeChanged(Fragment fragment) {
		callerIUiModeChanged = (IUiModeChanged) fragment;
	}
	
	private void getBTBroadcasts()
	{
		
		// create and register BT BroadcastReceiver
		mBtReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				final String action = intent.getAction();

				if (action
						.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(
							BluetoothAdapter.EXTRA_CONNECTION_STATE,
							BluetoothAdapter.ERROR);

					switch (state) {
					case BluetoothAdapter.STATE_CONNECTING:
						logger.sys_(TAG,
								"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTING");
						break;
					case BluetoothAdapter.STATE_CONNECTED:
						logger.sys_(TAG,
								"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTED");
						break;
					case BluetoothAdapter.STATE_DISCONNECTING:
						logger.sys_(TAG,
								"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTING");
						break;
					case BluetoothAdapter.STATE_DISCONNECTED:
						logger.sys_(TAG,
								"BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTED");
						break;
					}

				}

				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(
							BluetoothAdapter.EXTRA_STATE,
							BluetoothAdapter.ERROR);
					switch (state) {
					case BluetoothAdapter.STATE_OFF:
						logger.sys_(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_OFF");
						setUiMode(AppGlobals.UI_MODE_STATE_OFF);
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						logger.sys_(TAG,
								"BTAdpter [ACTION_STATE_CHANGED]: TURNING_OFF");
						setUiMode(AppGlobals.UI_MODE_TURNING_OFF);
						break;
					case BluetoothAdapter.STATE_ON:
						logger.sys_(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_ON"); // 2nd
																					// after
																					// turning_on
						setUiMode(AppGlobals.UI_MODE_STATE_ON);
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						logger.sys_(TAG,
								"BTAdpter [ACTION_STATE_CHANGED]: TURNING_ON"); // 1st
																				// on
																				// bt
																				// enable
						setUiMode(AppGlobals.UI_MODE_TURNING_ON);
						break;
					default:
						logger.sys_(TAG, "BTAdpter [ACTION_STATE_CHANGED]: unknown");
						break;
					}

				}

				if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
					logger.sys_(TAG, "BTDevice [ACTION_ACL_CONNECTED]");
					setUiMode(AppGlobals.UI_MODE_CONNECTED);
				}

				if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					logger.sys_(TAG, "BTDevice [ACTION_ACL_DISCONNECTED]");
					setUiMode(AppGlobals.UI_MODE_DISCONNECTED);
				}

				if (callerIUiModeChanged != null)
					callerIUiModeChanged.onUiModeChanged();

			}
		};
		
	}

}
