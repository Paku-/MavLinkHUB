package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.util.Log;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.Messages.MAVLinkPacket;

public class MavLinkParserThread extends Thread {

	private static final String TAG = "MavLinkParserThread";

	private Parser parser;

	private byte[] buffer;
	private int bufferLen;

	// data read stream

	private ByteArrayOutputStream mMLCollectorTempByteDataStream;
	private ObjectOutputStream mMavLinkMsgsOutputStream;
	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	public MavLinkParserThread(ByteArrayOutputStream mDataStream,
			ObjectOutputStream mOutputStream) {

		parser = new Parser();

		// read this
		mMLCollectorTempByteDataStream = mDataStream;

		// write pockets here
		mMavLinkMsgsOutputStream = mOutputStream;
		running = true;

	}

	public void run() {

		while (running) {


			if (mMLCollectorTempByteDataStream.size() > 0) {
				lastMavLinkPacket = null;
				buffer = mMLCollectorTempByteDataStream.toByteArray();
				bufferLen = mMLCollectorTempByteDataStream.size();
				mMLCollectorTempByteDataStream.reset();
				Log.d(TAG, "Parser IN: " + bufferLen);
			}

			for (int i = 0; i < bufferLen; i++) {
				lastMavLinkPacket = parser.mavlink_parse_char(buffer[i]& 0x00ff);
				if (lastMavLinkPacket != null) {
					Log.d(TAG, "Pkg: " + lastMavLinkPacket.seq + " "
							+ lastMavLinkPacket.msgid);
					MAVLinkMessage lastMavLinkMsg = lastMavLinkPacket.unpack();
					Log.d(TAG, "Msg: " + lastMavLinkMsg.toString());					
					
					//fill msgs stream with new arrival
					try {
						mMavLinkMsgsOutputStream.writeObject(lastMavLinkMsg);
					} catch (IOException e) {
						Log.d(TAG, "MsgStream write: " + e.getMessage());
						//e.printStackTrace();
					}
					
					/*
					try {
						if (lastMavLinkPacket != null)
							mPacketsOutputStream.writeObject(lastMavLinkPacket);						

					} catch (IOException e) {
						Log.d(TAG, "ObjectStream write: " + e.getMessage());
						e.printStackTrace();
					}
*/
				}
			}

			bufferLen = 0;
		}

	}

	public void stopMe(boolean doStop) {
		running = doStop;
	}

}
