package com.paku.mavlinkhub.queue;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class QueueMsgItems {

	@SuppressWarnings("unused")
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

	public ArrayList<ItemMavLinkMsg> getListMsgItemsForUI() {
		synchronized (arrayMavLinkMsgItemsForUI) {

			// we need a clone for adapter.
			final ArrayList<ItemMavLinkMsg> clone = new ArrayList<ItemMavLinkMsg>();
			clone.addAll(arrayMavLinkMsgItemsForUI);

			return clone;
		}
	}

	public int getItemCount() {
		return hubQueue.size();
	}

}
