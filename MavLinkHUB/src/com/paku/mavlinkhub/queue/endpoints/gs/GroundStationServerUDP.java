package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;
import java.net.SocketException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SERVER_IP_MODE;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.utils.ThreadReaderDatagramBased;
import com.paku.mavlinkhub.utils.Utils;

import android.os.Handler;
import android.util.Log;

public class GroundStationServerUDP extends GroundStationServer {

	private static final String TAG = GroundStationServerUDP.class.getSimpleName();

	private static final int SIZEBUFF = 1024;

	private ThreadReaderDatagramBased serverThread;

	private Handler handlerServerMsgRead;

	public GroundStationServerUDP(HUBGlobals hub) {
		super(SERVER_IP_MODE.UDP, hub, SIZEBUFF);
	}

	@Override
	public void startServer(int port) {

		// start received bytes handler
		handlerServerMsgRead = startInputQueueMsgHandler();

		//SERVER_IP_MODE modes[] = SERVER_IP_MODE.values();

		//switch (modes[myMode.ordinal()]) {

		try {
			serverThread = new ThreadReaderDatagramBased(Utils.getBroadcastAddress(hub), port, handlerServerMsgRead);
		}
		catch (SocketException e) {
			Log.d(TAG, "Thread creation exception: " + e.getMessage());
		}
		catch (IOException e) {
			Log.d(TAG, "Thread creation exception: " + e.getMessage());
		}

		serverThread.start();

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
		return serverThread.isRunning();
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
		//for UDP broadcast we assume someone is listening 
		return true;
	}
}
