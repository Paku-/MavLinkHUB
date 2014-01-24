package com.paku.mavlinkhub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import com.ftdi.j2xx.FT_Device;
import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ThreadUSBReader extends Thread {

	private static final String TAG = ThreadUSBReader.class.getSimpleName();

	private static final int BUFFSIZE = 1024 * 4;

	private final InputStream input;
	private final OutputStream output;

	private final Handler handlerQueueIOBytesReceiver;

	private boolean running = true;

	public ThreadUSBReader(FT_Device usbDevice, Handler handlerReceiver) {

		handlerQueueIOBytesReceiver = handlerReceiver;

		input = null;
		output = null;

	}

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

		// if socketTCP.close();

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