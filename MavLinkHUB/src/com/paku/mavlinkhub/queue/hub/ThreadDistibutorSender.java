// $codepro.audit.disable emptyIfStatement
package com.paku.mavlinkhub.queue.hub;

import java.io.IOException;
import android.util.Log;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class ThreadDistibutorSender extends Thread {

	private static final String TAG = ThreadDistibutorSender.class.getSimpleName();

	private boolean running = true;

	private final HUBGlobals hub;

	public ThreadDistibutorSender(HUBGlobals hubContext) {

		hub = hubContext;

		running = true;

	}

	public void run() {

		ItemMavLinkMsg tmpItem = null;

		HUBGlobals.logger.sysLog("MavLink Distributor", "Distributor Start..");

		while (running) {

			try {

				tmpItem = hub.queue.getHubQueueItem();

				if (null != tmpItem) {

					switch (tmpItem.direction) {
					case FROM_DRONE:
						if (hub.gcsServer.isClientConnected()) {
							if (hub.gcsServer.writeBytes(tmpItem.getPacketBytes())) {
								// Log.d(TAG, "gcsServer: Packet sent");
							}
							else {
								Log.d(TAG, "gcsServer: No Client connected failure");
							}
						}
						else {
							// Log.d(TAG,
							// "gcsServer: Packet Silently discarded");
						}
						break;
					case FROM_GS:
						if (hub.droneClient.isConnected()) {
							if (hub.droneClient.writeBytes(tmpItem.getPacketBytes())) {
								// Log.d(TAG, "droneClient: Packet sent.");
							}
							else {
								Log.d(TAG, "droneClient: Not connected.");
							}
						}
						else {
							// Log.d(TAG,
							// "droneClient: Packet Silently discarded.");
						}
						break;
					default:
						break;

					}

					// Store queue items count left after last read but
					// only if it changed - for UI update -
					if (HUBGlobals.logger.hubStats.getQueueItemsCnt() != hub.queue.getItemCount()) {
						HUBGlobals.logger.hubStats.setQueueItemsCnt(hub.queue.getItemCount());
						HUBGlobals.sendAppMsg(APP_STATE.MSG_DATA_UPDATE_STATS);
					}

				}
			}
			catch (IOException e) {
				Log.d(TAG, "gcsServer: Socket  write exception:" + e.getMessage());
				e.printStackTrace();

			}

		}
		HUBGlobals.logger.sysLog("MavLink Distributor", "Distributor ..Stop");
	}

	public void stopMe() {
		running = false;
	}

}
