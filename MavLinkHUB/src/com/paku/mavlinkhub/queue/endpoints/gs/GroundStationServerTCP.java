package com.paku.mavlinkhub.queue.endpoints.gs;

import java.net.Socket;

import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.threads.ThreadServerTCP;

import android.os.Handler;
import android.util.Log;

public class GroundStationServerTCP extends GroundStationServer {

	private static final String TAG = "GroundStationServerTCP";

	private static final int sizeBuff = 1024;

	Socket socketTCP;
	ThreadServerTCP serverTCP;

	public GroundStationServerTCP(Handler handler) {
		super(handler, sizeBuff);
	}

	@Override
	public void startServer(int port) {
		ThreadServerTCP serverTCP = new ThreadServerTCP(port);
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
