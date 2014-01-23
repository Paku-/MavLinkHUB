package com.paku.mavlinkhub.queue;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class QueueIOBytes {

	@SuppressWarnings("unused")
	private static final String TAG = QueueIOBytes.class.getSimpleName();

	private final ArrayDeque<ByteBuffer> inputByteQueue;
	private final ArrayDeque<ByteBuffer> outputByteQueue;
	// hub wide msg center
	public Handler appMsgHandler;

	protected QueueIOBytes(Handler msgCenter, int capacity) {
		// to the device
		outputByteQueue = new ArrayDeque<ByteBuffer>(capacity);
		// from the device
		inputByteQueue = new ArrayDeque<ByteBuffer>(capacity);

		this.appMsgHandler = msgCenter;

	}

	// get bytes
	public ByteBuffer getOutputByteQueueItem() {
		synchronized (outputByteQueue) {
			return outputByteQueue.pollFirst();
		}
	}

	public ByteBuffer getInputByteQueueItem() {
		synchronized (inputByteQueue) {
			return inputByteQueue.pollFirst();
		}
	}

	// add bytes
	public void addInputByteQueueItem(Message byteMsg) {

		synchronized (inputByteQueue) {
			inputByteQueue.addLast((ByteBuffer) byteMsg.obj);
		}
		return;

	}

	public void addInputByteQueueItem(ByteBuffer buffer) {
		synchronized (inputByteQueue) {
			inputByteQueue.addLast(buffer);
		}
		return;
	}

	public void addOutputByteQueueItem(ByteBuffer buffer) {
		synchronized (outputByteQueue) {
			outputByteQueue.addLast(buffer);
		}
	}

	// get queues
	public ArrayDeque<ByteBuffer> getInputByteQueue() {
		return inputByteQueue;
	}

	public ArrayDeque<ByteBuffer> getOutputByteQueue() {
		return outputByteQueue;
	}

	// that's the true ADD ,method for this class
	// this handler is called by the messages coming from any other class build
	// over the QueueIOBytes (both clients and servers). Any bytes receiving
	// thread sends a msg with the
	// buffer here to be stored in the underlying queue.
	// msg other then ADD are forwarded to the main hub messenger
	protected Handler startInputQueueMsgHandler() {
		return new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message byteMsg) {

				final SOCKET_STATE[] socketStates = SOCKET_STATE.values();
				switch (socketStates[byteMsg.what]) {

				// ===== All clients and servers threads send those msgs ======

				// Received data
				case MSG_SOCKET_BYTE_DATA_READY:
					addInputByteQueueItem(byteMsg);
					break;
				// closing so kill myself
				case MSG_SOCKET_CLOSED:
					removeMessages(0);
					break;

				// ===== Those are only sent by drone/client threads ======

				case MSG_SOCKET_DRONE_CLIENT_LOST_CONNECTION:
					appMsgHandler.obtainMessage(APP_STATE.MSG_DRONE_CONNECTION_LOST.ordinal()).sendToTarget();
					break;

				// ===== Those are only sent by gs/servers threads ======

				// new client connected
				case MSG_SOCKET_SERVER_CLIENT_CONNECTED:
					appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_CLIENT_CONNECTED.ordinal(), byteMsg.arg1, byteMsg.arg2, byteMsg.obj).sendToTarget();
					break;
				// Client lost;
				case MSG_SOCKET_SERVER_CLIENT_DISCONNECTED:
					appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					break;
				default:
					super.handleMessage(byteMsg);

				}
			}

		};

	}
}
