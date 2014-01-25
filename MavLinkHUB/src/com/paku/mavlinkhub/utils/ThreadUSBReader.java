package com.paku.mavlinkhub.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.util.Log;

public class ThreadUSBReader extends Thread {

	private static final String TAG = ThreadUSBReader.class.getSimpleName();

	private static final int BUFFSIZE = 1024 * 4;

	private static final int reqBuffLen = 4;

	private final Handler handlerQueueIOBytesReceiver;

	private boolean running = true;

	FT_Device usbDevice;

	public ThreadUSBReader(FT_Device usbDevice, Handler handlerReceiver) {

		handlerQueueIOBytesReceiver = handlerReceiver;
		this.usbDevice = usbDevice;

	}

	public void run() {
		byte[] buffer = new byte[BUFFSIZE];
		int len; // bytes received

		while (running) {
			// len = usbDevice.read(buffer, buffer.length);

			len = usbDevice.read(buffer, reqBuffLen);

			// Log.d(TAG, len + "/" + reqBuffLen + ":" + (new
			// String(buffer)).substring(0, len));

			if (len > 0) {
				//final ByteBuffer byteMsg = ByteBuffer.wrap(new byte[len]);
				final ByteBuffer byteMsg = ByteBuffer.allocate(len);
				byteMsg.put(buffer, 0, len);
				byteMsg.flip();
				handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_BYTE_DATA_READY.ordinal(), len, -1, byteMsg).sendToTarget();
			}

			/*
			 * else { // could happen mostly to the servers thread Log.d(TAG,
			 * "** Server lost connection **");
			 * handlerQueueIOBytesReceiver.obtainMessage
			 * (SOCKET_STATE.MSG_SOCKET_SERVER_CLIENT_DISCONNECTED
			 * .ordinal()).sendToTarget(); running = false; }
			 */

		}

		usbDevice.stopInTask();
		usbDevice.close();

	}

	public void writeBytes(byte[] bytes) throws IOException {
		usbDevice.write(bytes);
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