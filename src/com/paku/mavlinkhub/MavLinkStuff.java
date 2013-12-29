package com.paku.mavlinkhub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;

import android.content.Context;
import android.util.Log;

import com.MAVLink.Parser;
import com.MAVLink.Messages.MAVLinkPacket;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

public class MavLinkStuff implements IBufferReady {

	private static final String TAG = "MavLinkStuff";

	private AppGlobals globalVars;

	// here data arrive
	private ByteArrayOutputStream mMavLinkOutputStream;
	// then we read it suing this
	private ByteArrayInputStream mMavLinkInputStream;

	// parsed packets are stream here
	private ObjectOutputStream mMavLinkPacketsObjectsOutputStream;
	// And stored in this stream for further distribution
	private ByteArrayOutputStream mMavLinkPacketsOutputStream;

	private Parser parser;

	public MavLinkStuff(Context mConext) {

		globalVars = ((AppGlobals) mConext.getApplicationContext());

		// set the arrival data stream ready..
		mMavLinkOutputStream = new ByteArrayOutputStream();
		mMavLinkOutputStream.reset();

		// set the decoded packets streams ready.
		try {
			mMavLinkPacketsOutputStream = new ByteArrayOutputStream();
			mMavLinkPacketsObjectsOutputStream = new ObjectOutputStream(
					mMavLinkPacketsOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		parser = new Parser();

		globalVars.mBtConnector.registerForIBufferReady(this);

	}

	@Override
	public void onBufferReady() {

		MAVLinkPacket lastMavLinkPacket = null;

		try {
			globalVars.mBtConnector.mConnectorStream
					.writeTo(mMavLinkOutputStream);
		} catch (IOException e) {
			Log.d(TAG, "Stream copy: " + e.getMessage());
			e.printStackTrace();
		}

		Log.d(TAG, "Buff START size: " + mMavLinkOutputStream.size());

		mMavLinkInputStream = new ByteArrayInputStream(
				mMavLinkOutputStream.toByteArray());

		while (mMavLinkInputStream.available() > 0) {

			while (( (lastMavLinkPacket == null)&& (mMavLinkInputStream.available() > 0))

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

			Log.d(TAG, "ObjBuff size: " + mMavLinkPacketsOutputStream.size());
			if (lastMavLinkPacket != null)
			Log.d(TAG, "Got packet " + lastMavLinkPacket.seq+" "+lastMavLinkPacket.msgid);


		}
		
		mMavLinkOutputStream.reset();
		
	}
}
