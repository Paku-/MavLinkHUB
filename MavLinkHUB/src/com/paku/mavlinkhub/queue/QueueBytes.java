package com.paku.mavlinkhub.queue;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class QueueBytes {

	private static final String TAG = "QueueBytes";

	private final BlockingQueue<ByteBuffer> inputByteQueue;
	private final BlockingQueue<ByteBuffer> outputByteQueue;
	// hub wide msg center
	public Handler appMsgHandler;

	protected QueueBytes(Handler msgCenter, int capacity) {
		// to the device
		outputByteQueue = new ArrayBlockingQueue<ByteBuffer>(capacity);
		// from the device
		inputByteQueue = new ArrayBlockingQueue<ByteBuffer>(capacity);

		this.appMsgHandler = msgCenter;

	}

	// output queue
	public ByteBuffer getOutputQueueItem() {
		ByteBuffer buffer = ByteBuffer.allocate(0);

		try {
			buffer = outputByteQueue.take();
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[getOutputQueue]" + e.getMessage());
			e.printStackTrace();
		}
		return buffer;
	}

	public void putOutputQueueItem(ByteBuffer buffer) {
		try {
			outputByteQueue.put(buffer);
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[putOutputQueue]" + e.getMessage());
			e.printStackTrace();
		}
	}

	public BlockingQueue<ByteBuffer> getOutputQueue() {
		return outputByteQueue;
	}

	// input queue
	public ByteBuffer getInputQueueItem() {
		return inputByteQueue.poll();
	}

	public void putInputQueueItem(ByteBuffer buffer) {
		try {
			inputByteQueue.put(buffer);
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[putInputQueue]" + e.getMessage());
			e.printStackTrace();
		}
	}

	public BlockingQueue<ByteBuffer> getInputQueue() {
		return inputByteQueue;
	}

	// this handler is called by the messages coming from any other class build
	// over the QueueBytes. Any bytes receiving thread sends a msg with the
	// buffer here to be stored in the underlying queue.
	protected Handler startInputQueueMsgHandler() {
		return new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				final SOCKET_STATE[] socketStates = SOCKET_STATE.values();
				switch (socketStates[msg.what]) {

				// new client connected
				case MSG_SOCKET_TCP_SERVER_CLIENT_CONNECTED:
					appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_CLIENT_CONNECTED.ordinal(), msg.arg1, msg.arg2,
							msg.obj).sendToTarget();
					break;

				// Client lost;
				case MSG_SOCKET_TCP_SERVER_CLIENT_DISCONNECTED:
					appMsgHandler.obtainMessage(APP_STATE.MSG_SERVER_CLIENT_DISCONNECTED.ordinal()).sendToTarget();
					break;

				// Received data
				case MSG_SOCKET_DATA_READY:
					// read bytes from drone
					putInputQueueItem(ByteBuffer.wrap((byte[]) msg.obj, 0, msg.arg1));
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
