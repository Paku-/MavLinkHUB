package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;

import android.os.Handler;

public class GroundStationServerTCP extends GroundStationServer {

	@SuppressWarnings("unused")
	private static final String TAG = GroundStationServerTCP.class.getSimpleName();

	private static final int SIZEBUFF = 1024;

	// Socket socketTCP;
	ThreadGroundStationServerTCP serverTCP;

	private Handler handlerServerMsgRead;

	public GroundStationServerTCP(HUBGlobals hub) {
		super(hub, SIZEBUFF);
	}

	@Override
	public void startServer(int port) {

		// start received bytes handler
		handlerServerMsgRead = startInputQueueMsgHandler();

		serverTCP = new ThreadGroundStationServerTCP(handlerServerMsgRead, port);
		serverTCP.start();

		// send hub wide server_started msg
		hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_STARTED.ordinal(), 0, 0, port).sendToTarget();
	}

	@Override
	public void stopServer() {

		// stop handler
		if (handlerServerMsgRead != null) {
			handlerServerMsgRead.removeMessages(0);
		}

		serverTCP.stopMe();
		hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_STOPPED.ordinal()).sendToTarget();
	}

	@Override
	public boolean isRunning() {
		return serverTCP.running;
	}

	@Override
	public boolean writeBytes(byte[] bytes) throws IOException {
		if (isClientConnected()) {
			serverTCP.writeBytes(bytes);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isClientConnected() {
		if (serverTCP.socketServerReaderThreadTCP != null)
			return (serverTCP.socketServerReaderThreadTCP.isRunning());
		else
			return false;
	}
}
