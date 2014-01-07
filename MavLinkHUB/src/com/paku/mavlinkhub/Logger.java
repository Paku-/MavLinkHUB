package com.paku.mavlinkhub;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.paku.mavlinkhub.interfaces.IDataLoggedIn;
import com.paku.mavlinkhub.mavlink.MavLinkMsgItem;

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
	public ByteArrayOutputStream mInMemMsgBackgroundStream;
	// true parser output - Mavlink messages objects stream
	public ObjectOutputStream mInMemMsgStream;
	public ByteArrayOutputStream mInMemSysLogStream;

	// stats vars
	public int statsReadByteCount = 0;
	private boolean lock = false;

	// handler

	public Handler loggerMsgHandler;

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
			mInMemMsgStream = new ObjectOutputStream(mInMemMsgBackgroundStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// msg handler for asynch UI updates

		loggerMsgHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				// Received MLmsg
				case AppGlobals.MSG_MAVLINK_MSG_READY:
					break;
				// all data logged in
				case AppGlobals.MSG_SYSLOG_DATA_READY:
					processSysLogDataLoggedIn();
					break;
				case AppGlobals.MSG_BYTELOG_DATA_READY:
					processByteLogDataLoggedIn();
					break;

				case AppGlobals.MSG_CONNECTOR_DATA_READY:
					// globalVars.logger.byteLog((byte[]) msg.obj, 0, msg.arg1);
					// mavlinkCollectorMsgHandler
					// .obtainMessage(AppGlobals.MSG_LOGGER_DATA_READY).sendToTarget();
					break;
				default:
					super.handleMessage(msg);
				}

			}
		};

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
		} catch (IOException e1) {
			Log.d(TAG, "[sysLog] " + e1.getMessage());
		}
	}

	public void sysLog(String tag, String msg) {

		sysLog("[" + tag + "] " + msg);
		Log.d(tag, msg);

	}

	public void sys_(byte[] buffer, int pos, int bufferLen) {

	}

	public void byte_(String string) {

	}

	public void byteLog(byte[] buffer, int pos, int bufferLen) {

		try {

			waitForLock();
			mFileIncomingByteLogStream.write(buffer, 0, bufferLen);
			mInMemIncomingBytesStream.write(buffer, 0, bufferLen);
			releaseLock();
			statsReadByteCount += bufferLen;
		} catch (IOException e1) {
			Log.d(TAG, "[byteLog] " + e1.getMessage());
		}
	}

	public void streamMavLinkMsgItem(MavLinkMsgItem msgItem) {
		// fill msgs stream with new arrival
		try {
			mInMemMsgStream.writeObject(msgItem);
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
			mFileIncomingByteLogStream = new BufferedOutputStream(
					new FileOutputStream(byteLogFile, false), 1024);
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
					sysLogFile, false), 1024);
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

	// *****************************************
	// interface
	// *****************************************
	private IDataLoggedIn callRealTimeMavlinkFragment = null;
	private IDataLoggedIn callSysLogFragment = null;

	public void registerRealTimeMavlinkForIDataLoggedIn(Fragment fragment) {
		callRealTimeMavlinkFragment = (IDataLoggedIn) fragment;
	}

	public void registerSysLogForIDataLoggedIn(Fragment fragment) {
		callSysLogFragment = (IDataLoggedIn) fragment;
	}

	public void unregisterSysLogForIDataLoggedIn() {
		callSysLogFragment = null;
	}

	public void unregisterRealTimeMavlinkForIDataLoggedIn() {
		// TODO Auto-generated method stub
		callRealTimeMavlinkFragment = null;
	}

	public void processSysLogDataLoggedIn() {

		if (callSysLogFragment != null) {
			callSysLogFragment.onDataLoggedIn();
		}

	}

	public void processByteLogDataLoggedIn() {

		if (callRealTimeMavlinkFragment != null) {
			callRealTimeMavlinkFragment.onDataLoggedIn();
		}
	}

	/*
	 * runOnUiThread(new Runnable() { public void run() {
	 * titleProgress.setVisibility(View.VISIBLE); } });
	 */

	// *****************************************
	// interface end
	// *****************************************

}
