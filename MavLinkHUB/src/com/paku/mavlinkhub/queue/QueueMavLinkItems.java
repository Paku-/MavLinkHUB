package com.paku.mavlinkhub.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

import android.util.Log;

public abstract class QueueMavLinkItems {

	private static final String TAG = "QueueBytes";

	protected final BlockingQueue<ItemMavLinkMsg> hubMavLinkItemsQueue;

	public QueueMavLinkItems(int capacity) {
		hubMavLinkItemsQueue = new ArrayBlockingQueue<ItemMavLinkMsg>(capacity);
	}

	// output queue
	public ItemMavLinkMsg getOutputQueueItem() {
		ItemMavLinkMsg tmp = new ItemMavLinkMsg(null, null, 0);

		try {
			tmp = hubMavLinkItemsQueue.take();
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[get hub queue]" + e.getMessage());
			e.printStackTrace();
		}
		return tmp;
	}

	public void putOutputQueueItem(ItemMavLinkMsg buffer) {
		try {
			hubMavLinkItemsQueue.put(buffer);
		}
		catch (InterruptedException e) {
			Log.d(TAG, "[put hub queue]" + e.getMessage());
			e.printStackTrace();
		}
	}

	public BlockingQueue<ItemMavLinkMsg> getOutputQueue() {
		return hubMavLinkItemsQueue;
	}

}
