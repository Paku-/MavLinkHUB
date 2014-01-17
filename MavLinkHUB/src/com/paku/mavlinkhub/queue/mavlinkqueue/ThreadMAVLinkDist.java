package com.paku.mavlinkhub.queue.mavlinkqueue;

import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

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
			// send to the GS
			send(MSG_SOURCE.FROM_DRONE);
			// send to the Drone
			send(MSG_SOURCE.FROM_GS);
		}

		hub.logger.sysLog("MavLink Distributor", "...Stop");
	}

	private void send(MSG_SOURCE direction) {

		switch (direction) {
		case FROM_DRONE:
			tmpItem = null;
			// hub.droneClient.getInputQueueItem();
			break;
		case FROM_GS:
			tmpItem = null;
			// hub.gsServer.getInputQueueItem();
			break;
		default:
			break;
		}

		if (tmpItem != null) {

			tmpPacket = null;

			switch (direction) {
			case FROM_DRONE:
				break;
			case FROM_GS:
				break;
			default:
				break;
			}
		}

	}

	public void stopMe() {
		running = false;

	}

}
