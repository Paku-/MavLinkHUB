package com.paku.mavlinkhub.queue.msgcenter;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.QueueMsgItems;

public class MAVLinkQueue extends QueueMsgItems {

	// MAVLink class fields names holder/object
	public MAVLinkCollector msgCollector;

	public MAVLinkQueue(HUBGlobals appContext, int capacity) {
		super(appContext, capacity);
		msgCollector = new MAVLinkCollector(appContext);

	}

}
