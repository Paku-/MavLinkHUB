package com.paku.mavlinkhub.queue.endpoints;

import java.io.IOException;

import com.paku.mavlinkhub.queue.QueueBytes;

import android.os.Handler;

public abstract class GroundStationServer extends QueueBytes {

	@SuppressWarnings("unused")
	private static final String TAG = "GroundStationServer";

	public abstract void startServer(int port);

	public abstract void stopServer();

	public abstract boolean isRunning();

	public abstract void writeByte(byte[] bytes) throws IOException;

	protected GroundStationServer(Handler handler, int capacity) {
		super(handler, capacity);
	}

}
