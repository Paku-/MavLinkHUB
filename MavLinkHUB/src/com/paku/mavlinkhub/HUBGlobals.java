package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.messenger.HUBMessenger;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientBluetooth;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerTCP;
import com.paku.mavlinkhub.queue.hub.HUBQueue;
import com.paku.mavlinkhub.utils.HUBLogger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;

public class HUBGlobals extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = HUBGlobals.class.getSimpleName();

	// constants
	// buffer, stream sizes
	public int visibleByteLogSize = 256 * 8;
	public int visibleMsgList = 50;
	public int serverTCP_port = 5760;

	// messages handler
	public HUBMessenger messenger;

	// main Drone connector
	public DroneClient droneClient;

	// main GS connector
	public GroundStationServer gsServer;

	// main ItemMavLinkMsg objects queue
	public HUBQueue queue;

	// sys log stats holder object
	public HUBLogger logger;

	// we are a Fragment Application
	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	public SharedPreferences prefs;

	// with initial state as "created"
	public UI_MODE uiMode;

	public void hubInit(Context mContext) {

		uiMode = UI_MODE.UI_MODE_CREATED;

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// start application asynchronous messaging - has to be first !!!
		messenger = new HUBMessenger(this);

		logger = new HUBLogger(this);

		// create client - by default BT (not connected)
		droneClient = new DroneClientBluetooth(messenger.appMsgHandler);

		// server started from the beginning
		gsServer = new GroundStationServerTCP(messenger.appMsgHandler);
		// start listening on configured port.
		gsServer.startServer(serverTCP_port);

		// finally start parsers and distributors
		queue = new HUBQueue(this, 1000);
		queue.startQueue();

	}

}
