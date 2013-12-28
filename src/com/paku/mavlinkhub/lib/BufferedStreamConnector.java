package com.paku.mavlinkhub.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.paku.mavlinkhub.interfaces.IBufferReady;

import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class BufferedStreamConnector {

	public ByteArrayOutputStream mConnectorStream;
	public boolean lockConnStream = false;
	private int buffFlushSize = 64;

	protected abstract boolean openConnection(String address); // throws
																// UnknownHostException,IOException;

	protected abstract void closeConnection(); // throws
												// UnknownHostException,IOException;

	protected abstract boolean isConnected();

	protected abstract String getPeerName();

	protected abstract void startTransmission(BluetoothSocket socket);

	private IBufferReady callerFragment = null;
	private IBufferReady callerMavLink = null;

	public void registerFragmentForIBufferReady(Fragment fragment) {
		callerFragment = (IBufferReady) fragment;
	}

	public void registerMavLinkForIBufferReady(Fragment fragment) {
		callerMavLink = (IBufferReady) fragment;
	}

	public BufferedStreamConnector(int capacity) {

		mConnectorStream = new ByteArrayOutputStream(capacity);
		mConnectorStream.reset();

	}

	public void waitForStreamLock() {
		while (lockConnStream) {
			;
		}

		lockConnStream = true;
	}

	public void releaseStream() {
		lockConnStream = false;
	}

	public void processBuffer() {

		if (mConnectorStream.size() > buffFlushSize) {

			Log.d("BUFFER", "Size [" + String.valueOf(mConnectorStream.size()) + "]:");

			if (callerFragment != null) {
				callerFragment.onBufferReady();
			}

			if (callerMavLink != null) {
				callerMavLink.onBufferReady();
			}

		}

	}

	private void resetBuffer(boolean withLock) {
		if (withLock)
			waitForStreamLock();
		mConnectorStream.reset();
		if (withLock)
			releaseStream();
	}

	public void getResetConnStream(OutputStream targetStream)
			throws IOException {

		mConnectorStream.writeTo(targetStream);
		resetBuffer(false);

	}

}
