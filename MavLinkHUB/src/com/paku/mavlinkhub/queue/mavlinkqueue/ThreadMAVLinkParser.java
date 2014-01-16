package com.paku.mavlinkhub.queue.mavlinkqueue;

import java.nio.ByteBuffer;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class ThreadMAVLinkParser extends Thread {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkParser";

	private final Parser parserDrone, parserGS;
	MAVLinkPacket tmpPacket = null;

	private ByteBuffer tmpBuffer = null;

	private boolean running = true;

	private final HUBGlobals globalVars;

	public ThreadMAVLinkParser(HUBGlobals context) {

		globalVars = context;

		parserDrone = new Parser();
		parserGS = new Parser();

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog("MavLink Parser", "Start...");

		while (running) {

			parse(MSG_SOURCE.FROM_DRONE);
			// save transmission from drone
			globalVars.logger.byteLog(MSG_SOURCE.FROM_DRONE, tmpBuffer);

			parse(MSG_SOURCE.FROM_GS);

			// that's rolling to fast, we need GUI update timer to preserve
			// resources or we can sleep a little (as below)
			globalVars.messenger.appMsgHandler.obtainMessage(APP_STATE.MSG_DATA_UPDATE_STATS.ordinal()).sendToTarget();

			// we do not need so much speed
			try {
				sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		globalVars.logger.sysLog("MavLink Parser", "...Stop");
	}

	private void parse(MSG_SOURCE direction) {

		switch (direction) {
		case FROM_DRONE:
			tmpBuffer = globalVars.droneClient.getInputQueueItem();
			break;
		case FROM_GS:
			tmpBuffer = globalVars.gsServer.getInputQueueItem();
			break;
		default:
			break;
		}

		if (tmpBuffer != null) {

			tmpPacket = null;

			// add bytes count to the stats
			globalVars.logger.hubStats.addByteStats(direction, tmpBuffer.limit());

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

				if (tmpPacket != null) {

					// 1 is here for msg repetition count; it's 1 as we do not
					// support
					// packet multiplication yet :(
					ItemMavLinkMsg tmpMsgItem = new ItemMavLinkMsg(tmpPacket, direction, 1);

					// store item for distribution and UI update
					globalVars.mavlinkQueue.putHubQueueItem(tmpMsgItem);
					// stream for syslog
					globalVars.logger.sysLog("MavlinkMsg", tmpMsgItem.humanDecode());

					// store parser stats
					switch (direction) {
					case FROM_DRONE:
						globalVars.logger.hubStats.setParserStats(direction, parserDrone.stats);
						break;
					case FROM_GS:
						globalVars.logger.hubStats.setParserStats(direction, parserGS.stats);
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
