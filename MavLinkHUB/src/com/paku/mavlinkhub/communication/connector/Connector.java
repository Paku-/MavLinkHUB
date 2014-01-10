package com.paku.mavlinkhub.communication.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public abstract class Connector {

	@SuppressWarnings("unused")
	private static final String TAG = "Connector";

	public ByteArrayOutputStream mConnectorStream;
	public boolean lockConnStream = false;
	public Handler appMsgHandler;

	protected abstract boolean openConnection(String address);

	protected abstract void closeConnection();

	protected abstract boolean isConnected();

	protected abstract String getPeerName();

	protected abstract String getPeerAddress();

	protected abstract void startConnectorReceiver(BluetoothSocket socket);

	public Connector(Handler handler, int capacity) {

		mConnectorStream = new ByteArrayOutputStream(capacity);
		mConnectorStream.reset();

		appMsgHandler = handler;

	}

	public void waitForStreamLock(int milis) {
		while (lockConnStream) {
			// Log.d(TAG, "Stream Locked..");
			try {
				Thread.sleep(milis);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lockConnStream = true;
	}

	public void releaseStream() {
		lockConnStream = false;
	}

	private void resetStream(boolean withLock) {
		if (withLock) waitForStreamLock(2);
		mConnectorStream.reset();
		if (withLock) releaseStream();
	}

	public void copyConnectorStream(OutputStream targetStream, boolean doReset) throws IOException {
		mConnectorStream.writeTo(targetStream);
		if (doReset) resetStream(false);
	}

	public ByteArrayOutputStream getConnectorStream() {
		return mConnectorStream;
	}
}
