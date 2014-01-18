package com.paku.mavlinkhub.queue.endpoints;

import java.io.IOException;

import com.paku.mavlinkhub.queue.QueueIOBytes;

import android.os.Handler;

public abstract class DroneClient extends QueueIOBytes {

	@SuppressWarnings("unused")
	private static final String TAG = "DroneClient";

	// application handler used to report connection states

	public abstract void startClient(String address);

	public abstract void stopClient();

	public abstract boolean isConnected();

	public abstract String getMyName();

	public abstract String getMyAddress();

	public abstract String getPeerName();

	public abstract String getPeerAddress();

	public abstract boolean writeBytes(byte[] bytes) throws IOException;

	protected DroneClient(Handler handler, int capacity) {
		super(handler, capacity);

	}

}
