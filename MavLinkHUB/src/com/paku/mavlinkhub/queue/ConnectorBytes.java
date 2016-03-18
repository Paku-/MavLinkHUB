package com.paku.mavlinkhub.queue;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.CONNECTOR_STATE;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ConnectorBytes {

	@SuppressWarnings("unused")
	private static final String TAG = ConnectorBytes.class.getSimpleName();

	protected final HUBGlobals hub;

	protected static Handler ConnMsgHandler;

	private final ArrayDeque<ByteBuffer> inputByteQueue;
	private final ArrayDeque<ByteBuffer> outputByteQueue;

	protected ConnectorBytes(HUBGlobals hub, int capacity) {
		// to the device
		outputByteQueue = new ArrayDeque<ByteBuffer>(capacity);
		// from the device
		inputByteQueue = new ArrayDeque<ByteBuffer>(capacity);

		this.hub = hub;

		//create and runn msg handler for this connector

		// that's the true ADD method for this class
		// this handler is called by the messages coming from any other classes build
		// over the ConnectorBytes (both clients and servers). Any bytes receiving/reading
		// thread sends a msg with the
		// buffer here to be stored in the underlying queue.
		// msg other then ADD are forwarded to the main hub messenger		

		ConnMsgHandler =

		new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message byteMsg) {

				final CONNECTOR_STATE[] socketStates = CONNECTOR_STATE.values();
				switch (socketStates[byteMsg.what]) {

				// ===== All clients and servers threads send those msgs ======

				// Received data
				case MSG_CONN_BYTE_DATA_READY:
					addInputByteQueueItem(byteMsg);
					break;
				// closing so kill myself
				case MSG_CONN_CLOSED:
					removeMessages(0);
					break;

				// ===== Those are only sent by drone/client threads ======

				case MSG_CONN_DRONE_CLIENT_LOST_CONNECTION:
					HUBGlobals.sendAppMsg(APP_STATE.MSG_DRONE_CONNECTION_LOST);
					break;

				// ===== Those are only sent by gs/servers threads ======

				// new client connected
				case MSG_CONN_SERVER_CLIENT_CONNECTED:
					HUBGlobals.sendAppMsg(APP_STATE.MSG_SERVER_GCS_CONNECTED, byteMsg);
					break;
				// Client lost;
				case MSG_CONN_SERVER_CLIENT_DISCONNECTED:
					HUBGlobals.sendAppMsg(APP_STATE.MSG_SERVER_GCS_DISCONNECTED);
					break;
				case MSG_CONN_SERVER_STARTED:
					HUBGlobals.sendAppMsg(APP_STATE.MSG_SERVER_STARTED, byteMsg);
					break;
				case MSG_CONN_SERVER_START_FAILED:
					HUBGlobals.sendAppMsg(APP_STATE.MSG_SERVER_START_FAILED);
					break;
				default:
					super.handleMessage(byteMsg);

				}
			}

		};
	}

	//public getters

	// get bytes
	public final ByteBuffer getOutputByteQueueItem() {
		synchronized (outputByteQueue) {
			return outputByteQueue.pollFirst();
		}
	}

	public final ByteBuffer getInputByteQueueItem() {
		synchronized (inputByteQueue) {
			return inputByteQueue.pollFirst();
		}
	}

	//rest only for me and derived classes

	// add bytes
	protected final void addInputByteQueueItem(Message byteMsg) {

		synchronized (inputByteQueue) {
			inputByteQueue.addLast((ByteBuffer) byteMsg.obj);
		}
		return;

	}

	protected final void addInputByteQueueItem(ByteBuffer buffer) {
		synchronized (inputByteQueue) {
			inputByteQueue.addLast(buffer);
		}
		return;
	}

	protected final void addOutputByteQueueItem(ByteBuffer buffer) {
		synchronized (outputByteQueue) {
			outputByteQueue.addLast(buffer);
		}
	}

	// get queues
	protected final ArrayDeque<ByteBuffer> getInputByteQueue() {
		return inputByteQueue;
	}

	protected final ArrayDeque<ByteBuffer> getOutputByteQueue() {
		return outputByteQueue;
	}

	//below there are methods for derived classes (drones @ gs servers)

	protected static final void sendConnectorMsg(CONNECTOR_STATE msgType) {

	}

	protected void stopMsgHandler() {
		if (null != ConnMsgHandler) {
			ConnMsgHandler.removeMessages(0);
		}
	}

}
