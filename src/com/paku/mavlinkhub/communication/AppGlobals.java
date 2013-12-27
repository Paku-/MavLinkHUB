package com.paku.mavlinkhub.communication;

import com.paku.mavlinkhub.interfaces.IUiModeChanged;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

public class AppGlobals extends Application {

	@SuppressWarnings("unused")
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
	public static final int MESSAGE_READ = 101;
	public static final int REQUEST_ENABLE_BT = 102;

	public Context appContext;

	public int ui_Mode = AppGlobals.UI_MODE_CREATED;

	// main BT connector
	public BluetoothConnector mBtConnector;

	public void Init(Context mConext) {
		appContext = mConext;
		mBtConnector = new BluetoothConnector();

	}

	public int getUiMode() {
		return ui_Mode;

	}

	public void setUiMode(int mode) {
		ui_Mode = mode;
	}

	public IUiModeChanged callerIUiModeChanged;

	public void registerForIUiModeChanged(Fragment fragment) {
		callerIUiModeChanged = (IUiModeChanged) fragment;
	}

}
