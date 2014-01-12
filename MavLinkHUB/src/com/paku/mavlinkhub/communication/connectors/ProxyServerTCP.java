package com.paku.mavlinkhub.communication.connectors;

import java.net.Socket;

import com.paku.mavlinkhub.threads.ThreadServerTCP;

import android.os.Handler;

public class ProxyServerTCP extends ProxyServer {

	Socket socketTCP;
	ThreadServerTCP serverTCP;

	public ProxyServerTCP(Handler handler, int capacity) {
		super(handler, capacity);
	}

	@Override
	public void startServer(int port) {
		ThreadServerTCP serverTCP = new ThreadServerTCP(port);
		serverTCP.start();
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
