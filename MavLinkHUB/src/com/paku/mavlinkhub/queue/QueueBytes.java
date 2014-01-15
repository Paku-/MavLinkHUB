package com.paku.mavlinkhub.queue;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.paku.mavlinkhub.enums.SOCKET_STATE;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class QueueBytes {

	private static final String TAG = "QueueBytes";

	private final BlockingQueue<ByteBuffer> inputByteQueue;
	private final BlockingQueue<ByteBuffer> outputByteQueue;

	protected QueueBytes(int capacity) {

		outputByteQueue = new ArrayBlockingQueue<ByteBuffer>(capacity);
		inputByteQueue = new ArrayBlockingQueue<ByteBuffer>(capacity);

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

	protected Handler startInputQueueMsgHandler() {
		return new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				final SOCKET_STATE[] socketStates = SOCKET_STATE.values();
				switch (socketStates[msg.what]) {
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
