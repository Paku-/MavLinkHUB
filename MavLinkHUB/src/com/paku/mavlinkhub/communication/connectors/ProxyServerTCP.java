package com.paku.mavlinkhub.communication.connectors;

import java.net.Socket;

import com.paku.mavlinkhub.threads.ThreadServerTCP;

import android.os.Handler;
import android.util.Log;

public class ProxyServerTCP extends ProxyServer {

	private static final String TAG = "ProxyServerTCP";

	private static final int sizeBuff = 1024;

	Socket socketTCP;
	ThreadServerTCP serverTCP;

	public ProxyServerTCP(Handler handler) {
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
