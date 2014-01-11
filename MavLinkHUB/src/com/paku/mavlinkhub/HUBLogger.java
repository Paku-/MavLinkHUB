package com.paku.mavlinkhub;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;

public class HUBLogger {

	private static final String TAG = "HUBLogger";

	private HUBGlobals globalVars;

	// log files & files writing streams
	private File byteLogFile, sysLogFile;
	private BufferedOutputStream mFileIncomingByteLogStream, mFileSysLogStream;

	// sys wide in memory logging streams
	// incoming bytes
	public ByteArrayOutputStream mInMemIncomingBytesStream;

	// in mem msgItems storage
	public ArrayList<ItemMavLinkMsg> mavlinkMsgItemsArray;

	public ByteArrayOutputStream mInMemSysLogStream;

	// stats vars
	public int statsReadByteCount = 0;
	private boolean lock = false;

	public HUBLogger(HUBGlobals context) {

		globalVars = context;

		// **** memory logging/store

		// set the system wide byte storage stream ready for data collecting..
		mInMemSysLogStream = new ByteArrayOutputStream();
		mInMemSysLogStream.reset();

		// set the system wide byte storage stream ready for data collecting..
		mInMemIncomingBytesStream = new ByteArrayOutputStream();
		mInMemIncomingBytesStream.reset();

		// set the decoded msgItems array ready.
		mavlinkMsgItemsArray = new ArrayList<ItemMavLinkMsg>();

		restartSysLog();
		restartByteLog();
		sysLog(TAG, "** MavLinkHUB Syslog Init **");

	}

	public void sysLog(String string) {
		String tempStr;

		tempStr = timeStamp() + string + "\n";

		// syslog write
		try {
			waitForLock();
			mFileSysLogStream.write(tempStr.getBytes(), 0, tempStr.length());
			mInMemSysLogStream.write(tempStr.getBytes(), 0, tempStr.length());
			releaseLock();
			globalVars.messanger.appMsgHandler.obtainMessage(HUBGlobals.MSG_DATA_UPDATE_SYSLOG).sendToTarget();
		}
		catch (IOException e1) {
			Log.d(TAG, "[sysLog] " + e1.getMessage());
		}
	}

	public void sysLog(String tag, String msg) {

		sysLog("[" + tag + "] " + msg);
		// Log.d(tag, msg);

	}

	public void byteLog(byte[] buffer, int pos, int bufferLen) {

		try {

			waitForLock();
			mFileIncomingByteLogStream.write(buffer, 0, bufferLen);
			mInMemIncomingBytesStream.write(buffer, 0, bufferLen);
			releaseLock();
			statsReadByteCount += bufferLen;
			globalVars.messanger.appMsgHandler.obtainMessage(HUBGlobals.MSG_DATA_UPDATE_STATS).sendToTarget();
			globalVars.messanger.appMsgHandler.obtainMessage(HUBGlobals.MSG_DATA_UPDATE_BYTELOG).sendToTarget();
		}
		catch (IOException e1) {
			Log.d(TAG, "[byteLog] " + e1.getMessage());
		}
	}

	public void storeMavLinkMsgItem(ItemMavLinkMsg msgItem) {
		// fill msgs stream with new arrival

		mavlinkMsgItemsArray.add(msgItem);
		globalVars.messanger.appMsgHandler.obtainMessage(HUBGlobals.MSG_MAVLINK_MSG_READY, -1, -1, msgItem)
				.sendToTarget();

	}

	public void restartByteLog() {

		byteLogFile = new File(globalVars.getExternalFilesDir(null), "bytes.txt");

		// **** file logging
		// byte log stream
		try {
			mFileIncomingByteLogStream = new BufferedOutputStream(new FileOutputStream(byteLogFile, false), 1024);
		}
		catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}

	}

	public void restartSysLog() {

		sysLogFile = new File(globalVars.getExternalFilesDir(null), "syslog.txt");

		// syslog stream
		try {
			mFileSysLogStream = new BufferedOutputStream(new FileOutputStream(sysLogFile, false), 1024);
		}
		catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}

	}

	public void stopByteLog() {

		try {
			mFileIncomingByteLogStream.flush(); // working ??
			mFileIncomingByteLogStream.close();
		}
		catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}
	}

	public void stopSysLog() {

		try {
			mFileSysLogStream.flush(); // working ??
			mFileSysLogStream.close();
		}
		catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}
	}

	public void stopAllLogs() {
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

	@SuppressLint("SimpleDateFormat")
	public String timeStamp() {

		SimpleDateFormat s = new SimpleDateFormat("[hh:mm:ss.SSS]");
		return s.format(new Date());

		// Time dtNow = new Time();
		// dtNow.setToNow();
		// return dtNow.format("[%Y.%m.%d %H:%M]");

		// SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss.SSS");

		// return dtNow.format("[%H:%M:%S.%ss]");
		// int hours = dtNow.hour;

		// String lsYMD = dtNow.toString(); // YYYYMMDDTHHMMSS

	}

}
