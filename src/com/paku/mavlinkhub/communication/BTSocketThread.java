package com.paku.mavlinkhub.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BTSocketThread extends Thread {

	private static final String TAG = "BTSocketThread";


	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private final Handler mmSocketHandler;

	public BTSocketThread(BluetoothSocket socket, Handler socketHandler) {

		mmSocket = socket;
		mmSocketHandler = socketHandler;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			Log.d(TAG, "Exception [getstreams]:" + e.getMessage());
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {
		byte[] buffer = new byte[1024]; // buffer store for the stream
		int bytes; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI activity
				mmSocketHandler.obtainMessage(AppGlobals.MESSAGE_READ, bytes, -1, buffer)
						.sendToTarget();
			} catch (IOException e) {
				Log.d(TAG, "Exception [run.read.buffer]:" + e.getMessage());
				break;
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			mmOutStream.write(bytes);
		} catch (IOException e) {
			Log.d(TAG, "Exception [write]:" + e.getMessage());
		}
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			Log.d(TAG, "Exception [cancel]:" + e.getMessage());
		}
	}
}