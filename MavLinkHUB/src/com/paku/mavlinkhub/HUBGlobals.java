package com.paku.mavlinkhub;

import com.ftdi.j2xx.D2xxManager;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.messenger.HUBMessenger;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientBluetooth;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientUSB;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerTCP;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerUDP;
import com.paku.mavlinkhub.queue.hub.HUBQueue;
import com.paku.mavlinkhub.utils.HUBLogger;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;

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
	public static final int visibleByteLogSize = 256 * 4;
	public static final int visibleMsgList = 50;
	public static final int serverTCP_port = 5760;
	public static final int serverUDP_port = 14550;

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

		// Default is the BT client
		droneClient = new DroneClientBluetooth(this);

		//TCP server
		// server started from the beginning
		gsServer = new GroundStationServerTCP(this);
		// start listening on configured port.
		gsServer.startServer(HUBGlobals.serverTCP_port);

		//UDP server
		// server started from the beginning
		gsServer = new GroundStationServerUDP(this);
		// start listening on configured port.
		gsServer.startServer(HUBGlobals.serverUDP_port);

		// finally start parsers and distributors
		queue = new HUBQueue(this, 1000);
		queue.startQueue();

	}

	public void switchClient(ItemPeerDevice newDevice) {

		if (null != droneClient) {
			droneClient.stopClient();
		}

		DEVICE_INTERFACE devs[] = DEVICE_INTERFACE.values();

		switch (devs[newDevice.getDevInterface().ordinal()]) {
		case Bluetooth:
			droneClient = new DroneClientBluetooth(this);
			break;
		case USB:
			droneClient = new DroneClientUSB(this);
			break;
		default:
			break;
		}

		droneClient.setMyPeerDevice(newDevice);
		droneClient.startClient(newDevice);

	}

}
