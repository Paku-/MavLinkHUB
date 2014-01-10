package com.paku.mavlinkhub.communication.connector;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.threads.ThreadConnectBluetooth;
import com.paku.mavlinkhub.threads.ThreadSocketBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ConnectorBluetooth extends Connector {

	private static final String TAG = "ConnectorBluetooth";
	private static final int sizeBuff = 1024;

	private BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;

	ThreadConnectBluetooth threadConnectBluetooth;
	ThreadSocketBluetooth threadSocketBluetooth;
	Handler btConnectorMsgHandler;

	public ConnectorBluetooth(Handler messanger) {
		super(messanger, sizeBuff);
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
			threadConnectBluetooth = new ThreadConnectBluetooth(this, mBluetoothAdapter, mBluetoothDevice);
			threadConnectBluetooth.start();
			return true;
		}
		catch (Exception e) {
			Log.d(TAG, "ConnectBT: " + e.getMessage());
			return false;
		}

	}

	@Override
	public void startConnectorReceiver(BluetoothSocket socket) {

		mBluetoothSocket = socket;

		btConnectorMsgHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				// Received data
				case HUBGlobals.MSG_CONNECTOR_DATA_READY:
					waitForStreamLock(3);
					mConnectorStream.write((byte[]) msg.obj, 0, msg.arg1);
					releaseStream();
					break;
				default:
					super.handleMessage(msg);
				}
			}
		};

		threadSocketBluetooth = new ThreadSocketBluetooth(socket, btConnectorMsgHandler);
		threadSocketBluetooth.start();

	}

	@Override
	public void closeConnection() {
		Log.d(TAG, "Closing connection..");

		try {
			threadSocketBluetooth.stopRunning();
		}
		catch (Exception e) {
			Log.d(TAG, "Exception [threadSocketBluetooth.cancel]: " + e.getMessage());
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

		return (isConnected()) ? mBluetoothSocket.getRemoteDevice().getName() : "";

	}

	@Override
	public String getPeerAddress() {
		return (isConnected()) ? mBluetoothSocket.getRemoteDevice().getAddress() : "";

	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	public void setBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
	}

}
