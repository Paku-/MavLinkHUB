package com.paku.mavlinkhub.mavlink;

import android.content.Context;
import com.paku.mavlinkhub.communication.AppGlobals;

public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	private MavLinkParserThread parserThread;

	public MavLinkCollector(Context mConext) {

		globalVars = ((AppGlobals) mConext.getApplicationContext());

		// globalVars.mBtConnector.registerForIBufferReady(this);

	}

	public void startMavLinkParserThread() {

		parserThread = new MavLinkParserThread(globalVars);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		parserThread.stopMe(true);
	}

}
