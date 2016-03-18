// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.hub;

import com.paku.mavlinkhub.HUBGlobals;

public class MAVLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = MAVLinkCollector.class.getSimpleName();

	private final HUBGlobals hub;

	private ThreadCollectorParser parserThread;

	public MAVLinkCollector(HUBGlobals hubContext) {

		hub = ((HUBGlobals) hubContext.getApplicationContext());

	}

	public void startMAVLinkParserThread() {

		parserThread = new ThreadCollectorParser(hub);
		parserThread.start();
	}

	public void stopMAVLinkParserThread() {
		if (null != parserThread) parserThread.stopMe();
	}

}
