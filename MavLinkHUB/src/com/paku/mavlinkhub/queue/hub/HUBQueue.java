// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.hub;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.QueueMsgItems;

public class HUBQueue extends QueueMsgItems {

	// MAVLink class fields names holder/object
	public MAVLinkCollector msgCollector;
	public MAVLinkDistributor msgDistributor;

	public HUBQueue(HUBGlobals hubContext, int capacity) {
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
