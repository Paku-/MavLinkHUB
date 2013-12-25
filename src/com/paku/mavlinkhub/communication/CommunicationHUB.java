package com.paku.mavlinkhub.communication;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class CommunicationHUB extends Application {

	private static final String TAG = "CommHUB";

	public static final int MESSAGE_READ = 101;
	public static final int REQUEST_ENABLE_BT = 102;

	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;
	Context appContext;

	BTConnectThread connThread;
	BTSocketThread socketThread;
	Handler socketHandler;

	public void Init(Context mConext) {
		appContext = mConext;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public boolean IsConnected() {
		if (mBluetoothSocket == null)
			return false;
		else
			return mBluetoothSocket.isConnected();

	}

	public boolean ConnectBT(String address) {

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
			Log.d(TAG, "ConnectBT: ");
			return false;
		}

	}

	public void StartTransmission(BluetoothSocket socket) {

		mBluetoothSocket = socket;

		socketHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				// Received data from... somewhere
				case MESSAGE_READ:

					byte[] readBuf = (byte[]) msg.obj;
					String readMessage = new String(readBuf, 0, msg.arg1);

					Log.d("DATA", readMessage);

					break;
				// case MSG_SELF_DESTRY_SERVICE:
				// close();
				// break;
				default:
					super.handleMessage(msg);
				}

			}
		};

		socketThread = new BTSocketThread(socket, socketHandler);
		socketThread.start();

	}

	public void CloseConnection() {

		Log.d(TAG, "socketThread call - socketThread.cancel");
		socketThread.cancel();

		Log.d(TAG, "connThread call - conn.disconnect");
		connThread.disconnect();

	}

}
