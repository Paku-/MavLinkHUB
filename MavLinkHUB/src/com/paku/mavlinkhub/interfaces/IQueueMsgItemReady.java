package com.paku.mavlinkhub.interfaces;

import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public interface IQueueMsgItemReady {

	void onQueueMsgItemReady(ItemMavLinkMsg msgItem);

}
