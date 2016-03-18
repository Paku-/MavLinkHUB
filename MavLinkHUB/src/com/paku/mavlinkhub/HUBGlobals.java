// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub;

import com.ftdi.j2xx.D2xxManager;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.messenger.HUBMessenger;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientBluetooth;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientUSB;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerTCP;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerUDP;
import com.paku.mavlinkhub.queue.hub.HUBQueue;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;
import com.paku.mavlinkhub.utils.HUBLogger;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class HUBGlobals extends Application {

	private static final String TAG = HUBGlobals.class.getSimpleName();

	// constants
	// buffer, stream sizes
	public static final int visibleByteLogSize = 256 * 4;
	public static final int visibleMsgList = 50;
	public static final int serverTCP_port = 5760;
	public static final int serverUDP_port = 14550;

	// messages handler
	public static HUBMessenger messenger;

	// main Drone connector
	public DroneClient droneClient;

	// main GS connector
	public GroundStationServer gcsServer;

	// main ItemMavLinkMsg objects queue
	public HUBQueue queue;

	// sys log stats holder object
	public static HUBLogger logger;

	public static D2xxManager usbHub = null;

	public SharedPreferences prefs;

	// with initial state as "created"
	public UI_MODE uiMode;

	public void hubInit(Context mContext) {

		uiMode = UI_MODE.UI_MODE_CREATED;

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// start application asynchronous messaging - has to be first !!!
		messenger = new HUBMessenger(this);

		//start system wide logger
		logger = new HUBLogger(this);

		/*
		<usb-device vendor-id="1027" product-id="24577" /> <!-- FT232RL -->
		<usb-device vendor-id="1027" product-id="24596" /> <!-- FT232H -->
		<usb-device vendor-id="1027" product-id="24597" /> <!-- FT231X 0x0403 / 0x6015 -->
		<usb-device vendor-id="1027" product-id="24592" /> <!-- FT2232C/D/HL -->
		<usb-device vendor-id="1027" product-id="24593" /> <!-- FT4232HL -->
		<usb-device vendor-id="1027" product-id="24597" /> <!-- FT230X -->
		<usb-device vendor-id="1412" product-id="45088" /> <!-- REX-USB60F -->
		<usb-device vendor-id="9025" product-id="16" /><!-- APM 2.5 device -->    
		<usb-device vendor-id="9900" product-id="16" /><!-- APM 2.5 device -->
		<usb-device vendor-id="5824" product-id="1155" /><!--  Teensyduino  -->
		<usb-device vendor-id="4292" product-id="60000" /><!-- CP210x UART Bridge -->
		<usb-device vendor-id="1118" product-id="688"/>		
		*/

		//start USB driver
		try {
			usbHub = D2xxManager.getInstance(this);
			// setup the additional VIDPIDPAIRs 
			if (!usbHub.setVIDPID(0x2341, 0x0010)) Log.d(TAG, "APM 2.5 VIDPID1 setting error");
			if (!usbHub.setVIDPID(0x26ac, 0x0010)) Log.d(TAG, "APM 2.5 VIDPID2 setting error");

		}
		catch (D2xxManager.D2xxException ex) {
			ex.printStackTrace();
		}

		// Default is the BT client
		droneClient = new DroneClientBluetooth(this);

		// switch server, if it's null start UDP as default.
		switchServer();

		// finally start parsers and distributors
		queue = new HUBQueue(this, 1000);
		queue.startQueue();

	}

	public void switchServer() {

		if (null != gcsServer) {
			gcsServer.stopServer();

			if (gcsServer.getClass().equals(GroundStationServerTCP.class)) {
				gcsServer = new GroundStationServerUDP(this);
				gcsServer.startServer(HUBGlobals.serverUDP_port);
				return;
			}

			if (gcsServer.getClass().equals(GroundStationServerUDP.class)) {
				gcsServer = new GroundStationServerTCP(this);
				gcsServer.startServer(HUBGlobals.serverTCP_port);
				return;
			}
		}
		else {

			gcsServer = new GroundStationServerUDP(this);
			gcsServer.startServer(HUBGlobals.serverUDP_port);

		}

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

	//let's have the app wide STATIC messaging method for our children/application classes
	public static final void sendAppMsg(APP_STATE msgId, String msgTxt) {
		messenger.appMsgHandler.obtainMessage(msgId.ordinal(), msgTxt.length(), -1, msgTxt.getBytes()).sendToTarget();
	}

	public static final void sendAppMsg(APP_STATE msgId) {
		messenger.appMsgHandler.obtainMessage(msgId.ordinal()).sendToTarget();
	}

	public static final void sendAppMsg(APP_STATE msgId, Message msg) {
		messenger.appMsgHandler.obtainMessage(msgId.ordinal(), msg.arg1, msg.arg2, msg.obj).sendToTarget();
	}

	public static void sendAppMsg(APP_STATE msgId, ItemMavLinkMsg item) {
		messenger.appMsgHandler.obtainMessage(msgId.ordinal(), -1, -1, item).sendToTarget();
	}

}
