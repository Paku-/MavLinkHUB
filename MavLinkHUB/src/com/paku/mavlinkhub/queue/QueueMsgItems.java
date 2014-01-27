// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue;

import java.util.ArrayDeque;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class QueueMsgItems {

	@SuppressWarnings("unused")
	private static final String TAG = QueueMsgItems.class.getSimpleName();

	private final ArrayDeque<ItemMavLinkMsg> hubQueue;

	protected final HUBGlobals hub;

	public QueueMsgItems(HUBGlobals hubContext, int capacity) {

		hubQueue = new ArrayDeque<ItemMavLinkMsg>(capacity);

		hub = hubContext;

	}

	public final ItemMavLinkMsg getHubQueueItem() {
		synchronized (hubQueue) {
			return hubQueue.poll();
		}

	}

	public final void addHubQueueItem(ItemMavLinkMsg item) {
		synchronized (hubQueue) {
			hubQueue.addLast(item);
		}

		HUBGlobals.sendAppMsg(APP_STATE.MSG_QUEUE_MSGITEM_READY, item);

	}

	public final int getItemCount() {
		return hubQueue.size();
	}

}
