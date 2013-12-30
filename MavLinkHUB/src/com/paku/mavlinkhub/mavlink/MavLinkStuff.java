package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import android.content.Context;
import android.util.Log;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

public class MavLinkStuff implements IBufferReady {

	private static final String TAG = "MavLinkStuff";

	private AppGlobals globalVars;

	// here data arrive
	private ByteArrayOutputStream mMavLinkTempOutputStream;

	// And are stored in this stream for further distribution
	private ByteArrayOutputStream mMavLinkPacketByteOutputStream;

	// true parser output - the mavlink pockets stream
	public ObjectOutputStream mMavLinkPacketObjectsOutputStream;
	
	private MavLinkParserThread parserThread;

	public MavLinkStuff(Context mConext) {

		globalVars = ((AppGlobals) mConext.getApplicationContext());

		// set the arrival data stream ready for data collecting..
		mMavLinkTempOutputStream = new ByteArrayOutputStream();
		mMavLinkTempOutputStream.reset();

		// set the decoded packets streams ready.
		try {
			// mavlink packets as byte data
			mMavLinkPacketByteOutputStream = new ByteArrayOutputStream();
			// true mavlink packets objects stream (based on above)
			mMavLinkPacketObjectsOutputStream = new ObjectOutputStream(
					mMavLinkPacketByteOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		globalVars.mBtConnector.registerForIBufferReady(this);
		
		parserThread = new MavLinkParserThread(mMavLinkTempOutputStream, mMavLinkPacketObjectsOutputStream);

	}

	@Override
	public void onBufferReady() {

		try {
			// get data copy
			globalVars.mBtConnector.mConnectorStream
					.writeTo(mMavLinkTempOutputStream);
		} catch (IOException e) {
			Log.d(TAG, "Stream copy: " + e.getMessage());
			e.printStackTrace();
		}

		Log.d(TAG, "Buff START size: " + mMavLinkTempOutputStream.size());

	}
	
	public void startMavLinkParserThread()
	{
		parserThread.start();
	}
	
	public void stopMavLinkParserThread(){
		parserThread.stopMe(true);
	}

}
