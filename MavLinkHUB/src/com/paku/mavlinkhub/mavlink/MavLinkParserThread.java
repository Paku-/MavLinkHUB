package com.paku.mavlinkhub.mavlink;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.AppGlobals;

public class MavLinkParserThread extends Thread {

	private static final String TAG = "MavLinkParser";

	private Parser parser;

	private byte[] buffer;
	private int bufferLen;

	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	private AppGlobals globalVars;

	public MavLinkParserThread(AppGlobals context) {

		globalVars = context;

		parser = new Parser();

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog(TAG, "MavLink Parser Started");

		while (running) {

			if (globalVars.mBtConnector.mConnectorStream.size() > globalVars.minStreamReadSize) {

				// lock, read and clear input stream
				globalVars.mBtConnector.waitForStreamLock();
				buffer = globalVars.mBtConnector.mConnectorStream.toByteArray();
				bufferLen = globalVars.mBtConnector.mConnectorStream.size();
				globalVars.mBtConnector.mConnectorStream.reset();
				globalVars.mBtConnector.releaseStream();

				// globalVars.logger.sysLog(TAG, "[bytes]: " + bufferLen);

				lastMavLinkPacket = null;

				for (int i = 0; i < bufferLen; i++) {

					lastMavLinkPacket = parser
							.mavlink_parse_char(buffer[i] & 0x00ff);
					if (lastMavLinkPacket != null) {
						MAVLinkMessage lastMavLinkMsg = lastMavLinkPacket
								.unpack();
						// log ml msg
						globalVars.logger.mavlinkMsg(lastMavLinkMsg);
						globalVars.logger.loggerMsgHandler.obtainMessage(
								AppGlobals.MSG_MAVLINK_MSG_READY, -1, -1,
								lastMavLinkMsg).sendToTarget();

						globalVars.logger
								.sysLog("MavlinkMsg",
										globalVars.mMavLinkCollector
												.decodeMavlinkPkg(lastMavLinkPacket)
												+ " msg: "
												+ globalVars.mMavLinkCollector
														.decodeMavlinkMsg(lastMavLinkMsg));
						globalVars.logger.loggerMsgHandler.obtainMessage(
								AppGlobals.MSG_SYSLOG_DATA_READY)
								.sendToTarget();
						globalVars.mMavLinkCollector.storeLastParserStats(parser.stats);
					}
				}
				// store bytes stream
				globalVars.logger.byteLog(buffer, 0, bufferLen);
				globalVars.logger.loggerMsgHandler.obtainMessage(
						AppGlobals.MSG_BYTELOG_DATA_READY).sendToTarget();

				bufferLen = 0;

			}
		}

		globalVars.logger.sysLog(TAG, "MavLink Parser Stopped");
		globalVars.logger.loggerMsgHandler.obtainMessage(
				AppGlobals.MSG_SYSLOG_DATA_READY).sendToTarget();

	}

	public void stopRunning() {
		running = false;

	}

}
