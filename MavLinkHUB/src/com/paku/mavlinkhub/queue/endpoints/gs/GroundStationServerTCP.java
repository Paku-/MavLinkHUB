package com.paku.mavlinkhub.queue.endpoints.gs;

import java.net.Socket;
import java.nio.ByteBuffer;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.SOCKET_STATE;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class GroundStationServerTCP extends GroundStationServer {

	private static final String TAG = "GroundStationServerTCP";

	private static final int sizeBuff = 1024;

	// Socket socketTCP;
	ThreadServerTCP serverTCP;

	private Handler handlerServerMsgRead;

	public GroundStationServerTCP(Handler handler) {
		super(handler, sizeBuff);
	}

	@Override
	public void startServer(int port) {

		// start received bytes handler
		handlerServerMsgRead = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				SOCKET_STATE[] socketStates = SOCKET_STATE.values();
				switch (socketStates[msg.what]) {
				// Received data
				case MSG_SOCKET_DATA_READY:
					// read bytes from drone
					putInputQueueItem(ByteBuffer.wrap((byte[]) msg.obj, 0, msg.arg1));
					break;
				// closing so kill itself
				case MSG_SOCKET_CLOSED:
					removeMessages(0);
				default:
					super.handleMessage(msg);
				}
			}
		};

		ThreadServerTCP serverTCP = new ThreadServerTCP(handlerServerMsgRead, port);
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
