package com.paku.mavlinkhub.communication.connectors;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.os.Handler;
import android.util.Log;

public abstract class DroneConnector {

	private static final String TAG = "DroneConnector";

	protected final BlockingQueue<ByteBuffer> fromDroneConnectorQueue;

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

		fromDroneConnectorQueue = new ArrayBlockingQueue<ByteBuffer>(capacity);

		appMsgHandler = handler;

	}

	public ByteBuffer getQueueItem() {

		ByteBuffer buffer = ByteBuffer.allocate(0);

		try {
			buffer = fromDroneConnectorQueue.take();
			// buffer.flip();
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[getQueueElement]" + e.getMessage());
			e.printStackTrace();
		}
		return buffer;
	}
}
