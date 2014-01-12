package com.paku.mavlinkhub.communication.connectors;

import java.nio.ByteBuffer;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.threads.ThreadClientBluetooth;
import com.paku.mavlinkhub.threads.ThreadSocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DroneConnectorBluetooth extends DroneConnector {

	private static final String TAG = "DroneConnectorBluetooth";
	private static final int sizeBuff = 1024;

	private final BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothSocket mBluetoothSocket;

	public Handler msgWriterHandler;

	private ThreadClientBluetooth threadClientBluetooth;
	private ThreadSocket socketServiceBT;

	public DroneConnectorBluetooth(Handler messenger) {
		super(messenger, sizeBuff);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	// return value makes no-sense here - status is unknown.
	public void startConnection(String address) {

		// start connection threat
		if (mBluetoothAdapter == null) {
			return;
		}
		try {
			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
			threadClientBluetooth = new ThreadClientBluetooth(this, mBluetoothAdapter, mBluetoothDevice);
			threadClientBluetooth.start();
			return;
		}
		catch (Exception e) {
			Log.d(TAG, "ConnectBT: " + e.getMessage());
			return;
		}

	}

	public void startTransmission(BluetoothSocket socket) {

		mBluetoothSocket = socket;

		// start received bytes handler
		msgWriterHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				// Received data
				case HUBGlobals.MSG_SOCKET_BT_DATA_READY:
					try {
						fromDroneConnectorQueue.put(ByteBuffer.wrap((byte[]) msg.obj, 0, msg.arg1));
					}
					catch (InterruptedException e) {
						Log.d(TAG, "fromDroneConnectorQueue add failure");
						e.printStackTrace();
					}
					break;
				case HUBGlobals.MSG_SOCKET_BT_CLOSED:
					removeMessages(0);
				default:
					super.handleMessage(msg);
				}
			}
		};

		// start receiver thread
		socketServiceBT = new ThreadSocket(mBluetoothSocket, msgWriterHandler);
		socketServiceBT.start();

	}

	@Override
	public void stopConnection() {
		Log.d(TAG, "Closing connection..");

		try {
			// stop handler
			msgWriterHandler.removeMessages(0);
			// strop socket thread
			socketServiceBT.stopRunning();
		}
		catch (Exception e) {
			Log.d(TAG, "Exception [socketServiceBT.cancel]: " + e.getMessage());
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

	@Override
	public String getMyName() {
		return mBluetoothAdapter.getName();
	}

	@Override
	public String getMyAddress() {
		return mBluetoothAdapter.getAddress();
	}

}
