package com.paku.mavlinkhub;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class HubQueue {

	private final BlockingQueue<ItemMavLinkMsg> hubQueue;

	public HubQueue(int capacity) {

		hubQueue = new ArrayBlockingQueue<ItemMavLinkMsg>(capacity);

	}

	public BlockingQueue<ItemMavLinkMsg> getHubQueue() {
		return hubQueue;
	}

	public ItemMavLinkMsg getHubQueueItem() throws InterruptedException {
		return hubQueue.take();
	}

	public void putHubQueueItem(ItemMavLinkMsg item) throws InterruptedException {
		hubQueue.put(item);
	}

}
