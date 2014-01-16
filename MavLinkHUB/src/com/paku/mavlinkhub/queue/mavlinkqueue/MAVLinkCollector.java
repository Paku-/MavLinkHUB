package com.paku.mavlinkhub.queue.mavlinkqueue;

import android.content.Context;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

public class MAVLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MAVLinkCollector";

	private final HUBGlobals hub;

	private ThreadMAVLinkParser parserThread;

	public MAVLinkCollector(HUBGlobals hubContext) {

		hub = ((HUBGlobals) hubContext.getApplicationContext());

	}

	public void startMAVLinkParserThread() {

		parserThread = new ThreadMAVLinkParser(hub);
		parserThread.start();
	}

	public void stopMAVLinkParserThread() {
		if (parserThread != null) parserThread.stopRunning();
	}

}
