package com.paku.mavlinkhub;

import com.paku.mavlinkhub.communication.connectors.DroneConnector;
import com.paku.mavlinkhub.communication.connectors.DroneConnectorBluetooth;
import com.paku.mavlinkhub.communication.connectors.ProxyServerTCP;
import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.mavlink.MavLinkCollector;

import android.app.Application;
import android.content.Context;
import android.support.v4.view.ViewPager;

public class HUBGlobals extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = "HUBGlobals";

	// other constants
	public static final int MSG_SOCKET_BT_DATA_READY = 101;
	public static final int MSG_SOCKET_BT_CLOSED = 103;
	public static final int MSG_SOCKET_TCP_DATA_READY = 105;
	public static final int MSG_SOCKET_TCP_CLOSED = 107;

	public static final int MSG_DRONE_CONNECTION_FAILED = 109;
	public static final int MSG_MAVLINK_MSGITEM_READY = 111;
	public static final int MSG_DATA_UPDATE_SYSLOG = 113;
	public static final int MSG_DATA_UPDATE_BYTELOG = 115;
	public static final int MSG_DATA_UPDATE_STATS = 117;
	public static final int MSG_CONNECTOR_STOP_HANDLER = 119;
	public static final int REQUEST_ENABLE_BT = 120;

	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	// messages handler
	public HUBMessenger messanger;

	// main Drone connector
	public DroneConnector droneConnector;

	// main BT connector
	public ProxyServerTCP proxyServer;

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
		droneConnector = new DroneConnectorBluetooth(messanger.appMsgHandler);
		mMavLinkCollector = new MavLinkCollector(this);

		proxyServer = new ProxyServerTCP(messanger.appMsgHandler);
		proxyServer.startServer(35000);
	}

}
