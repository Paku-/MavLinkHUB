package com.paku.mavlinkhub.queue.mavlinkqueue;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.QueueMsgItems;

public class MAVLinkQueue extends QueueMsgItems {

	// MAVLink class fields names holder/object
	public MAVLinkCollector msgCollector;

	public MAVLinkQueue(HUBGlobals hubContext, int capacity) {
		super(hubContext, capacity);
		msgCollector = new MAVLinkCollector(hubContext);

	}

}
