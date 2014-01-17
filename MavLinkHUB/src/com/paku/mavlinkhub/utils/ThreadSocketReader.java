package com.paku.mavlinkhub.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ThreadSocketReader extends Thread {

	private static final String TAG = "ThreadSocketReader";

	private static final int BUFFSIZE = 1024;

	private final BluetoothSocket socketBT;
	private final Socket socketTCP;

	private final BufferedInputStream input;
	private final BufferedOutputStream output;

	private final Handler handlerSocketMsgReceiver;

	private boolean running = true;

	// BT constructor
	public ThreadSocketReader(BluetoothSocket socket, Handler handlerReceiver) {

		socketBT = socket;
		socketTCP = null;
		handlerSocketMsgReceiver = handlerReceiver;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [get BT streams]:" + e.getMessage());
		}

		input = new BufferedInputStream(tmpIn);
		output = new BufferedOutputStream(tmpOut);
	}

	// TCP constructor
	public ThreadSocketReader(Socket socket, Handler handlerReceiver) {
		this.socketTCP = socket;
		socketBT = null;
		handlerSocketMsgReceiver = handlerReceiver;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try {
			tmpIn = socketTCP.getInputStream();
			tmpOut = socketTCP.getOutputStream();
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [get TCP streams]:" + e.getMessage());
		}

		input = new BufferedInputStream(tmpIn);
		output = new BufferedOutputStream(tmpOut);

	}

	public void run() {
		byte[] buffer = new byte[BUFFSIZE];
		int bytes; // bytes received

		while (running) {
			try {
				// for BT: sleep as we are to fast for BT serial :)
				if (socketBT != null) sleep(100);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {

				bytes = input.read(buffer, 0, buffer.length);
				if (bytes > 0) {
					handlerSocketMsgReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_DATA_READY.ordinal(), bytes, -1, buffer).sendToTarget();
				}
				else {
					Log.d(TAG, "** Empty socket buffer - Connection Error...**");
					handlerSocketMsgReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_TCP_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					running = false;
				}
			}
			catch (IOException e) {
				Log.d(TAG, "Exception [run.read.buffer]:" + e.getMessage());
				running = false;
				break;
			}
		}

		// if we are here it's time to close ...
		try {
			// try to close whatever we are :)
			if (socketBT != null) socketBT.close();
			if (socketTCP != null) socketTCP.close();
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [socket(BT/TCP) Close]:" + e.getMessage());
			e.printStackTrace();
		}

	}

	public void writeBytes(byte[] bytes) throws IOException {
		output.write(bytes);
		output.flush();
	}

	public void stopMe() {
		// stop handler as well
		handlerSocketMsgReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_CLOSED.ordinal()).sendToTarget();
		running = false;
	}

	public boolean isRunning() {
		return running;
	}
}