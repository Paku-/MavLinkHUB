package com.paku.mavlinkhub.utils;

import java.nio.ByteBuffer;

import com.ftdi.j2xx.FT_Device;
import com.paku.mavlinkhub.enums.CONNECTOR_STATE;

import android.os.Handler;

public class ThreadReaderUSB extends Thread {

	@SuppressWarnings("unused")
	private static final String TAG = ThreadReaderUSB.class.getSimpleName();

	private static final int BUFFSIZE = 1024 * 4;

	private final Handler connHandler;

	private boolean running = true;

	FT_Device usbDevice;

	public ThreadReaderUSB(FT_Device usbDevice, Handler handlerReceiver) {

		connHandler = handlerReceiver;
		this.usbDevice = usbDevice;

	}

	public void run() {
		final byte[] buffer = new byte[BUFFSIZE];
		int len, available; // bytes received

		while (running) {

			available = usbDevice.getQueueStatus();

			if (available > 0) {

				if (available > BUFFSIZE) {
					available = BUFFSIZE;
				}

				len = usbDevice.read(buffer, available);

				//Log.d(TAG, "usb read len:" + len);

				if (len > 0) {

					final ByteBuffer byteMsg = ByteBuffer.allocate(len);

					byteMsg.put(buffer, 0, len);
					byteMsg.flip();

					connHandler.obtainMessage(CONNECTOR_STATE.MSG_CONN_BYTE_DATA_READY.ordinal(), len, -1, byteMsg).sendToTarget();
				}

				/*
				 * else { // could happen mostly to the servers thread Log.d(TAG,
				 * "** Server lost connection **");
				 * connHandler.obtainMessage
				 * (CONNECTOR_STATE.MSG_CONN_SERVER_CLIENT_DISCONNECTED
				 * .ordinal()).sendToTarget(); running = false; }
				 */

			}
		}

		usbDevice.stopInTask();
		usbDevice.close();

	}

	public void writeBytes(byte[] bytes) {
		usbDevice.write(bytes);
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