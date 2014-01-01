package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import android.content.Context;
import com.paku.mavlinkhub.communication.AppGlobals;


public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	// here data arrive
	public ByteArrayOutputStream mByteLogStream;

	// And are stored in this stream for further distribution
	private ByteArrayOutputStream mMavLinkMsgByteBackgroundOutputStream;

	// true parser output - the mavlink pockets stream
	public ObjectOutputStream mMavLinkMsgLogOutputStream;
	
	private MavLinkParserThread parserThread;

	public MavLinkCollector(Context mConext) {

		globalVars = ((AppGlobals) mConext.getApplicationContext());

		// set the arrival data stream ready for data collecting..
		mByteLogStream = new ByteArrayOutputStream();
		mByteLogStream.reset();

		// set the decoded msg streams ready.
		try {
			// mavlink msg objects as byte data
			mMavLinkMsgByteBackgroundOutputStream = new ByteArrayOutputStream();
			// true mavlink msg objects stream (based on above)
			mMavLinkMsgLogOutputStream = new ObjectOutputStream(
					mMavLinkMsgByteBackgroundOutputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//globalVars.mBtConnector.registerForIBufferReady(this);
		
	}


	public void startMavLinkParserThread()
	{
		parserThread = new MavLinkParserThread(globalVars.mBtConnector.mConnectorStream,mByteLogStream, mMavLinkMsgLogOutputStream,globalVars.sysStatsHolder);
		parserThread.start();
	}
	
	public void stopMavLinkParserThread(){
		parserThread.stopMe(true);
	}

}
