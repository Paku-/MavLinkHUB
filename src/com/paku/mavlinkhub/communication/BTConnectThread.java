package com.paku.mavlinkhub.communication;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BTConnectThread extends Thread {
	private static final String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";
	private static final String TAG = "BTConnectThread";
	private final BluetoothAdapter mmBluetoothAdapter;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private BluetoothConnector parentBtConnector;

	public BTConnectThread(BluetoothAdapter adapter, BluetoothDevice device,
			BluetoothConnector parent) {

		BluetoothSocket tmp = null;
		mmBluetoothAdapter = adapter;
		mmDevice = device;
		parentBtConnector = parent;

		try {
			tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID
					.fromString(UUID_SPP));
		} catch (IOException e) {
		}
		mmSocket = tmp;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		mmBluetoothAdapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			Log.d(TAG, "Connecting socket..");
			mmSocket.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			Log.d(TAG,
					"Exception: [run.connect]" + connectException.getMessage());
			try {
				mmSocket.close();
			} catch (IOException closeException) {
				Log.d(TAG,
						"Exception: [run.close]" + closeException.getMessage());
			}
			return;
		}

		Log.d(TAG, "Connected..");

		// Do work to manage the connection (in a separate thread)
		parentBtConnector.startTransmission(mmSocket);
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void disconnect() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.d(TAG, "Exception [disconnect]: " + e.getMessage());
		}
	}
}