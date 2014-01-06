package com.paku.mavlinkhub.mavlink;

import android.content.Context;
import com.paku.mavlinkhub.AppGlobals;

public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	private MavLinkParserThread parserThread;

	public MavLinkCollector(Context mContext) {

		globalVars = ((AppGlobals) mContext.getApplicationContext());

	}

	public void startMavLinkParserThread() {

		parserThread = new MavLinkParserThread(globalVars);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		if (parserThread != null)
			parserThread.stopRunning();
	}

}
