package com.paku.mavlinkhub;

import com.ftdi.j2xx.D2xxManager;
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
import android.util.Log;

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

	public static D2xxManager usbHub = null;

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

		try {
			usbHub = D2xxManager.getInstance(this);
		}
		catch (D2xxManager.D2xxException ex) {
			ex.printStackTrace();
		}

		// setup the additinal VIDPIDPAIR
		if (!usbHub.setVIDPID(0x0403, 0xada1)) Log.i("ftd2xx-java", "setVIDPID Error");

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
