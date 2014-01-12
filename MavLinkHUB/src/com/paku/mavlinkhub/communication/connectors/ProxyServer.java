package com.paku.mavlinkhub.communication.connectors;

import java.io.ByteArrayOutputStream;

import android.os.Handler;

public abstract class ProxyServer {

	@SuppressWarnings("unused")
	private static final String TAG = "ProxyServer";

	ByteArrayOutputStream receiverStream;
	Handler appMsgHandler;

	public abstract void startServer(int port);

	public abstract void stopServer();

	public abstract boolean isRunning();

	public ProxyServer(Handler handler, int capacity) {

		receiverStream = new ByteArrayOutputStream(capacity);
		appMsgHandler = handler;

	}

}
