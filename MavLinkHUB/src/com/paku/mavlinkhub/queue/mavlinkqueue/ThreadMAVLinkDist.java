package com.paku.mavlinkhub.queue.mavlinkqueue;

import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class ThreadMAVLinkDist extends Thread {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkParser";

	MAVLinkPacket tmpPacket = null;

	private ItemMavLinkMsg tmpItem = null;

	private boolean running = true;

	private final HUBGlobals hub;

	public ThreadMAVLinkDist(HUBGlobals hubContext) {

		hub = hubContext;

		running = true;

	}

	public void run() {

		hub.logger.sysLog("MavLink Distributor", "Start...");

		while (running) {

			try {
				tmpItem = hub.mavlinkQueue.getHubQueueItem();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (tmpItem != null) {
				switch (tmpItem.getDirection()) {
				case FROM_DRONE:
					tmpItem = null;
					break;
				case FROM_GS:
					tmpItem = null;
					break;
				default:
					break;

				}
			}
		}
		hub.logger.sysLog("MavLink Distributor", "...Stop");
	}

	public void stopMe() {
		running = false;

	}

}
