package com.paku.mavlinkhub.mavlink;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.interfaces.IDataLoggedIn;

public class MavLinkCollector {

	@SuppressWarnings("unused")
	private static final String TAG = "MavLinkCollector";

	private AppGlobals globalVars;

	private MavLinkParserThread parserThread;
	private Handler mavlinkCollectorMsgHandler;

	// interface
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

	public void processMavLinkCollectorDataLoggedIn() {

		if (callRealTimeMavlinkFragment != null) {
			callRealTimeMavlinkFragment.onDataLoggedInReady();
		}

		if (callSysLogFragment != null) {
			callSysLogFragment.onDataLoggedInReady();
		}

	}

	// interface end

	public MavLinkCollector(Context mContext) {

		globalVars = ((AppGlobals) mContext.getApplicationContext());

	}

	public void startMavLinkParserThread() {

		// prepare msg handler for MLMsg_ready msg.

		mavlinkCollectorMsgHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {

				switch (msg.what) {
				// Received MLmsg
				case AppGlobals.MSG_MAVLINK_MSG_READY:
					break;
				// all data logged in
				case AppGlobals.MSG_LOGGER_DATA_READY:
					processMavLinkCollectorDataLoggedIn();
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

		parserThread = new MavLinkParserThread(globalVars,
				mavlinkCollectorMsgHandler);
		parserThread.start();
	}

	public void stopMavLinkParserThread() {
		if (parserThread != null)
			parserThread.stopRunning();
	}

}
