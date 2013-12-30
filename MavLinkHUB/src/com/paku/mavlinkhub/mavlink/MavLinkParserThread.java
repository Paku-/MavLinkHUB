package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import android.util.Log;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;

public class MavLinkParserThread extends Thread {

	private static final String TAG = "MavLinkParserThread";

	private Parser parser;
	// data read stream
	private ByteArrayInputStream mMavLinkInputStream;
	private ObjectOutputStream mPacketsOutputStream;
	private boolean running = true;
	private MAVLinkPacket lastMavLinkPacket = null;

	public MavLinkParserThread(ByteArrayOutputStream mDataStream,
			ObjectOutputStream mOutputStream) {
		parser = new Parser();

		// read this
		mMavLinkInputStream = new ByteArrayInputStream(
				mDataStream.toByteArray());
		// write pockets here
		mPacketsOutputStream = mOutputStream;
		running = true;

	}

	public void run() {

		while (running) {

			lastMavLinkPacket = null;

			while (mMavLinkInputStream.available() > 0) {

				while (((lastMavLinkPacket == null) && (mMavLinkInputStream
						.available() > 0))

				) {

					lastMavLinkPacket = parser
							.mavlink_parse_char(mMavLinkInputStream.read());
				}
				;
				/*
				 * try { if (lastMavLinkPacket != null)
				 * mMavLinkPacketsObjectsOutputStream
				 * .writeObject(lastMavLinkPacket); } catch (IOException e) {
				 * Log.d(TAG, "ObjectStream write: " + e.getMessage());
				 * e.printStackTrace(); }
				 */

				// Log.d(TAG,"ObjBuff size: " + mPacketsOutputStream.);

				if (lastMavLinkPacket != null)
					Log.d(TAG, "Got packet " + lastMavLinkPacket.seq + " "
							+ lastMavLinkPacket.msgid);
				else
					Log.d(TAG, " ** Null **");

			}

			// mdatast.reset();

		}
	}

	public void stopMe(boolean doStop) {
		running = doStop;
	}

}
