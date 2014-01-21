package com.paku.mavlinkhub.queue.hubqueue;

import com.paku.mavlinkhub.HUBGlobals;

public class MAVLinkDistributor {

	@SuppressWarnings("unused")
	private static final String TAG = "MAVLinkDistributor";

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
		if (distThread != null) distThread.stopMe();
	}

}
