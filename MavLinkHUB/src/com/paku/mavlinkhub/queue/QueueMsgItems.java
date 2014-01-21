package com.paku.mavlinkhub.queue;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class QueueMsgItems {

	@SuppressWarnings("unused")
	private static final String TAG = "QueueMsgItems";

	private final ArrayDeque<ItemMavLinkMsg> hubQueue;

	HUBGlobals hub;

	public QueueMsgItems(HUBGlobals hubContext, int capacity) {

		hubQueue = new ArrayDeque<ItemMavLinkMsg>(capacity);

		hub = hubContext;

	}

	public ItemMavLinkMsg getHubQueueItem() {
		synchronized (hubQueue) {
			return hubQueue.poll();
		}

	}

	public void addHubQueueItem(ItemMavLinkMsg item) {
		synchronized (hubQueue) {
			hubQueue.addLast(item);
		}

		hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_QUEUE_MSGITEM_READY.ordinal(), -1, -1, item).sendToTarget();

	}

	public int getItemCount() {
		return hubQueue.size();
	}

}
