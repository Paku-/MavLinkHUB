package com.paku.mavlinkhub.queue.msgcenter;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.QueueMsgItems;

public class MavlinkMsgCenter extends QueueMsgItems {

	// MAVLink class fields names holder/object
	public MavLinkMsgCollector mavlinkCollector;

	public MavlinkMsgCenter(HUBGlobals appContext, int capacity) {
		super(appContext, capacity);

		mavlinkCollector = new MavLinkMsgCollector(appContext);

	}

}
