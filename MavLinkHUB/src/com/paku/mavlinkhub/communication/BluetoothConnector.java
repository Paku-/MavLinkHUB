package com.paku.mavlinkhub.communication;

import com.paku.mavlinkhub.lib.BufferedStreamConnector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BluetoothConnector extends BufferedStreamConnector {

	private static final String TAG = "BluetoothConnector";

	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;

	BTConnectThread connThread;
	BTSocketThread socketThread;
	Handler connectorReceiverHandler;

	public BluetoothConnector() {
		super(1024);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public boolean openConnection(String address) {

		// start connection threat
		if (mBluetoothAdapter == null) {
			return false;
		}
		try {
			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
			connThread = new BTConnectThread(mBluetoothAdapter,
					mBluetoothDevice, this);
			connThread.start();
			return true;
		} catch (Exception e) {
			Log.d(TAG, "ConnectBT: " + e.getMessage());
			return false;
		}

	}

	@Override
	public void startConnectorReceiver(BluetoothSocket socket) {

		mBluetoothSocket = socket;

		connectorReceiverHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				// Received data from... somewhere
				case AppGlobals.MSG_DATA_READY:

					// byte[] readBuf = (byte[]) msg.obj;
					// String readMessage = new String(readBuf, 0, msg.arg1);
					// Log.d("DATA", readMessage);
					// lock mConnectorStream just for me :)

					waitForStreamLock();

					mConnectorStream.write((byte[]) msg.obj, 0, msg.arg1);

					// Log.d("DATA", mConnectorStream.toString());
					// Log.d("DATA"," *** " +String.valueOf(mConnectorStream.size()));
					// mConnectorStream.reset();

					releaseStream();

					processConnectorStream();

					break;
				// case MSG_SELF_DESTRY_SERVICE:
				// close();
				// break;
				default:
					super.handleMessage(msg);
				}

			}
		};

		socketThread = new BTSocketThread(socket, connectorReceiverHandler);
		socketThread.start();

	}

	@Override
	public void closeConnection() {
		Log.d(TAG, "Closing connection..");
		try {
			socketThread.cancel();
		} catch (Exception e) {
			Log.d(TAG, "Exception [socketThread.cancel]: " + e.getMessage());
		}

		try {
			connThread.disconnect();
		} catch (Exception e) {
			Log.d(TAG, "Exception [connThread.disconnect]: " + e.getMessage());
		}
	}

	@Override
	public boolean isConnected() {
		if (mBluetoothSocket == null)
			return false;
		else
			return mBluetoothSocket.isConnected();

	}

	@Override
	public String getPeerName() {

		return mBluetoothSocket.getRemoteDevice().getName();

	}

}
