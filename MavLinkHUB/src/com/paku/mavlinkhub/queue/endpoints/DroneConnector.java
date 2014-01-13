package com.paku.mavlinkhub.queue.endpoints;

import com.paku.mavlinkhub.queue.QueueBytes;

import android.os.Handler;

public abstract class DroneConnector extends QueueBytes {

	@SuppressWarnings("unused")
	private static final String TAG = "DroneConnector";

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
		super(capacity);
		appMsgHandler = handler;

	}

}
