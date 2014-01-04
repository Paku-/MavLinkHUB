package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayOutputStream;
import android.util.Log;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.communication.AppGlobals;

public class MavLinkParserThread extends Thread {

	private static final String TAG = "MavLinkParserThread";

	private Parser parser;

	private byte[] buffer;
	private int bufferLen;

	// data read stream

	private ByteArrayOutputStream mByteDataStream;
	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	private AppGlobals globalVars;

	public MavLinkParserThread(AppGlobals context) {

		globalVars = context;

		parser = new Parser();

		// read this
		mByteDataStream = globalVars.mBtConnector.mConnectorStream;

		running = true;

	}

	public void run() {

		while (running) {

			if (mByteDataStream.size() > 0) {
				lastMavLinkPacket = null;

				buffer = mByteDataStream.toByteArray();
				bufferLen = mByteDataStream.size();

				// flush input stream
				mByteDataStream.reset();

				// store bytes stream
				globalVars.logger.byte_(buffer, 0, bufferLen);

				globalVars.logger.sys_(TAG, "Got [bytes]: " + bufferLen);

				for (int i = 0; i < bufferLen; i++) {

					lastMavLinkPacket = parser
							.mavlink_parse_char(buffer[i] & 0x00ff);
					if (lastMavLinkPacket != null) {
						MAVLinkMessage lastMavLinkMsg = lastMavLinkPacket
								.unpack();

						globalVars.logger.mavlinkMsg(lastMavLinkMsg);
						globalVars.logger.sys_(TAG,
								lastMavLinkPacket.toString() + " : "
										+ lastMavLinkMsg.toString());

					}
				}

				bufferLen = 0;
			}
		}

	}

	public void stopMe(boolean doStop) {
		running = doStop;

	}

}
