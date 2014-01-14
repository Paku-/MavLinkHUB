package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.mavlink.MavLinkCollector;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientBluetooth;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerTCP;

import android.app.Application;
import android.content.Context;
import android.support.v4.view.ViewPager;

public class HUBGlobals extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = "HUBGlobals";

	// other constants

	// messages handler
	public HUBMessenger messenger;

	// main Drone connector
	public DroneClient droneClient;

	// main GS connector
	public GroundStationServer gsServer;

	// main ItemMavLinkMsg objects queue
	public HubQueue hubQueue;

	// MAVLink class fields names holder/object
	public MavLinkCollector mMavLinkCollector;

	// sys log stats holder object
	public HUBLogger logger;

	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;
	public UI_MODE uiMode = UI_MODE.UI_MODE_CREATED;

	// buffer, stream sizes
	public int visibleBuffersSize = 1024 * 10;
	// public int minStreamReadSize = 2 ^ 4; // ^6 = 64 ^5=32 ^4=16
	public int visibleMsgList = 20;

	public void Init(Context mContext) {

		// start application asynchronous messaging - has to be first !!!
		messenger = new HUBMessenger(this);

		hubQueue = new HubQueue(this, 10);

		logger = new HUBLogger(this);

		uiMode = UI_MODE.UI_MODE_CREATED;

		// !!! connector has to exist before the MavLink as there is interface
		// to it.
		droneClient = new DroneClientBluetooth(messenger.appMsgHandler);

		// server started from the beginning
		gsServer = new GroundStationServerTCP(messenger.appMsgHandler);
		gsServer.startServer(35000);

		mMavLinkCollector = new MavLinkCollector(this);
	}

}
