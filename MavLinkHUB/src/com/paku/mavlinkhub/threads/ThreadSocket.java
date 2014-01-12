package com.paku.mavlinkhub.threads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.paku.mavlinkhub.HUBGlobals;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ThreadSocket extends Thread {

	private static final String TAG = "ThreadSocket";

	private static final int bufferSize = 1024;

	private final BluetoothSocket socketBT;
	private final Socket socketTCP;

	private final BufferedInputStream input;
	private final BufferedOutputStream output;

	private final Handler socketMsgReceiverHandler;
	// msgs depends on our ervice type /BT,TCP etc/
	private final int msgDataReady;
	private final int msgConnClosed;

	private boolean running = true;

	// BT constructor
	public ThreadSocket(BluetoothSocket socket, Handler connectorReceiverHandler) {

		socketBT = socket;
		socketTCP = null;
		socketMsgReceiverHandler = connectorReceiverHandler;
		msgDataReady = HUBGlobals.MSG_SOCKET_BT_DATA_READY;
		msgConnClosed = HUBGlobals.MSG_SOCKET_BT_CLOSED;

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
	public ThreadSocket(Socket socket, Handler connectorReceiverHandler) {
		this.socketTCP = socket;
		socketBT = null;
		socketMsgReceiverHandler = connectorReceiverHandler;
		msgDataReady = HUBGlobals.MSG_SOCKET_TCP_DATA_READY;
		msgConnClosed = HUBGlobals.MSG_SOCKET_TCP_CLOSED;

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
		byte[] buffer = new byte[bufferSize];
		int bytes; // bytes received

		while (running) {
			try {
				// sleep as we are to fast for BT serial :)
				if (socketBT != null) sleep(50);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {

				bytes = input.read(buffer, 0, buffer.length);
				if (bytes > 0) {
					if (socketMsgReceiverHandler != null)
						socketMsgReceiverHandler.obtainMessage(msgDataReady, bytes, -1, buffer).sendToTarget();
				}
				else {
					Log.d(TAG, "** Empty socket buffer - Connection Error quiting...**");
					running = false;
				}
			}
			catch (IOException e) {
				Log.d(TAG, "Exception [run.read.buffer]:" + e.getMessage());
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
			Log.d(TAG, "Exception [socketBT Close]:" + e.getMessage());
			e.printStackTrace();
		}

	}

	public void write(byte[] bytes) {
		try {
			output.write(bytes);
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [write]:" + e.getMessage());
		}
	}

	public void stopRunning() {
		// stop handler as well
		socketMsgReceiverHandler.obtainMessage(msgConnClosed).sendToTarget();
		running = false;
	}
}