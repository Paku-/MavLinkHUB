package com.paku.mavlinkhub.queue.mavlinkqueue;

import com.paku.mavlinkhub.HUBGlobals;

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
