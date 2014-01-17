package com.paku.mavlinkhub.queue.mavlinkqueue;

import java.io.IOException;
import android.util.Log;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class ThreadMAVLinkDist extends Thread {

	private static final String TAG = "ThreadMAVLinkDist";

	private boolean running = true;

	private final HUBGlobals hub;

	public ThreadMAVLinkDist(HUBGlobals hubContext) {

		hub = hubContext;

		running = true;

	}

	public void run() {

		ItemMavLinkMsg tmpItem = null;

		hub.logger.sysLog("MavLink Distributor", "Start...");

		// String tmp = new String();

		while (running) {
			// tmp = "#" + Long.toString(System.currentTimeMillis()) + "#\r\n";
			/*
			 * try { sleep(2); } catch (InterruptedException e1) { // TODO
			 * Auto-generated catch block e1.printStackTrace(); }
			 */
			try {

				hub.logger.hubStats.setQueueItemsCnt(hub.hubQueue.getItemCount());

				hub.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_DATA_UPDATE_STATS.ordinal()).sendToTarget();

				tmpItem = hub.hubQueue.getHubQueueItem();

				if (tmpItem != null) {
					switch (tmpItem.getDirection()) {
					case FROM_DRONE:
						if (hub.gsServer.isClientConnected()) {
							if (hub.gsServer.writeBytes(tmpItem.getPacketBytes())) {
								Log.d(TAG, "gsServer: Packet sent");
							}
							else {
								Log.d(TAG, "gsServer: No Client connected failure");
							}
						}
						else
							Log.d(TAG, "gsServer: Packet Silently discarded");

						break;
					case FROM_GS:
						if (hub.droneClient.isConnected()) {
							if (hub.droneClient.writeBytes(tmpItem.getPacketBytes())) {
								Log.d(TAG, "droneClient: Packet sent.");
							}
							else {
								Log.d(TAG, "droneClient: Not connected.");
							}
						}
						else
							Log.d(TAG, "droneClient: Packet Silently discarded.");
						break;
					default:
						break;

					}
				}
			}
			catch (IOException e) {
				Log.d(TAG, "gsServer: Socket  write exception:" + e.getMessage());
				e.printStackTrace();
			}
			catch (InterruptedException e) {
				Log.d(TAG, "gsServer: Queue get waiting interrupted:" + e.getMessage());
				e.printStackTrace();
			}
		}
		hub.logger.sysLog("MavLink Distributor", "...Stop");
	}

	public void stopMe() {
		running = false;
	}

}
