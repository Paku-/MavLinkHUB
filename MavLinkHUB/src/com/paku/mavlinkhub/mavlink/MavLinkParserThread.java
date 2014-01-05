package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.os.Handler;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.AppGlobals;

public class MavLinkParserThread extends Thread {

	private static final String TAG = "MavLinkParserThread";

	private Parser parser;

	private byte[] buffer;
	private int bufferLen;

	private int minBuffSize = 128;

	// data read stream

	// private ByteArrayOutputStream mByteDataStream;

	// private ByteArrayInputStream collectedDataStream;

	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	private AppGlobals globalVars;
	private Handler mavlinkCollectorMsgHandler;

	public MavLinkParserThread(AppGlobals context, Handler msgHandler) {

		globalVars = context;

		parser = new Parser();

		mavlinkCollectorMsgHandler = msgHandler;

		// read this
		// mByteDataStream = globalVars.mBtConnector.mConnectorStream;

		running = true;

	}

	public void run() {

		globalVars.logger.sysLog(TAG, "MavLink Parser Started");

		while (running) {

			if (globalVars.mBtConnector.mConnectorStream.size() > minBuffSize) {

				globalVars.mBtConnector.waitForStreamLock();
				buffer = globalVars.mBtConnector.mConnectorStream.toByteArray();
				bufferLen = globalVars.mBtConnector.mConnectorStream.size();
				// clear input stream
				globalVars.mBtConnector.mConnectorStream.reset();
				globalVars.mBtConnector.releaseStream();

				globalVars.logger.sysLog(TAG, "[bytes]: " + bufferLen);

				lastMavLinkPacket = null;

				for (int i = 0; i < bufferLen; i++) {

					lastMavLinkPacket = parser
							.mavlink_parse_char(buffer[i] & 0x00ff);
					if (lastMavLinkPacket != null) {
						MAVLinkMessage lastMavLinkMsg = lastMavLinkPacket
								.unpack();
						// log ml msg
						globalVars.logger.mavlinkMsg(lastMavLinkMsg);
						globalVars.logger.sysLog(TAG,
								lastMavLinkPacket.toString() + " : "
										+ lastMavLinkMsg.toString());
						// Broadcast it's ready
						mavlinkCollectorMsgHandler.obtainMessage(
								AppGlobals.MSG_MAVLINK_MSG_READY, -1, -1,
								lastMavLinkMsg).sendToTarget();

					}
				}

				// store bytes stream
				globalVars.logger.byteLog(buffer, 0, bufferLen);
				mavlinkCollectorMsgHandler.obtainMessage(
						AppGlobals.MSG_LOGGER_DATA_READY).sendToTarget();

				bufferLen = 0;

			}
		}

		globalVars.logger.sysLog(TAG, "MavLink Parser Stopped");

	}

	public void stopRunning() {
		running = false;

	}

}
