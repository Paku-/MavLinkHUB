package com.paku.mavlinkhub.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.MSG_SOURCE;

import android.annotation.SuppressLint;
import android.util.Log;

public class HUBLogger {

	private static final String TAG = HUBLogger.class.getSimpleName();

	private final HUBGlobals hub;

	// log files & files writing streams
	private File byteLogFile, sysLogFile;
	private BufferedOutputStream mFileByteLogStream, mFileSysLogStream;

	// sys wide in memory logging streams
	// incoming bytes

	public StringBuilder inMemByteLogBuffer;
	public StringBuilder inMemSysLogBuffer;

	// system stats holding object
	public HUBStats hubStats;

	public HUBLogger(HUBGlobals hubContext) {

		hub = hubContext;

		hubStats = new HUBStats();

		inMemSysLogBuffer = new StringBuilder(2 * HUBGlobals.visibleByteLogSize);
		restartSysLog();

		inMemByteLogBuffer = new StringBuilder(2 * HUBGlobals.visibleByteLogSize);
		restartByteLog();

		sysLog(TAG, "** MavLinkHUB Syslog Init **");

	}

	public void sysLog(String string) {
		String tempStr;

		tempStr = timeStamp() + string + "\n";

		// syslog write
		try {
			synchronized (inMemSysLogBuffer) {
				mFileSysLogStream.write(tempStr.getBytes(), 0, tempStr.length());
				inMemSysLogBuffer.append(tempStr);
			}
			HUBGlobals.sendAppMsg(APP_STATE.MSG_DATA_UPDATE_SYSLOG);
		}
		catch (IOException e1) {
			Log.d(TAG, "[sysLog] " + e1.getMessage());
		}
	}

	public void sysLog(String tag, String msg) {

		sysLog("[" + tag + "] " + msg);
		// Log.d(tag, msg);

	}

	public void byteLog(MSG_SOURCE direction, ByteBuffer buffer) {

		// log only Drone data to the bytelog file
		if (buffer != null && direction == MSG_SOURCE.FROM_DRONE) {
			synchronized (inMemByteLogBuffer) {
				// mFileByteLogStream.write(buffer.array(), 0,
				// buffer.limit());

				inMemByteLogBuffer.append(new String(buffer.array(), 0, buffer.limit()));
				// trim to the limit
				if (inMemByteLogBuffer.length() > HUBGlobals.visibleByteLogSize * 1.5) {
					inMemByteLogBuffer.replace(0, inMemByteLogBuffer.length() - HUBGlobals.visibleByteLogSize, "[***]\r\n");
				}
			}
			HUBGlobals.sendAppMsg(APP_STATE.MSG_DATA_UPDATE_BYTELOG);
		}
	}

	// Log.d(TAG, "[byteLog] " + e1.getMessage());
	public void restartByteLog() {

		byteLogFile = new File(getLoggerStorageLocation(null), getLoggerFileName("byte"));
		try {
			mFileByteLogStream = new BufferedOutputStream(new FileOutputStream(byteLogFile, false), 1024);
		}
		catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}

		inMemSysLogBuffer.delete(0, inMemByteLogBuffer.length());

	}

	public void restartSysLog() {

		sysLogFile = new File(getLoggerStorageLocation(null), getLoggerFileName("sys"));
		try {
			mFileSysLogStream = new BufferedOutputStream(new FileOutputStream(sysLogFile, false), 1024);
		}
		catch (FileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}

		inMemSysLogBuffer.delete(0, inMemSysLogBuffer.length());

	}

	private File getLoggerStorageLocation(String txt) {
		return hub.getExternalFilesDir(txt);
	}

	private String getLoggerFileName(String name) {
		return System.currentTimeMillis() + "-" + name + ".txt";
	}

	public void stopByteLog() {
		stopLog(mFileByteLogStream);
	}

	public void stopSysLog() {
		stopLog(mFileSysLogStream);
	}

	private void stopLog(BufferedOutputStream stream) {
		try {
			stream.flush(); // working ??
			stream.close();
		}
		catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}

	}

	public void stopAllLogs() {
		stopByteLog();
		stopSysLog();
	}

	@SuppressLint("SimpleDateFormat")
	public String timeStamp() {

		SimpleDateFormat s = new SimpleDateFormat("[HH:mm:ss.SSS]");
		// s.setTimeZone(TimeZone.getTimeZone("UTC"));
		s.setTimeZone(TimeZone.getDefault());
		return s.format((new Date()));

		// Time dtNow = new Time(Time.getCurrentTimezone());
		// dtNow.setToNow();
		// return dtNow.format("[%H:%M:%S]");
		// return dtNow.format("[%Y.%m.%d %H:%M]");

		// SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss.SSS");

		// return dtNow.format("[%H:%M:%S.%ss]");
		// int hours = dtNow.hour;

		// String lsYMD = dtNow.toString(); // YYYYMMDDTHHMMSS

	}

	public String getByteLog() {
		synchronized (inMemByteLogBuffer) {
			return inMemByteLogBuffer.toString();
		}
	}

	public String getSysLog() {
		synchronized (inMemSysLogBuffer) {
			return inMemSysLogBuffer.toString();
		}
	}

}
