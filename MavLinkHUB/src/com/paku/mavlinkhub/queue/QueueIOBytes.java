package com.paku.mavlinkhub.queue;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public abstract class QueueIOBytes {

	@SuppressWarnings("unused")
	private static final String TAG = "QueueIOBytes";

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

	// output queue
	public ByteBuffer getOutputQueueItem() {
		synchronized (outputByteQueue) {
			return outputByteQueue.poll();
		}
	}

	public void addOutputQueueItem(ByteBuffer buffer) {
		synchronized (outputByteQueue) {
			outputByteQueue.addLast(buffer);
		}
	}

	public ArrayDeque<ByteBuffer> getOutputQueue() {
		return outputByteQueue;
	}

	// input queue
	public ByteBuffer getInputQueueItem() {
		synchronized (inputByteQueue) {
			return inputByteQueue.poll();
		}
	}

	public void addInputQueueItem(Message msg) {
		ByteBuffer buffer = ByteBuffer.wrap((byte[]) msg.obj, 0, msg.arg1);
		/*
		 * String tmp = new String((byte[]) msg.obj, 0, msg.arg1);
		 * Log.d("[msg]", SystemClock.currentThreadTimeMillis() + "[" + msg.arg1
		 * + "]>" + tmp + "<"); tmp = new String(buffer.array(), 0,
		 * buffer.limit()); Log.d("[buf]", SystemClock.currentThreadTimeMillis()
		 * + "[" + buffer.limit() + "]>" + tmp + "<");
		 */
		synchronized (inputByteQueue) {
			inputByteQueue.addLast(buffer);
		}
		return;

	}

	public void addInputQueueItem(ByteBuffer buffer) {
		synchronized (inputByteQueue) {
			inputByteQueue.addLast(buffer);
		}
		return;
	}

	public ArrayDeque<ByteBuffer> getInputQueue() {
		return inputByteQueue;
	}

	// that's the true ADD ,method for this class
	// this handler is called by the messages coming from any other class build
	// over the QueueIOBytes. Any bytes receiving thread sends a msg with the
	// buffer here to be stored in the underlying queue.
	// msg othr then ADD are forwarded to the main app messenger
	protected Handler startInputQueueMsgHandler() {
		return new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				final SOCKET_STATE[] socketStates = SOCKET_STATE.values();
				switch (socketStates[msg.what]) {

				// new client connected
				case MSG_SOCKET_TCP_SERVER_CLIENT_CONNECTED:
					appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_CLIENT_CONNECTED.ordinal(), msg.arg1, msg.arg2, msg.obj).sendToTarget();
					break;

				// Client lost;
				case MSG_SOCKET_TCP_SERVER_CLIENT_DISCONNECTED:
					appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					break;

				// Received data
				case MSG_SOCKET_DATA_READY:
					// addInputQueueItem(ByteBuffer.wrap((byte[]) msg.obj, 0,
					// msg.arg1));
					addInputQueueItem(msg);
					break;

				// closing so kill itself
				case MSG_SOCKET_CLOSED:
					removeMessages(0);
					break;
				default:
					super.handleMessage(msg);
				}
			}

		};

	}
}
