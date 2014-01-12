package com.paku.mavlinkhub.communication.connectors;

import android.os.Handler;

public class ProxyServerTCP extends ProxyServer {

	public ProxyServerTCP(Handler handler, int capacity) {
		super(handler, capacity);
	}

	@Override
	public void startServer(int port) {
	}

	@Override
	public void stopServer() {
	}

	@Override
	public boolean isRunning() {
		return false;
	}

}
