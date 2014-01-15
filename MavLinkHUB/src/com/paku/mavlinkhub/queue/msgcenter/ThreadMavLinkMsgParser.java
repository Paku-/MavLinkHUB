package com.paku.mavlinkhub.queue.msgcenter;

import java.nio.ByteBuffer;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class ThreadMavLinkMsgParser extends Thread {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkParser";

	private final Parser parserDrone, parserGS;
	MAVLinkPacket tmpPacket = null;

	private ByteBuffer buffer = null;

	private boolean running = true;

	private final HUBGlobals globalVars;

	public ThreadMavLinkMsgParser(HUBGlobals context) {

		globalVars = context;

		parserDrone = new Parser();
		parserGS = new Parser();

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog("[MavLink Parser]", "Start...");

		while (running) {

			parse(MSG_SOURCE.FROM_DRONE);
			// save transmission from drone
			globalVars.logger.byteLog(buffer);
			parse(MSG_SOURCE.FROM_GS);

		}

		globalVars.logger.sysLog("[MavLink Parser]", "...Stop");
	}

	private void parse(MSG_SOURCE source) {

		switch (source) {
		case FROM_DRONE:
			buffer = globalVars.droneClient.getInputQueueItem();
			break;
		case FROM_GS:
			buffer = globalVars.gsServer.getInputQueueItem();
			break;
		default:
			break;
		}

		if (buffer != null) {

			tmpPacket = null;

			for (int i = 0; i < buffer.limit(); i++) {

				switch (source) {
				case FROM_DRONE:
					tmpPacket = parserDrone.mavlink_parse_char(buffer.get(i) & 0x00ff);
					break;
				case FROM_GS:
					tmpPacket = parserGS.mavlink_parse_char(buffer.get(i) & 0x00ff);
					break;
				default:
					break;
				}

				if (tmpPacket != null) {

					// 1 here is for msg repetition count as we do not support
					// packet multiplication yes :(
					ItemMavLinkMsg tmpMsgItem = new ItemMavLinkMsg(tmpPacket, source, 1);

					// store item for distribution and UI update
					globalVars.msgCenter.putHubQueueItem(tmpMsgItem);
					// stream for syslog
					globalVars.logger.sysLog("MavlinkMsg", tmpMsgItem.humanDecode());

					// store parser stats
					switch (source) {
					case FROM_DRONE:
						globalVars.msgCenter.mavlinkCollector.storeLastParserStats(source, parserDrone.stats);
						break;
					case FROM_GS:
						globalVars.msgCenter.mavlinkCollector.storeLastParserStats(source, parserGS.stats);
						break;
					default:
						break;
					}

				}
			}
		}

	}

	public void stopRunning() {
		running = false;

	}

}
