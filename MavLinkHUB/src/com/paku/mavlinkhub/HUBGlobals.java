package com.paku.mavlinkhub;

import com.paku.mavlinkhub.communication.connector.ConnectorBluetooth;
import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.mavlink.MavLinkCollector;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;

public class HUBGlobals extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = "HUBGlobals";

	// other constants
	public static final int MSG_CONNECTOR_DATA_READY = 101;
	public static final int MSG_CONNECTOR_CONNECTION_FAILED = 1001;
	public static final int MSG_MAVLINK_MSG_READY = 102;
	public static final int MSG_DATA_READY_SYSLOG = 103;
	public static final int MSG_DATA_READY_BYTELOG = 104;
	public static final int REQUEST_ENABLE_BT = 111;

	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	// messages handler
	public HUBMessenger messanger;

	// main BT connector
	public ConnectorBluetooth connectorBluetooth;

	// MAVLink class holder/object
	public MavLinkCollector mMavLinkCollector;

	// sys log stats holder object
	public HUBLogger logger;

	public UI_MODE uiMode = UI_MODE.UI_MODE_CREATED;

	// buffer, stream sizes
	public int visibleBuffersSize = 1024 * 10;
	public int minStreamReadSize = 2 ^ 4; // ^6 = 64 ^5=32 ^4=16
	public int visibleMsgList = 20;

	public void Init(Context mContext) {

		// start application asynchronous messaging hub
		// has to be first !!!
		messanger = new HUBMessenger(this);

		logger = new HUBLogger(this);

		uiMode = UI_MODE.UI_MODE_CREATED;

		// !!! connector has to exist before the MavLink as there is interface
		// to it.
		connectorBluetooth = new ConnectorBluetooth(messanger.appMsgHandler);
		mMavLinkCollector = new MavLinkCollector(this);

	}

}
