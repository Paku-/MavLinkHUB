package com.paku.mavlinkhub.queue.hub;

import java.nio.ByteBuffer;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class ThreadCollectorParser extends Thread {

	@SuppressWarnings("unused")
	private static final String TAG = ThreadCollectorParser.class.getSimpleName();

	private final Parser parserDrone, parserGS;
	MAVLinkPacket tmpPacket = null;

	private ByteBuffer tmpBuffer = null;

	private boolean running = true;

	private final HUBGlobals hub;

	public ThreadCollectorParser(HUBGlobals hubContext) {

		hub = hubContext;

		parserDrone = new Parser();
		parserGS = new Parser();

		running = true;

	}

	public void run() {

		HUBGlobals.logger.sysLog("MavLink Parser", "Parser Start..");

		while (running) {

			parse(MSG_SOURCE.FROM_DRONE);

			// save transmission from drone
			HUBGlobals.logger.byteLog(MSG_SOURCE.FROM_DRONE, tmpBuffer);

			parse(MSG_SOURCE.FROM_GS);

		}

		HUBGlobals.logger.sysLog("MavLink Parser", "Parser ..Stop");
	}

	private void parse(MSG_SOURCE direction) {

		switch (direction) {
		case FROM_DRONE:
			tmpBuffer = hub.droneClient.getInputByteQueueItem();
			break;
		case FROM_GS:
			tmpBuffer = hub.gcsServer.getInputByteQueueItem();
			break;
		default:
			break;
		}

		if (null != tmpBuffer) {

			tmpPacket = null;

			// add bytes count to the stats
			HUBGlobals.logger.hubStats.addByteStats(direction, tmpBuffer.limit());

			// parse
			for (int i = 0; i < tmpBuffer.limit(); i++) {

				switch (direction) {
				case FROM_DRONE:
					tmpPacket = parserDrone.mavlink_parse_char(tmpBuffer.get(i) & 0x00ff);
					break;
				case FROM_GS:
					tmpPacket = parserGS.mavlink_parse_char(tmpBuffer.get(i) & 0x00ff);
					break;
				default:
					break;
				}

				if (null != tmpPacket) {

					// 1 is here for msg repetition count; it's 1 as we do not
					// support
					// packet multiplication yet :(
					ItemMavLinkMsg tmpMsgItem = new ItemMavLinkMsg(tmpPacket, direction, 1);

					// store item for distribution and UI update
					hub.queue.addHubQueueItem(tmpMsgItem);

					// stream for syslog
					// hub.logger.sysLog("MavlinkMsg",tmpMsgItem.humanDecode());

					// store parser stats
					switch (direction) {
					case FROM_DRONE:
						HUBGlobals.logger.hubStats.setParserStats(direction, parserDrone.stats);
						break;
					case FROM_GS:
						HUBGlobals.logger.hubStats.setParserStats(direction, parserGS.stats);
						break;
					default:
						break;

					}

				}

			}
			// update UI stats display - both byte and parsers stats
			HUBGlobals.sendAppMsg(APP_STATE.MSG_DATA_UPDATE_STATS);
		}

	}

	public void stopMe() {
		running = false;

	}

}
