package com.paku.mavlinkhub.queue;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

public abstract class QueueBytes {

	private static final String TAG = "QueueBytes";

	protected final BlockingQueue<ByteBuffer> inputByteQueue;
	protected final BlockingQueue<ByteBuffer> outputByteQueue;

	public QueueBytes(int capacity) {

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
		ByteBuffer buffer = ByteBuffer.allocate(0);

		try {
			buffer = inputByteQueue.take();
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[getInputQueue]" + e.getMessage());
			e.printStackTrace();
		}
		return buffer;
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

}
