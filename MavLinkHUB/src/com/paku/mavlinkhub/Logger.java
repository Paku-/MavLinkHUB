package com.paku.mavlinkhub;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.util.Log;

import com.MAVLink.Messages.MAVLinkMessage;
import com.paku.mavlinkhub.communication.AppGlobals;

public class Logger {

	private static final String TAG = "Logger";

	private AppGlobals appContext;

	// log files & files writing streams
	private File byteLogFile, sysLogFile;
	private BufferedOutputStream mFileIncomingByteLogStream, mFileSysLogStream;

	// sys wide in memory logging streams
	// incoming bytes
	public ByteArrayOutputStream mInMemIncomingBytesStream;
	// Mavlink messages as bytes
	private ByteArrayOutputStream mInMemMsgBackgroundStream;
	// true parser output - Mavlink messages objects stream
	public ObjectOutputStream mInMemMsgStream;
	public ByteArrayOutputStream mInMemSysLogStream;

	// stats vars
	public int statsReadByteCount = 0;
	private boolean lock = false;

	public Logger(AppGlobals context) {

		appContext = context;

	
		// **** memory logging/store
		
		// set the system wide byte storage stream ready for data collecting..
		mInMemSysLogStream = new ByteArrayOutputStream();
		mInMemSysLogStream.reset();


		// set the system wide byte storage stream ready for data collecting..
		mInMemIncomingBytesStream = new ByteArrayOutputStream();
		mInMemIncomingBytesStream.reset();

		// set the decoded msg streams ready.
		try {
			// mavlink msg objects as byte data
			mInMemMsgBackgroundStream = new ByteArrayOutputStream();
			// true system wide mavlink msg objects stream (based on above)
			mInMemMsgStream = new ObjectOutputStream(
					mInMemMsgBackgroundStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sys_(String string) {
		String tempStr;

		tempStr = string + "\n";

		// syslog write
		try {
			waitForLock();
			mFileSysLogStream.write(tempStr.getBytes(), 0, tempStr.length());
			mInMemSysLogStream.write(tempStr.getBytes(),0,tempStr.length());
			releaseLock();
		} catch (IOException e1) {
			Log.d(TAG, e1.getMessage());
		}

	}
	
	public void sys_(String tag,String msg) {
		
		sys_("["+tag+"] "+msg);
		Log.d(tag, msg);

	}	
	
	public void sys_(byte[] buffer, int pos, int bufferLen) {

	}
	

	public void byte_(String string) {

	}


	public void byte_(byte[] buffer, int pos, int bufferLen) {

		try {
			
			waitForLock();
			mFileIncomingByteLogStream.write(buffer, 0, bufferLen);
			mInMemIncomingBytesStream.write(buffer, 0, bufferLen);
			releaseLock();
			statsReadByteCount += bufferLen;
		} catch (IOException e1) {
			Log.d(TAG, e1.getMessage());
		}

	}

	public void mavlinkMsg(MAVLinkMessage msg) {
		// fill msgs stream with new arrival
		try {
			mInMemMsgStream.writeObject(msg);
		} catch (IOException e) {
			Log.d(TAG, "MsgStream write: " + e.getMessage());
		}

	}
	

	public void restartByteLog() {
		
		byteLogFile = new File(appContext.getExternalFilesDir(null),
				"bytes.txt");

		// **** file logging
		// byte log stream
		try {
			mFileIncomingByteLogStream = new BufferedOutputStream(new FileOutputStream(
					byteLogFile, false),1024);
		} catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}

		
	}
	
	public void restartSysLog() {
		
		sysLogFile = new File(appContext.getExternalFilesDir(null),
				"syslog.txt");

		// syslog stream
		try {
			mFileSysLogStream = new BufferedOutputStream(new FileOutputStream(
					sysLogFile, false),1024);
		} catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}

		
	}
	
	public void stopByteLog() {

		try {
			mFileIncomingByteLogStream.flush(); // working ??
			mFileIncomingByteLogStream.close();
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}
	}

	public void stopSysLog() {

		try {
			mFileSysLogStream.flush(); // working ??
			mFileSysLogStream.close();
		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}
	}
	
	public void stopAllLogs(){
		stopByteLog();
		stopSysLog();		
	}
	
	
	public void waitForLock() {
		while (lock) {
			;
		}

		lock = true;
	}

	public void releaseLock() {
		lock = false;
	}	
	

}
