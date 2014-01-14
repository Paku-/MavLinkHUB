package com.paku.mavlinkhub.queue.endpoints.drone;

import java.nio.ByteBuffer;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.SOCKET_STATE;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.threads.ThreadSocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DroneClientBluetooth extends DroneClient {

	private static final String TAG = "DroneClientBluetooth";
	private static final int sizeBuff = 1024;

	private final BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mBluetoothDevice;
	private BluetoothSocket mBluetoothSocket;

	private Handler handlerDroneMsgRead;

	private DroneConnectingBluetoothThread droneConnectingBluetoothThread;
	private ThreadSocket socketServiceBT;

	public DroneClientBluetooth(Handler messenger) {
		super(messenger, sizeBuff);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public void startConnection(String address) {

		// start connection threat
		if (mBluetoothAdapter == null) {
			return;
		}
		try {
			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
			// create and start BT specific connection thread
			droneConnectingBluetoothThread = new DroneConnectingBluetoothThread(this, mBluetoothAdapter,
					mBluetoothDevice);
			droneConnectingBluetoothThread.start();
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
		handlerDroneMsgRead = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				SOCKET_STATE[] socketStates = SOCKET_STATE.values();
				switch (socketStates[msg.what]) {
				// Received data
				case MSG_SOCKET_DATA_READY:
					// read bytes from drone
					putInputQueueItem(ByteBuffer.wrap((byte[]) msg.obj, 0, msg.arg1));
					break;
				// closing so kill itself
				case MSG_SOCKET_CLOSED:
					removeMessages(0);
				default:
					super.handleMessage(msg);
				}
			}
		};

		// start receiver thread
		socketServiceBT = new ThreadSocket(mBluetoothSocket, handlerDroneMsgRead);
		socketServiceBT.start();

	}

	@Override
	public void stopConnection() {
		Log.d(TAG, "Closing connection..");

		try {
			// stop handler
			handlerDroneMsgRead.removeMessages(0);
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
