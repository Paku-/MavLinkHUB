package com.paku.mavlinkhub.queue.endpoints.gs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.paku.mavlinkhub.enums.SOCKET_STATE;
import com.paku.mavlinkhub.utils.ThreadSocketReader;

import android.os.Handler;
import android.util.Log;

public class ThreadGroundStationServerTCP extends Thread {

	private static final String TAG = ThreadGroundStationServerTCP.class.getSimpleName();

	Socket socket;
	ServerSocket serverSocket;
	ThreadSocketReader socketServerReaderThreadTCP;
	Handler handlerServerReadMsg;
	public boolean running = true;

	public ThreadGroundStationServerTCP(Handler handler, int port) {
		try {

			// /could be the port is already used - we need a check ...
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		handlerServerReadMsg = handler;

	}

	public void run() {
		String clientIP = new String();
		while (running) {
			Log.d(TAG, "Accept wait");
			try {
				socket = serverSocket.accept();

				socketServerReaderThreadTCP = new ThreadSocketReader(socket, handlerServerReadMsg);
				socketServerReaderThreadTCP.start();

				clientIP = socket.getInetAddress().toString() + ":" + socket.getPort();
				clientIP.replace("/", " ");

				handlerServerReadMsg.obtainMessage(SOCKET_STATE.MSG_SOCKET_SERVER_CLIENT_CONNECTED.ordinal(), clientIP.length(), -1, clientIP.getBytes()).sendToTarget();

				Log.d(TAG, "New Connection: TCP Socket Started");
			}
			catch (SocketException e) {
				running = false;
			}
			catch (IOException e) {
				// ?? should never happen if permissions set
				e.printStackTrace();
				running = false;
			}
		}
	}

	public void stopMe() {
		if (socketServerReaderThreadTCP != null) {
			socketServerReaderThreadTCP.stopMe();
		}
		running = false; // just in case
		try {
			serverSocket.close();
		}
		catch (IOException e) {
			// not possible on close ??
			e.printStackTrace();
		}

	}

	public void writeBytes(byte[] bytes) throws IOException {
		socketServerReaderThreadTCP.writeBytes(bytes);

	}

}