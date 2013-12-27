package com.paku.mavlinkhub.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.paku.mavlinkhub.interfaces.IBufferReady;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;

public abstract class BufferedStreamConnector {

	public ByteArrayOutputStream buffer;
	public boolean lockBuffer = false;

	protected abstract boolean openConnection(String address); // throws
																// UnknownHostException,IOException;

	protected abstract void closeConnection(); // throws
												// UnknownHostException,IOException;

	protected abstract boolean isConnected();

	protected abstract String getPeerName();

	protected abstract void startTransmission(BluetoothSocket socket);

	private IBufferReady callerFragment;

	public void registerForIBufferReady(Fragment fragment) {
		callerFragment = (IBufferReady) fragment;
	}

	public BufferedStreamConnector(int capacity) {

		buffer = new ByteArrayOutputStream(capacity);
		buffer.reset();

	}

	public void waitForBufferLock() {
		while (lockBuffer) {
			;
		}

		lockBuffer = true;
	}

	public void releaseBuffer() {
		lockBuffer = false;
	}

	public void processBuffer() {

		// waitForBufferLock();
		// Log.d("DATA","["+
		// String.valueOf(buffer.size())+"]:"+buffer.toString());
		// buffer.reset();
		// releaseBuffer();

		if (buffer.size() > 100) {

			callerFragment.onBufferReady();

		}
	}

	private void resetBuffer(boolean withLock) {
		if (withLock)
			waitForBufferLock();
		buffer.reset();
		if (withLock)
			releaseBuffer();
	}

	public void copyandResetBuffer(OutputStream targetStream)
			throws IOException {

		buffer.writeTo(targetStream);
		resetBuffer(false);

	}

}
