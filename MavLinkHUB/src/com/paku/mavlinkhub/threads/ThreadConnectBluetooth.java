package com.paku.mavlinkhub.threads;

import java.io.IOException;
import java.util.UUID;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.communication.Connector;
import com.paku.mavlinkhub.communication.ConnectorBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ThreadConnectBluetooth extends Thread {
	private static final String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";
	private static final String TAG = "ThreadConnectBluetooth";
	private final BluetoothAdapter mmBluetoothAdapter;
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private ConnectorBluetooth parentConnector;

	public ThreadConnectBluetooth(ConnectorBluetooth parent, BluetoothAdapter adapter, BluetoothDevice device) {

		BluetoothSocket tmp = null;
		mmBluetoothAdapter = adapter;
		mmDevice = device;
		parentConnector = parent;

		try {
			tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_SPP));
		}
		catch (IOException e) {
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
		}
		catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			Log.d(TAG, "Exception: [Failed Connection Attempt]" + connectException.getMessage());
			try {
				mmSocket.close();
				String msgTxt = connectException.getMessage();
				parentConnector.appMsgHandler.obtainMessage(AppGlobals.MSG_CONNECTOR_CONNECTION_FAILED,
						msgTxt.length(), -1, msgTxt.getBytes()).sendToTarget();
			}
			catch (IOException closeException) {
				Log.d(TAG, "Exception: [Failed Connection Attempt: close failed as well]" + closeException.getMessage());
			}
			return;
		}

		Log.d(TAG, "Connected..");
		// start Receiver on socket
		parentConnector.startConnectorReceiver(mmSocket);
	}
	/*
	 * public void disconnect() { try { mmSocket.close(); } catch (IOException
	 * e) { Log.d(TAG, "Exception [disconnect]: " + e.getMessage()); } }
	 */
}