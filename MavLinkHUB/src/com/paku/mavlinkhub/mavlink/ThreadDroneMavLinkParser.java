package com.paku.mavlinkhub.mavlink;

import java.nio.ByteBuffer;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.HubQueue;
import com.paku.mavlinkhub.enums.ITEM_DIRECTION;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class ThreadDroneMavLinkParser extends Thread {

	private static final String TAG = "MavLinkParser";

	private Parser parser;

	private ByteBuffer buffer;

	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	private HUBGlobals globalVars;

	public ThreadDroneMavLinkParser(HUBGlobals context) {

		globalVars = context;

		parser = new Parser();

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog(TAG, "Start...");

		while (running) {

			buffer = globalVars.droneConnector.getInputQueueItem();

			// globalVars.logger.sysLog(TAG, "[bytes]: " + bufferLen);

			lastMavLinkPacket = null;

			for (int i = 0; i < buffer.limit(); i++) {

				lastMavLinkPacket = parser.mavlink_parse_char(buffer.get(i) & 0x00ff);
				if (lastMavLinkPacket != null) {
					// MAVLinkMessage lastMavLinkMsg = lastMavLinkPacket
					// .unpack();

					ItemMavLinkMsg lastMavLinkMsgItem = new ItemMavLinkMsg(lastMavLinkPacket,
							ITEM_DIRECTION.FROM_DRONE, 1);

					try {
						globalVars.hubQueue.putHubQueueItem(lastMavLinkMsgItem);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}

					// stream for syslog
					globalVars.logger.sysLog("MavlinkMsg",
							globalVars.mMavLinkCollector.decodeMavlinkMsgItem(lastMavLinkMsgItem));
					// store parser stats
					globalVars.mMavLinkCollector.storeLastParserStats(parser.stats);
				}
			}
			globalVars.logger.byteLog(buffer);

		}

		globalVars.logger.sysLog(TAG, "...Stop");
	}

	public void stopRunning() {
		running = false;

	}

}
