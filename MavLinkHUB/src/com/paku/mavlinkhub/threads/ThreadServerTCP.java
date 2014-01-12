package com.paku.mavlinkhub.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class ThreadServerTCP extends Thread {

	private static final String TAG = "ThreadServerTCP";

	Socket socket;
	ServerSocket serverSocket;
	public boolean running = true;

	public ThreadServerTCP(int port) {
		try {

			serverSocket = new ServerSocket(port);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void run() {
		while (running) {
			try {
				Log.d(TAG, "Accept wait");
				socket = serverSocket.accept();
				ThreadSocket socketServiceTCP = new ThreadSocket(socket, null);
				socketServiceTCP.start();
				Log.d(TAG, "Socket Started");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopMe() {
		running = false;
	}
}