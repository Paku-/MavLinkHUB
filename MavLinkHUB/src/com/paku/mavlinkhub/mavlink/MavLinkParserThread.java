package com.paku.mavlinkhub.mavlink;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class MavLinkParserThread extends Thread {

	private static final String TAG = "MavLinkParser";

	private Parser parser;

	private byte[] buffer;
	private int bufferLen;

	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	private HUBGlobals globalVars;

	public MavLinkParserThread(HUBGlobals context) {

		globalVars = context;

		parser = new Parser();

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog(TAG, "[Start]");
		int tmpStreamSize = 0;

		while (running) {
			tmpStreamSize = globalVars.droneConnector.getStreamSize();
			if (tmpStreamSize > globalVars.minStreamReadSize) {

				// lock, read and clear input stream
				globalVars.droneConnector.lockStream(3);
				buffer = globalVars.droneConnector.getStreamArray();
				bufferLen = tmpStreamSize;
				globalVars.droneConnector.resetStream(false);
				globalVars.droneConnector.releaseStream();

				// globalVars.logger.sysLog(TAG, "[bytes]: " + bufferLen);

				lastMavLinkPacket = null;

				for (int i = 0; i < bufferLen; i++) {

					lastMavLinkPacket = parser.mavlink_parse_char(buffer[i] & 0x00ff);
					if (lastMavLinkPacket != null) {
						// MAVLinkMessage lastMavLinkMsg = lastMavLinkPacket
						// .unpack();

						ItemMavLinkMsg lastMavLinkMsgItem = new ItemMavLinkMsg(lastMavLinkPacket, 1);

						// stream msg for UI
						globalVars.logger.storeMavLinkMsgItem(lastMavLinkMsgItem);
						// stream for syslog
						globalVars.logger.sysLog("MavlinkMsg",
								globalVars.mMavLinkCollector.decodeMavlinkMsgItem(lastMavLinkMsgItem));
						// store parser stats
						globalVars.mMavLinkCollector.storeLastParserStats(parser.stats);
					}
				}
				// store bytes stream
				globalVars.logger.byteLog(buffer, 0, bufferLen);

				bufferLen = 0;

			}
		}

		globalVars.logger.sysLog(TAG, "[Stop]");
	}

	public void stopRunning() {
		running = false;

	}

}
