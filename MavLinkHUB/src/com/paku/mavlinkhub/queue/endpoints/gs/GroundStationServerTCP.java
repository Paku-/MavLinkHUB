// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SERVER_IP_MODE;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;

public class GroundStationServerTCP extends GroundStationServer {

	@SuppressWarnings("unused")
	private static final String TAG = GroundStationServerTCP.class.getSimpleName();

	private static final int SIZEBUFF = 1024;

	private ThreadGroundStationServerTCP serverThread;

	public GroundStationServerTCP(HUBGlobals hub) {
		super(hub, SIZEBUFF);
		serverMode = SERVER_IP_MODE.TCP;
	}

	@Override
	public void startServer(int port) {

		serverThread = new ThreadGroundStationServerTCP(ConnMsgHandler, port);
		serverThread.start();

		setAddress(serverThread.getAddress());
		setPort(serverThread.getPort());

	}

	@Override
	public void stopServer() {

		stopMsgHandler();

		serverThread.stopMe();

		HUBGlobals.sendAppMsg(APP_STATE.MSG_SERVER_STOPPED);
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
		if (null != serverThread) {
			if (null != serverThread.socketServerReaderThreadTCP)
				return (serverThread.socketServerReaderThreadTCP.isRunning());
			else {
				return false;
			}
		}
		return false;
	}
}
