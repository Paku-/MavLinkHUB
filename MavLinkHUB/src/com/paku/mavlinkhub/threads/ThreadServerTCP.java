package com.paku.mavlinkhub.threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadServerTCP extends Thread {

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
				socket = serverSocket.accept();
				ThreadSocketTCP socketServiceTCP = new ThreadSocketTCP(socket);
				socketServiceTCP.start();
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