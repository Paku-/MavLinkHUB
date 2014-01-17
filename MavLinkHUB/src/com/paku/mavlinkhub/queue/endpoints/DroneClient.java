package com.paku.mavlinkhub.queue.endpoints;

import java.io.IOException;

import com.paku.mavlinkhub.queue.QueueBytes;

import android.os.Handler;

public abstract class DroneClient extends QueueBytes {

	@SuppressWarnings("unused")
	private static final String TAG = "DroneClient";

	// application handler used to report connection states

	public abstract void startConnection(String address);

	public abstract void stopClient();

	public abstract boolean isConnected();

	public abstract String getMyName();

	public abstract String getMyAddress();

	public abstract String getPeerName();

	public abstract String getPeerAddress();

	public abstract void writeByte(byte[] bytes) throws IOException;

	protected DroneClient(Handler handler, int capacity) {
		super(handler, capacity);

	}

}
