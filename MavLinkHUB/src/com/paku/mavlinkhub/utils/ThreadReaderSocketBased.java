package com.paku.mavlinkhub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.paku.mavlinkhub.enums.CONNECTOR_STATE;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ThreadReaderSocketBased extends Thread {

	private static final String TAG = ThreadReaderSocketBased.class.getSimpleName();

	private static final int BUFFSIZE = 1024 * 4;

	private final BluetoothSocket socketBT;
	private final Socket socketTCP;

	private final InputStream input;
	private final OutputStream output;

	private final Handler connHandler;

	private boolean running = true;

	// BT constructor
	public ThreadReaderSocketBased(BluetoothSocket socket, Handler handlerReceiver) {

		socketBT = socket;
		socketTCP = null;
		connHandler = handlerReceiver;

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

		// input = new BufferedInputStream(tmpIn);
		// output = new BufferedOutputStream(tmpOut);

		input = tmpIn;
		output = tmpOut;

	}

	// TCP constructor
	public ThreadReaderSocketBased(Socket socket, Handler handlerReceiver) {
		socketTCP = socket;
		socketBT = null;
		connHandler = handlerReceiver;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try {
			tmpIn = socketTCP.getInputStream();
			tmpOut = socketTCP.getOutputStream();
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [get TCP streams]:" + e.getMessage());
		}

		// input = new BufferedInputStream(tmpIn);
		// output = new BufferedOutputStream(tmpOut);

		input = tmpIn;
		output = tmpOut;

	}

	// This thread runs for both drone clients and server clients !!!
	public void run() {
		final byte[] buffer = new byte[BUFFSIZE];
		int len; // bytes received

		while (running) {
			try {

				len = input.read(buffer, 0, buffer.length);
				if (len > 0) {
					//final ByteBuffer byteMsg = ByteBuffer.wrap(new byte[len]);
					final ByteBuffer byteMsg = ByteBuffer.allocate(len);
					byteMsg.put(buffer, 0, len);
					byteMsg.flip();
					connHandler.obtainMessage(CONNECTOR_STATE.MSG_CONN_BYTE_DATA_READY.ordinal(), len, -1, byteMsg).sendToTarget();
				}
				else {
					// could happen mostly to the servers thread
					Log.d(TAG, "** Server lost connection **");
					connHandler.obtainMessage(CONNECTOR_STATE.MSG_CONN_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					running = false;
				}
			}
			catch (IOException e) {
				// could happen mostly to the client thread
				Log.d(TAG, "** Client lost Drone link **" + e.getMessage());
				connHandler.obtainMessage(CONNECTOR_STATE.MSG_CONN_DRONE_CLIENT_LOST_CONNECTION.ordinal()).sendToTarget();
				running = false;
				break;
			}
		}

		// if we are here it's time to close ...
		try {
			// try to close whatever we are :)
			if (null != socketBT) socketBT.close();
			if (null != socketTCP) socketTCP.close();
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
		// stop threads run() loop
		running = false;
		// stop it's handler as well
		connHandler.obtainMessage(CONNECTOR_STATE.MSG_CONN_CLOSED.ordinal()).sendToTarget();

	}

	public boolean isRunning() {
		return running;
	}
}