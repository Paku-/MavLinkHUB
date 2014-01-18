package com.paku.mavlinkhub.queue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class QueueMsgItems {

	private static final String TAG = "QueueMsgItems";

	// private final BlockingQueue<ItemMavLinkMsg> queue;
	private final ArrayDeque<ItemMavLinkMsg> hubQueue;

	// in mem msgItems storage for UI shorter display /global var size limited/
	private final ArrayDeque<ItemMavLinkMsg> arrayMavLinkMsgItemsForUI;

	HUBGlobals hub;

	public QueueMsgItems(HUBGlobals hubContext, int capacity) {

		// queue = new ArrayBlockingQueue<ItemMavLinkMsg>(capacity);
		hubQueue = new ArrayDeque<ItemMavLinkMsg>(capacity);
		arrayMavLinkMsgItemsForUI = new ArrayDeque<ItemMavLinkMsg>();

		hub = hubContext;

	}

	public ArrayDeque<ItemMavLinkMsg> getHubQueue() {
		return hubQueue;
	}

	public ItemMavLinkMsg getHubQueueItem() {
		synchronized (hubQueue) {
			return hubQueue.poll();
		}

	}

	public void putHubQueueItem(ItemMavLinkMsg item) {
		// queue store
		synchronized (hubQueue) {
			hubQueue.addLast(item);
		}

		hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_QUEUE_MSGITEM_READY.ordinal(), -1, -1, item).sendToTarget();

		// store item for UI
		synchronized (arrayMavLinkMsgItemsForUI) {
			arrayMavLinkMsgItemsForUI.addLast(item);
			// and call for UI update
			// limit the Array size
			while (arrayMavLinkMsgItemsForUI.size() > hub.visibleMsgList) {
				arrayMavLinkMsgItemsForUI.removeFirst();
			}
		}

	}

	public ArrayDeque<ItemMavLinkMsg> getMsgItemsForUI() {
		synchronized (arrayMavLinkMsgItemsForUI) {
			return arrayMavLinkMsgItemsForUI;
		}
	}

	public int getItemCount() {
		return hubQueue.size();
	}

}
