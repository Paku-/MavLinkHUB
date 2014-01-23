package com.paku.mavlinkhub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ThreadSocketReader extends Thread {

	private static final String TAG = ThreadSocketReader.class.getSimpleName();

	private static final int BUFFSIZE = 1024 * 4;

	private final BluetoothSocket socketBT;
	private final Socket socketTCP;

	private final InputStream input;
	private final OutputStream output;

	private final Handler handlerQueueIOBytesReceiver;

	private boolean running = true;

	// BT constructor
	public ThreadSocketReader(BluetoothSocket socket, Handler handlerReceiver) {

		socketBT = socket;
		socketTCP = null;
		handlerQueueIOBytesReceiver = handlerReceiver;

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
	public ThreadSocketReader(Socket socket, Handler handlerReceiver) {
		this.socketTCP = socket;
		socketBT = null;
		handlerQueueIOBytesReceiver = handlerReceiver;

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
		byte[] buffer = new byte[BUFFSIZE];
		int len; // bytes received

		while (running) {
			try {

				len = input.read(buffer, 0, buffer.length);
				if (len > 0) {
					final ByteBuffer byteMsg = ByteBuffer.wrap(new byte[len]);
					byteMsg.put(buffer, 0, len);
					byteMsg.flip();
					handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_BYTE_DATA_READY.ordinal(), len, -1, byteMsg).sendToTarget();
				}
				else {
					// could happen mostly to the servers thread
					Log.d(TAG, "** Server lost connection **");
					handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					running = false;
				}
			}
			catch (IOException e) {
				// could happen mostly to the client thread
				Log.d(TAG, "** Client lost Drone link **" + e.getMessage());
				handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_DRONE_CLIENT_LOST_CONNECTION.ordinal()).sendToTarget();
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
		// stop threads run() loop
		running = false;
		// stop it's handler as well
		handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_CLOSED.ordinal()).sendToTarget();

	}

	public boolean isRunning() {
		return running;
	}
}