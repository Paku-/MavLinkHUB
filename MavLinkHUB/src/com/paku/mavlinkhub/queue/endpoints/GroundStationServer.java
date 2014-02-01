package com.paku.mavlinkhub.queue.endpoints;

import java.io.IOException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.SERVER_IP_MODE;
import com.paku.mavlinkhub.queue.ConnectorBytes;

public abstract class GroundStationServer extends ConnectorBytes {

	@SuppressWarnings("unused")
	private static final String TAG = GroundStationServer.class.getSimpleName();

	protected SERVER_IP_MODE serverMode;

	protected String address;

	protected int port;

	public abstract void startServer(int port);

	public abstract void stopServer();

	public abstract boolean isRunning();

	public abstract boolean isClientConnected();

	public abstract boolean writeBytes(byte[] bytes) throws IOException;

	protected GroundStationServer(HUBGlobals hub, int capacity) {
		super(hub, capacity);
	}

	public SERVER_IP_MODE getServerMode() {
		return serverMode;
	}

	public void setServerMode(SERVER_IP_MODE serverMode) {
		this.serverMode = serverMode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
