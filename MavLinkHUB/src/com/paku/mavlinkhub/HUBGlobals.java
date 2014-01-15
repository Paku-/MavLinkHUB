package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.messenger.HUBMessenger;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientBluetooth;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerTCP;
import com.paku.mavlinkhub.queue.msgcenter.MavlinkMsgCenter;

import android.app.Application;
import android.content.Context;
import android.support.v4.view.ViewPager;

public class HUBGlobals extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = "HUBGlobals";

	// constants
	// buffer, stream sizes
	public int visibleBuffersSize = 1024 * 10;
	// public int minStreamReadSize = 2 ^ 4; // ^6 = 64 ^5=32 ^4=16
	public int visibleMsgList = 20;

	// messages handler
	public HUBMessenger messenger;

	// main Drone connector
	public DroneClient droneClient;

	// main GS connector
	public GroundStationServer gsServer;

	// main ItemMavLinkMsg objects queue
	public MavlinkMsgCenter msgCenter;

	// sys log stats holder object
	public HUBLogger logger;

	// we are a Fragment Application
	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	// with initial state as "created"
	public UI_MODE uiMode;

	public void Init(Context mContext) {

		uiMode = UI_MODE.UI_MODE_CREATED;

		// start application asynchronous messaging - has to be first !!!
		messenger = new HUBMessenger(this);

		logger = new HUBLogger(this);

		msgCenter = new MavlinkMsgCenter(this, 200);

		droneClient = new DroneClientBluetooth(messenger.appMsgHandler);

		// server started from the beginning
		gsServer = new GroundStationServerTCP(messenger.appMsgHandler);
		gsServer.startServer(5760);

	}

}
