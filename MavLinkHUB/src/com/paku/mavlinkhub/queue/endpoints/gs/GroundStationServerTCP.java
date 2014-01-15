package com.paku.mavlinkhub.queue.endpoints.gs;

import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;

import android.os.Handler;
import android.util.Log;

public class GroundStationServerTCP extends GroundStationServer {

	private static final String TAG = "GroundStationServerTCP";

	private static final int SIZEBUFF = 1024;

	// Socket socketTCP;
	ThreadGroundStationServerTCP serverTCP;

	private Handler handlerServerMsgRead;

	public GroundStationServerTCP(Handler handler) {
		super(handler, SIZEBUFF);
	}

	@Override
	public void startServer(int port) {

		// start received bytes handler
		handlerServerMsgRead = startInputQueueMsgHandler();
		final ThreadGroundStationServerTCP serverTCP = new ThreadGroundStationServerTCP(handlerServerMsgRead, port);
		serverTCP.start();
		Log.d(TAG, "Start");
	}

	@Override
	public void stopServer() {
		serverTCP.stopMe();
	}

	@Override
	public boolean isRunning() {
		return serverTCP.running;
	}

}
