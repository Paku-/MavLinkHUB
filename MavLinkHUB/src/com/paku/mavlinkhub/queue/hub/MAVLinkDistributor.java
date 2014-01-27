// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.queue.hub;

import com.paku.mavlinkhub.HUBGlobals;

public class MAVLinkDistributor {

	@SuppressWarnings("unused")
	private static final String TAG = MAVLinkDistributor.class.getSimpleName();

	private final HUBGlobals hub;

	private ThreadDistibutorSender distThread;

	public MAVLinkDistributor(HUBGlobals hubContext) {

		hub = ((HUBGlobals) hubContext.getApplicationContext());

	}

	public void startMAVLinkDistThread() {

		distThread = new ThreadDistibutorSender(hub);
		distThread.start();
	}

	public void stopMAVLinkDistThread() {
		if (null != distThread) distThread.stopMe();
	}

}
