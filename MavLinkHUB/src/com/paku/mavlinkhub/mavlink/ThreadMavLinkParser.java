package com.paku.mavlinkhub.mavlink;

import java.nio.ByteBuffer;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.ITEM_DIRECTION;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class ThreadMavLinkParser extends Thread {

	private static final String TAG = "MavLinkParser";

	private Parser parser;

	private ByteBuffer buffer;

	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	private HUBGlobals globalVars;

	public ThreadMavLinkParser(HUBGlobals context) {

		globalVars = context;

		parser = new Parser();

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog(TAG, "Start...");

		while (running) {

			buffer = globalVars.droneClient.getInputQueueItem();

			// globalVars.logger.sysLog(TAG, "[bytes]: " + bufferLen);

			lastMavLinkPacket = null;

			for (int i = 0; i < buffer.limit(); i++) {

				lastMavLinkPacket = parser.mavlink_parse_char(buffer.get(i) & 0x00ff);
				if (lastMavLinkPacket != null) {

					ItemMavLinkMsg lastMavLinkMsgItem = new ItemMavLinkMsg(lastMavLinkPacket,
							ITEM_DIRECTION.FROM_DRONE, 1);

					// store item for distribution and UI update
					globalVars.hubQueue.putHubQueueItem(lastMavLinkMsgItem);
					// stream for syslog
					globalVars.logger.sysLog("MavlinkMsg", lastMavLinkMsgItem.humanDecode());
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
