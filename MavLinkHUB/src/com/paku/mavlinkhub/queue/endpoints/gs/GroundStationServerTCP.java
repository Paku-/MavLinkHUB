package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SERVER_IP_MODE;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import android.os.Handler;

public class GroundStationServerTCP extends GroundStationServer {

	@SuppressWarnings("unused")
	private static final String TAG = GroundStationServerTCP.class.getSimpleName();

	private static final int SIZEBUFF = 1024;

	private ThreadGroundStationServerTCP serverThread;

	private Handler handlerServerMsgRead;

	public GroundStationServerTCP(HUBGlobals hub) {
		super(SERVER_IP_MODE.TCP, hub, SIZEBUFF);
	}

	@Override
	public void startServer(int port) {

		// start received bytes handler
		handlerServerMsgRead = startInputQueueMsgHandler();

		//SERVER_IP_MODE modes[] = SERVER_IP_MODE.values();

		//switch (modes[myMode.ordinal()]) {
		serverThread = new ThreadGroundStationServerTCP(handlerServerMsgRead, port);
		serverThread.start();
		//serverThread = new ThreadReaderDatagramBased(Utils.getBroadcastAddress(hub), port, handlerServerMsgRead);

	}

	@Override
	public void stopServer() {

		// stop handler
		if (handlerServerMsgRead != null) {
			handlerServerMsgRead.removeMessages(0);
		}

		serverThread.stopMe();
		hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_STOPPED.ordinal()).sendToTarget();
	}

	@Override
	public boolean isRunning() {
		return serverThread.running;
	}

	@Override
	public boolean writeBytes(byte[] bytes) throws IOException {
		if (isClientConnected()) {
			serverThread.writeBytes(bytes);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isClientConnected() {
		if (serverThread.socketServerReaderThreadTCP != null)
			return (serverThread.socketServerReaderThreadTCP.isRunning());
		else
			return false;
	}
}
