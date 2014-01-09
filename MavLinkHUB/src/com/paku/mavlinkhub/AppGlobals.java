package com.paku.mavlinkhub;

import com.paku.mavlinkhub.communication.ConnectorBluetooth;
import com.paku.mavlinkhub.enums.UI_MODE;
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

public class AppGlobals extends Application {

	private static final String TAG = "AppGlobals";

	// other constants
	public static final int MSG_CONNECTOR_DATA_READY = 101;
	public static final int MSG_MAVLINK_MSG_READY = 102;
	public static final int MSG_SYSLOG_DATA_READY = 103;
	public static final int MSG_BYTELOG_DATA_READY = 104;
	public static final int REQUEST_ENABLE_BT = 111;

	public Context appContext;
	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	// main BT connector
	public ConnectorBluetooth mBtConnector;
	public IntentFilter mBtIntentFilter;
	private BroadcastReceiver mBtReceiver;

	// MAVLink class holder/object
	public MavLinkCollector mMavLinkCollector;

	// sys log stats holder object

	public Logger logger;

	public UI_MODE uiMode = UI_MODE.UI_MODE_CREATED;

	// buffer, stream sizes
	public int visibleBuffersSize = 1024 * 10;
	public int minStreamReadSize = 2 ^ 4; // ^6 = 64 ^5=32 ^4=16
	public int visibleMsgList = 20;

	public void Init(Context mContext) {

		// create logger holder
		logger = new Logger(this);
		// start logging
		logger.restartSysLog();
		logger.restartByteLog();

		logger.sysLog(TAG, "** MavLinkHUB Init **");

		appContext = mContext;

		setUiMode(UI_MODE.UI_MODE_CREATED);

		// !!! connector has to exist before the MavLink as there is interface
		// to it.
		mBtConnector = new ConnectorBluetooth();
		mMavLinkCollector = new MavLinkCollector(appContext);

		// create BT broadcasts receiver
		mBtIntentFilter = new IntentFilter();
		mBtIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
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

	public UI_MODE getUiMode() {
		return uiMode;

	}

	public void setUiMode(UI_MODE uiMode) {
		this.uiMode = uiMode;
	}

	// interface IUiModeChanged
	public IUiModeChanged callerIUiModeChanged = null;

	public void registerForIUiModeChanged(Fragment fragment) {
		callerIUiModeChanged = (IUiModeChanged) fragment;
	}

	private void getBTBroadcasts() {

		// create and register BT BroadcastReceiver
		mBtReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				final String action = intent.getAction();

				if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
							BluetoothAdapter.ERROR);

					switch (state) {
					case BluetoothAdapter.STATE_CONNECTING:
						logger.sysLog(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTING");
						break;
					case BluetoothAdapter.STATE_CONNECTED:
						logger.sysLog(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_CONNECTED");
						break;
					case BluetoothAdapter.STATE_DISCONNECTING:
						logger.sysLog(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTING");
						break;
					case BluetoothAdapter.STATE_DISCONNECTED:
						logger.sysLog(TAG, "BTAdpter [ACTION_CONNECTION_STATE_CHANGED]: STATE_DISCONNECTED");
						break;
					}

				}

				if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
					final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
					switch (state) {
					case BluetoothAdapter.STATE_OFF:
						logger.sysLog(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_OFF");
						setUiMode(UI_MODE.UI_MODE_STATE_OFF);
						break;
					case BluetoothAdapter.STATE_TURNING_OFF:
						logger.sysLog(TAG, "BTAdpter [ACTION_STATE_CHANGED]: TURNING_OFF");
						setUiMode(UI_MODE.UI_MODE_TURNING_OFF);
						break;
					case BluetoothAdapter.STATE_ON:
						logger.sysLog(TAG, "BTAdpter [ACTION_STATE_CHANGED]: STATE_ON"); // 2nd
						// after
						// turning_on
						setUiMode(UI_MODE.UI_MODE_STATE_ON);
						break;
					case BluetoothAdapter.STATE_TURNING_ON:
						logger.sysLog(TAG, "BTAdpter [ACTION_STATE_CHANGED]: TURNING_ON"); // 1st
																							// on
																							// bt
																							// enable
						setUiMode(UI_MODE.UI_MODE_TURNING_ON);
						break;
					default:
						logger.sysLog(TAG, "BTAdpter [ACTION_STATE_CHANGED]: unknown");
						break;
					}

				}

				if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
					BluetoothDevice connDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					logger.sysLog(TAG,
							"[ACTION_ACL_CONNECTED] " + connDevice.getName() + " [" + connDevice.getAddress() + "]");

					setUiMode(UI_MODE.UI_MODE_CONNECTED);
				}

				if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					BluetoothDevice connDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					logger.sysLog(TAG,
							"[ACTION_ACL_DISCONNECTED] " + connDevice.getName() + " [" + connDevice.getAddress() + "]");

					setUiMode(UI_MODE.UI_MODE_DISCONNECTED);
				}

				if (callerIUiModeChanged != null) callerIUiModeChanged.onUiModeChanged();

			}
		};

	}

}
