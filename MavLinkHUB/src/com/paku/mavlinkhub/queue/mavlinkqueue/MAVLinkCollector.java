package com.paku.mavlinkhub.queue.mavlinkqueue;

import android.content.Context;

import com.MAVLink.Messages.MAVLinkStats;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

public class MAVLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MAVLinkCollector";

	private final HUBGlobals globalVars;

	private ThreadMAVLinkParser parserThread;

	public MAVLinkCollector(Context mContext) {

		globalVars = ((HUBGlobals) mContext.getApplicationContext());

	}

	public void startMAVLinkParserThread() {

		parserThread = new ThreadMAVLinkParser(globalVars);
		parserThread.start();
	}

	public void stopMAVLinkParserThread() {
		if (parserThread != null) parserThread.stopRunning();
	}

}
