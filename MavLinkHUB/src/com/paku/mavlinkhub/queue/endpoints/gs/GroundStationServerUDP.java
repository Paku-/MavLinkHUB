// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;
import java.net.SocketException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SERVER_IP_MODE;
import com.paku.mavlinkhub.enums.CONNECTOR_STATE;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.utils.ThreadReaderDatagramBased;
import com.paku.mavlinkhub.utils.Utils;

import android.util.Log;

public class GroundStationServerUDP extends GroundStationServer {

	private static final String TAG = GroundStationServerUDP.class.getSimpleName();

	private static final int SIZEBUFF = 1024;

	private ThreadReaderDatagramBased serverThread;

	public GroundStationServerUDP(HUBGlobals hub) {
		super(hub, SIZEBUFF);
		serverMode = SERVER_IP_MODE.UDP;
	}

	@Override
	public void startServer(int port) {

		// start received bytes handler

		setAddress(Utils.getIPAddress(true));
		setPort(port);

		try {
			serverThread = new ThreadReaderDatagramBased(Utils.getBroadcastAddress(hub), getPort(), ConnMsgHandler);

			ConnMsgHandler.obtainMessage(CONNECTOR_STATE.MSG_CONN_SERVER_STARTED.ordinal(), -1, -1, "UDP:" + Utils.getIPAddress(true) + ":" + getPort()).sendToTarget();
		}
		catch (SocketException e) {
			Log.d(TAG, "Thread creation exception: " + e.getMessage());
			return;
		}
		catch (IOException e) {
			Log.d(TAG, "Thread creation exception: " + e.getMessage());
			return;
		}

		serverThread.start();

	}

	@Override
	public void stopServer() {

		stopMsgHandler();

		serverThread.stopMe();

		HUBGlobals.sendAppMsg(APP_STATE.MSG_SERVER_STOPPED);
	}

	@Override
	public boolean isRunning() {
		return serverThread.isRunning();
	}

	@Override
	public boolean writeBytes(byte[] bytes) throws IOException {
		if (null != serverThread) {
			if (isClientConnected()) {
				serverThread.writeBytes(bytes);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isClientConnected() {
		//for UDP broadcast we assume someone is listening 
		return true;
	}
}
