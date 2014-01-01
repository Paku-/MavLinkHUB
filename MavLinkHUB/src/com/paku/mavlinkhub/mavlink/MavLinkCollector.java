package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import android.content.Context;
import android.util.Log;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

public class MavLinkCollector implements IBufferReady {

	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	// here data arrive
	private ByteArrayOutputStream mByteLogTempStream;

	// And are stored in this stream for further distribution
	private ByteArrayOutputStream mMavLinkMsgByteBackgroundOutputStream;

	// true parser output - the mavlink pockets stream
	public ObjectOutputStream mMavLinkMsgObjectsOutputStream;
	
	private MavLinkParserThread parserThread;

	public MavLinkCollector(Context mConext) {

		globalVars = ((AppGlobals) mConext.getApplicationContext());

		// set the arrival data stream ready for data collecting..
		mByteLogTempStream = new ByteArrayOutputStream();
		mByteLogTempStream.reset();

		// set the decoded msg streams ready.
		try {
			// mavlink msg objects as byte data
			mMavLinkMsgByteBackgroundOutputStream = new ByteArrayOutputStream();
			// true mavlink msg objects stream (based on above)
			mMavLinkMsgObjectsOutputStream = new ObjectOutputStream(
					mMavLinkMsgByteBackgroundOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		globalVars.mBtConnector.registerForIBufferReady(this);
		
	}

	@Override
	public void onBufferReady() {

		try {
			// get data copy
			//globalVars.mBtConnector.mConnectorStream
			//		.writeTo(mMavLinkTempOutputStream);
			globalVars.mBtConnector.copyConnectorStream(mByteLogTempStream,false);
		} catch (IOException e) {
			Log.d(TAG, "Stream copy: " + e.getMessage());
			e.printStackTrace();
		}

		globalVars.sysStatsHolder.statsByteCount+=mByteLogTempStream.size();
		
		Log.d(TAG, "Buff START size: " + mByteLogTempStream.size());

	}
	
	public void startMavLinkParserThread()
	{
		parserThread = new MavLinkParserThread(mByteLogTempStream, mMavLinkMsgObjectsOutputStream);
		parserThread.start();
	}
	
	public void stopMavLinkParserThread(){
		parserThread.stopMe(true);
	}

}
