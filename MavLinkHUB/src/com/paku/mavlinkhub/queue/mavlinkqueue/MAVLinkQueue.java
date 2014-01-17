package com.paku.mavlinkhub.queue.mavlinkqueue;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.QueueMsgItems;

public class MAVLinkQueue extends QueueMsgItems {

	// MAVLink class fields names holder/object
	public MAVLinkCollector msgCollector;
	public MAVLinkDistributor msgDistributor;

	public MAVLinkQueue(HUBGlobals hubContext, int capacity) {
		super(hubContext, capacity);
		msgCollector = new MAVLinkCollector(hubContext);
		msgDistributor = new MAVLinkDistributor(hubContext);

	}

	public void stopQueue() {
		msgCollector.stopMAVLinkParserThread();
		msgDistributor.stopMAVLinkDistThread();
	}

	public void startQueue() {
		msgCollector.startMAVLinkParserThread();
		msgDistributor.startMAVLinkDistThread();
	}

}
