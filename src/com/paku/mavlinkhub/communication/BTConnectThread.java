package com.paku.mavlinkhub.communication;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

public class BTConnectThread extends Thread {
	private static final String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";
	private final BluetoothAdapter mmBluetoothAdapter;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private Context hubContext;

	public BTConnectThread(BluetoothAdapter adapter, BluetoothDevice device,
			Context context) {

		BluetoothSocket tmp = null;
		mmBluetoothAdapter = adapter;
		mmDevice = device;
		hubContext = context;

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
			Log.d("BT", "Connecting..");
			mmSocket.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				Log.d("BT", connectException.getMessage());
				mmSocket.close();
			} catch (IOException closeException) {
				Log.d("BT", closeException.getMessage());
			}
			return;
		}

		Log.d("BT", "Connected..");

		// Do work to manage the connection (in a separate thread)
		CommunicationHUB comHUB = (CommunicationHUB) hubContext;
		comHUB.StartTransmission(mmSocket);
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void disconnect() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.d("BTconnthread", e.getMessage());
		}
	}
}