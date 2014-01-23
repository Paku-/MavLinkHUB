package com.paku.mavlinkhub.queue.endpoints;

import java.io.IOException;

import com.paku.mavlinkhub.queue.QueueIOBytes;

import android.os.Handler;

public abstract class GroundStationServer extends QueueIOBytes {

	@SuppressWarnings("unused")
	private static final String TAG = GroundStationServer.class.getSimpleName();

	public abstract void startServer(int port);

	public abstract void stopServer();

	public abstract boolean isRunning();

	public abstract boolean isClientConnected();

	public abstract boolean writeBytes(byte[] bytes) throws IOException;

	protected GroundStationServer(Handler handler, int capacity) {
		super(handler, capacity);
	}

}
