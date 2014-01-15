package com.paku.mavlinkhub.queue.endpoints;

import com.paku.mavlinkhub.queue.QueueBytes;

import android.os.Handler;

public abstract class GroundStationServer extends QueueBytes {

	@SuppressWarnings("unused")
	private static final String TAG = "GroundStationServer";

	Handler appMsgHandler;

	public abstract void startServer(int port);

	public abstract void stopServer();

	public abstract boolean isRunning();

	protected GroundStationServer(Handler handler, int capacity) {
		super(capacity);
		appMsgHandler = handler;

	}

}
