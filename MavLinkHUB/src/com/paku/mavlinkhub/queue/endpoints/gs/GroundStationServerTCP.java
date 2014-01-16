package com.paku.mavlinkhub.queue.endpoints.gs;

import com.paku.mavlinkhub.enums.APP_STATE;
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
		serverTCP = new ThreadGroundStationServerTCP(handlerServerMsgRead, port);
		serverTCP.start();
		// send hub wide server_started msg
		appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_STARTED.ordinal(), 0, 0, port).sendToTarget();
	}

	@Override
	public void stopServer() {

		// stop handler
		if (handlerServerMsgRead != null) {
			handlerServerMsgRead.removeMessages(0);
		}

		serverTCP.stopMe();
		appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_STOPPED.ordinal()).sendToTarget();
	}

	@Override
	public boolean isRunning() {
		return serverTCP.running;
	}

}
