// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.endpoints.drone;

import java.io.IOException;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.utils.ThreadReaderUSB;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDeviceUSB;

import android.util.Log;

public class DroneClientUSB extends DroneClient {

	private static final String TAG = DroneClientUSB.class.getSimpleName();
	private static final int SIZEBUFF = 1024;

	protected FT_Device usbDevice;

	private ThreadReaderUSB readerThreadUSB;

	public DroneClientUSB(HUBGlobals hub) {
		super(hub, SIZEBUFF);
	}

	@Override
	public void startClient(ItemPeerDevice drone) {

		if (drone.getDevInterface() == DEVICE_INTERFACE.USB) {

			usbDevice = HUBGlobals.usbHub.openByLocation(hub, ((ItemPeerDeviceUSB) drone).getLocation());

			usbDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
			usbDevice.setBaudRate(Integer.valueOf(hub.prefs.getString("pref_usb_baud", "115200")));
			usbDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
			usbDevice.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x0b, (byte) 0x0d);

			usbDevice.setLatencyTimer((byte) 16);
			// ftDev.setReadTimeout(1000);

			usbDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

			readerThreadUSB = new ThreadReaderUSB(usbDevice, ConnMsgHandler);
			readerThreadUSB.start();
			HUBGlobals.sendAppMsg(APP_STATE.MSG_DRONE_CONNECTED);
		}
		else {
			Log.d(TAG, "Wrong client start-up parameter type:" + drone.getDevInterface().toString());
		}

		return;

	}

	@Override
	public void stopClient() {
		Log.d(TAG, "Closing connection..");

		stopMsgHandler();

		// stop thread
		if (isConnected()) {
			readerThreadUSB.stopMe();
		}

		HUBGlobals.sendAppMsg(APP_STATE.MSG_DRONE_DISCONNECTED);
	}

	@Override
	public boolean isConnected() {
		if (null == readerThreadUSB) {
			return false;
		}
		else {
			return readerThreadUSB.isRunning();
		}

	}

	@Override
	public String getPeerName() {
		return (isConnected()) ? getMyPeerDevice().getName() : "";
	}

	@Override
	public String getPeerAddress() {
		return (isConnected()) ? getMyPeerDevice().getAddress() : "";
	}

	@Override
	public String getMyName() {
		//return D2xxManager.class.getSimpleName();
		return "FTDI";
	}

	@Override
	public String getMyAddress() {
		//return "FTDI Lib v." + String.valueOf(D2xxManager.getLibraryVersion());
		return " v." + String.valueOf(D2xxManager.getLibraryVersion());
	}

	@Override
	public boolean writeBytes(byte[] bytes) throws IOException {

		if (isConnected()) {
			readerThreadUSB.writeBytes(bytes);
			return true;
		}
		return false;
	}

}
