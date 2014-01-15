package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.paku.mavlinkhub.enums.SOCKET_STATE;
import com.paku.mavlinkhub.threads.ThreadSocket;

import android.os.Handler;
import android.util.Log;

public class ThreadGroundStationServerTCP extends Thread {

	private static final String TAG = "ThreadGroundStationServerTCP";

	Socket socket;
	ServerSocket serverSocket;
	ThreadSocket socketServiceTCP;
	Handler handlerServerReadMsg;
	public boolean running = true;

	public ThreadGroundStationServerTCP(Handler handler, int port) {
		try {

			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		handlerServerReadMsg = handler;

	}

	public void run() {
		while (running) {
			try {
				Log.d(TAG, "Accept wait");
				socket = serverSocket.accept();
				socketServiceTCP = new ThreadSocket(socket, handlerServerReadMsg);
				socketServiceTCP.start();
				handlerServerReadMsg.obtainMessage(SOCKET_STATE.MSG_SOCKET_TCP_SERVER_CLIENT_CONNECTION.ordinal())
						.sendToTarget();
				Log.d(TAG, "New Connection: TCP Socket Started");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopMe() {
		if (socketServiceTCP != null) {
			socketServiceTCP.stopMe();
		}
		running = false;
	}
}