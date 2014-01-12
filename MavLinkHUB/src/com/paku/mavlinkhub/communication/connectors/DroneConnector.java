package com.paku.mavlinkhub.communication.connectors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.os.Handler;

public abstract class DroneConnector {

	@SuppressWarnings("unused")
	private static final String TAG = "DroneConnector";

	protected ByteArrayOutputStream fromDroneConnectorStream;
	public boolean lockStream = false;

	// application handler used to report connection states
	public Handler appMsgHandler;

	public abstract void startConnection(String address);

	public abstract void stopConnection();

	public abstract boolean isConnected();

	public abstract String getMyName();

	public abstract String getMyAddress();

	public abstract String getPeerName();

	public abstract String getPeerAddress();

	public DroneConnector(Handler handler, int capacity) {

		fromDroneConnectorStream = new ByteArrayOutputStream(capacity);

		appMsgHandler = handler;

	}

	public void lockStream(int milis) {
		while (lockStream) {
			// Log.d(TAG, "Stream Locked..");
			try {
				Thread.sleep(milis);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lockStream = true;
	}

	public void releaseStream() {
		lockStream = false;
	}

	public void resetStream(boolean withLock) {
		if (withLock) lockStream(2);
		fromDroneConnectorStream.reset();
		if (withLock) releaseStream();
	}

	public void cloneConnectorStream(OutputStream targetStream, boolean doReset) throws IOException {
		fromDroneConnectorStream.writeTo(targetStream);
		if (doReset) resetStream(false);
	}

	public ByteArrayOutputStream getConnectorStream() {
		return fromDroneConnectorStream;
	}

	public int getStreamSize() {
		return fromDroneConnectorStream.size();
	}

	public byte[] getStreamArray() {
		return fromDroneConnectorStream.toByteArray();
	}

}
