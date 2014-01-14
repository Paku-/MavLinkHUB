package com.paku.mavlinkhub;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class HubQueue {

	private static final String TAG = "HubQueue";

	private final BlockingQueue<ItemMavLinkMsg> hubQueue;

	// in mem msgItems storage for UI /global var size limited/
	private final ArrayList<ItemMavLinkMsg> arrayMavLinkMsgItemsForUI;

	HUBGlobals globalVars;

	public HubQueue(HUBGlobals appContext, int capacity) {

		hubQueue = new ArrayBlockingQueue<ItemMavLinkMsg>(capacity);
		arrayMavLinkMsgItemsForUI = new ArrayList<ItemMavLinkMsg>();

		globalVars = appContext;

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
		globalVars.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_MAVLINK_MSGITEM_READY.ordinal(), -1, -1, item)
				.sendToTarget();
		// limit the Array size
		while (arrayMavLinkMsgItemsForUI.size() > globalVars.visibleMsgList)
			arrayMavLinkMsgItemsForUI.remove(0);
		// flush mem
		arrayMavLinkMsgItemsForUI.trimToSize();

	}

	public ArrayList<ItemMavLinkMsg> getMsgItemsForUI() {
		return arrayMavLinkMsgItemsForUI;
	}

}
