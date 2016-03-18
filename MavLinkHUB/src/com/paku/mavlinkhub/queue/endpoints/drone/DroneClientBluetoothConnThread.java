package com.paku.mavlinkhub.queue.endpoints.drone;

import java.io.IOException;
import java.util.UUID;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

class DroneClientBluetoothConnThread extends Thread {

	private static final String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";

	private static final String TAG = DroneClientBluetoothConnThread.class.getSimpleName();

	private final BluetoothAdapter mmBluetoothAdapter;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private final DroneClientBluetooth parentConnector;

	DroneClientBluetoothConnThread(DroneClientBluetooth parent, BluetoothAdapter adapter, BluetoothDevice device) {

		BluetoothSocket tmp = null;
		mmBluetoothAdapter = adapter;
		mmDevice = device;
		parentConnector = parent;

		try {
			tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_SPP));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		mmSocket = tmp;
	}

	public void run() {

		// Cancel discovery because it will slow down the connection
		mmBluetoothAdapter.cancelDiscovery();

		try {
			Log.d(TAG, "Connecting socket..");
			mmSocket.connect();
		}
		catch (IOException connectException) {

			Log.d(TAG, "Exception: [Failed Connection Attempt]" + connectException.getMessage());
			try {
				mmSocket.close();
				final String msgTxt = connectException.getMessage();
				HUBGlobals.sendAppMsg(APP_STATE.MSG_DRONE_CONNECTION_ATTEMPT_FAILED, msgTxt);
			}
			catch (IOException closeException) {
				Log.d(TAG, "Exception: [Failed Connection Attempt: close failed as well]" + closeException.getMessage());
			}
			return;
		}

		Log.d(TAG, "Connected..");
		// start Receiver on socket
		parentConnector.startClientReaderThread(mmSocket);
	}
}