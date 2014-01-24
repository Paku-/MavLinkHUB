package com.paku.mavlinkhub.queue.endpoints.drone;

import java.io.IOException;

import com.ftdi.j2xx.FT_Device;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.utils.ThreadSocketReader;
import com.paku.mavlinkhub.utils.ThreadUSBReader;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class DroneClientUSB extends DroneClient {

	private static final String TAG = DroneClientUSB.class.getSimpleName();
	private static final int SIZEBUFF = 1024;

	private Handler handlerDroneMsgRead;

	protected FT_Device usbDevice;

	private ThreadUSBReader usbClientReaderThread;

	public DroneClientUSB(HUBGlobals hub) {
		super(hub, SIZEBUFF);
	}

	@Override
	public void startClient(ItemPeerDevice drone) {

		if (drone.getDevInterface() == DEVICE_INTERFACE.USB) {

			handlerDroneMsgRead = startInputQueueMsgHandler();

			usbDevice = HUBGlobals.usbHub.openBySerialNumber(hub, drone.getAddress());

			usbClientReaderThread = new ThreadUSBReader(usbDevice, handlerDroneMsgRead);
			usbClientReaderThread.start();
		}

		return;

	}

	@Override
	public void stopClient() {
		Log.d(TAG, "Closing connection..");

		// stop handler
		if (handlerDroneMsgRead != null) {
			handlerDroneMsgRead.removeMessages(0);
		}

		// stop thread
		if (isConnected()) {
			usbClientReaderThread.stopMe();
		}
	}

	@Override
	public boolean isConnected() {
		if (null == usbClientReaderThread) {
			return false;
		}
		else {
			return usbClientReaderThread.isRunning();
		}

	}

	@Override
	public String getPeerName() {

		// return (isConnected()) ? mBluetoothSocket.getRemoteDevice().getName()
		// : "";
		return "";

	}

	@Override
	public String getPeerAddress() {
		// return (isConnected()) ?
		// mBluetoothSocket.getRemoteDevice().getAddress() : "";
		return "";

	}

	@Override
	public String getMyName() {
		// return mBluetoothAdapter.getName();
		return "";
	}

	@Override
	public String getMyAddress() {
		// return mBluetoothAdapter.getAddress();
		return "";
	}

	@Override
	public boolean writeBytes(byte[] bytes) throws IOException {

		if (isConnected()) {
			usbClientReaderThread.writeBytes(bytes);
			return true;
		}
		return false;
	}

}
