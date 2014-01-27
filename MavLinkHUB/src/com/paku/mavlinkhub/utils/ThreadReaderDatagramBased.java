package com.paku.mavlinkhub.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.os.Handler;
import android.util.Log;

public class ThreadReaderDatagramBased extends Thread {

	private static final String TAG = ThreadReaderDatagramBased.class.getSimpleName();

	private static final int BUFFSIZE = 1024 * 4;

	private final DatagramSocket socketUDP;

	//that could be the broadcast as well.
	private final InetAddress address;

	private final int port;

	private final Handler handlerQueueIOBytesReceiver;

	private boolean running = true;

	// UDP constructor
	public ThreadReaderDatagramBased(InetAddress address, int port, Handler handlerReceiver) throws SocketException {

		//our receiving port is port+1 - while we broadcast (local lan f.e 192.168.1.255) for port = port
		this.socketUDP = new DatagramSocket(port + 1);
		this.port = port;
		this.address = address;

		handlerQueueIOBytesReceiver = handlerReceiver;

	}

	// This thread runs for both drone clients and server clients !!!
	public void run() {
		byte[] buffer = new byte[BUFFSIZE];
		DatagramPacket packet = new DatagramPacket(buffer, BUFFSIZE);

		int len; // bytes received

		while (running) {
			try {

				socketUDP.receive(packet);
				len = packet.getLength();
				if (len > 0) {
					//final ByteBuffer byteMsg = ByteBuffer.wrap(new byte[len]);
					final ByteBuffer byteMsg = ByteBuffer.allocate(len);
					byteMsg.put(packet.getData(), 0, len);
					byteMsg.flip();
					handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_BYTE_DATA_READY.ordinal(), len, -1, byteMsg).sendToTarget();
				}
				else {
					// could happen mostly to the servers thread
					Log.d(TAG, "** UDP got empty packet**");
					handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					running = false;
				}
			}
			catch (IOException e) {
				// could happen mostly to the client thread
				Log.d(TAG, "** UDP packet receive exception**" + e.getMessage());
				handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_DRONE_CLIENT_LOST_CONNECTION.ordinal()).sendToTarget();
				running = false;
				break;
			}
		}

		// try to close whatever we are :)
		socketUDP.close();

	}

	public void writeBytes(byte[] bytes) throws IOException {
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
		socketUDP.setBroadcast(true);
		socketUDP.send(packet);
	}

	public void stopMe() {
		// stop threads run() loop
		running = false;
		// stop it's handler as well
		handlerQueueIOBytesReceiver.obtainMessage(SOCKET_STATE.MSG_SOCKET_CLOSED.ordinal()).sendToTarget();

	}

	public boolean isRunning() {
		return running;
	}

}