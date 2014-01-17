package com.paku.mavlinkhub.queue;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class QueueMsgItems {

	private static final String TAG = "QueueMsgItems";

	private final BlockingQueue<ItemMavLinkMsg> hubQueue;

	// in mem msgItems storage for UI shorter display /global var size limited/
	private final ArrayList<ItemMavLinkMsg> arrayMavLinkMsgItemsForUI;

	HUBGlobals hub;

	public QueueMsgItems(HUBGlobals hubContext, int capacity) {

		hubQueue = new ArrayBlockingQueue<ItemMavLinkMsg>(capacity);
		arrayMavLinkMsgItemsForUI = new ArrayList<ItemMavLinkMsg>();

		hub = hubContext;

	}

	public BlockingQueue<ItemMavLinkMsg> getHubQueue() {
		return hubQueue;
	}

	public ItemMavLinkMsg getHubQueueItem() throws InterruptedException {
		return hubQueue.take();
	}

	public void putHubQueueItem(ItemMavLinkMsg item) {
		// queue store
		try {
			hubQueue.put(item);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Put item");
			e.printStackTrace();
		}

		// store item for UI
		arrayMavLinkMsgItemsForUI.add(item);
		// and call for UI update
		// limit the Array size
		while (arrayMavLinkMsgItemsForUI.size() > hub.visibleMsgList) {
			arrayMavLinkMsgItemsForUI.remove(0);
		}
		// flush mem
		arrayMavLinkMsgItemsForUI.trimToSize();

		hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_QUEUE_MSGITEM_READY.ordinal(), -1, -1, item).sendToTarget();

	}

	public ArrayList<ItemMavLinkMsg> getMsgItemsForUI() {
		return arrayMavLinkMsgItemsForUI;
	}

}
