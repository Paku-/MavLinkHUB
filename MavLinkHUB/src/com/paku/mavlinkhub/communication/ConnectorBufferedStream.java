package com.paku.mavlinkhub.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
// this is a super class for connectors using buffered stream to keep incoming data
// receiving Fragment class has to register for IDataLoggedIn interface to be called on data arrival. 

public abstract class ConnectorBufferedStream {

	private static final String TAG = "ConnectorBufferedStream";

	public ByteArrayOutputStream mConnectorStream;
	public boolean lockConnStream = false;

	protected abstract boolean openConnection(String address); // throws
																// UnknownHostException,IOException;

	protected abstract void closeConnection(); // throws
												// UnknownHostException,IOException;

	protected abstract boolean isConnected();

	protected abstract String getPeerName();

	protected abstract String getPeerAddress();

	protected abstract void startConnectorReceiver(BluetoothSocket socket);

	public ConnectorBufferedStream(int capacity) {

		mConnectorStream = new ByteArrayOutputStream(capacity);
		mConnectorStream.reset();

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

	public void processConnectorStream() {
		Log.d(TAG, "Stream Size: [" + String.valueOf(mConnectorStream.size()) + "]:");
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
