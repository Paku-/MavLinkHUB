package com.paku.mavlinkhub.mavlink;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import android.content.Context;
import com.paku.mavlinkhub.communication.AppGlobals;

public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	// here data arrive
	public ByteArrayOutputStream mByteSysWideLogStream;

	// And are stored in this stream for further distribution
	private ByteArrayOutputStream mMsgByteBasedBackgroundStream;

	// true parser output - the mavlink pockets stream
	public ObjectOutputStream mMsgSysWideLogStream;

	private MavLinkParserThread parserThread;

	public MavLinkCollector(Context mConext) {

		globalVars = ((AppGlobals) mConext.getApplicationContext());

		// set the system wide byte stream ready for data collecting..
		mByteSysWideLogStream = new ByteArrayOutputStream();
		mByteSysWideLogStream.reset();

		// set the decoded msg streams ready.
		try {
			// mavlink msg objects as byte data
			mMsgByteBasedBackgroundStream = new ByteArrayOutputStream();
			// true system wide mavlink msg objects stream (based on above)
			mMsgSysWideLogStream = new ObjectOutputStream(
					mMsgByteBasedBackgroundStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// globalVars.mBtConnector.registerForIBufferReady(this);

	}

	public void startMavLinkParserThread() {

		File logFile = new File(globalVars.appContext.getExternalFilesDir(null),
				"paku.txt");

		parserThread = new MavLinkParserThread(
				globalVars.mBtConnector.mConnectorStream,
				mByteSysWideLogStream, mMsgSysWideLogStream,
				globalVars.sysStatsHolder, logFile);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		parserThread.stopMe(true);
	}

}
