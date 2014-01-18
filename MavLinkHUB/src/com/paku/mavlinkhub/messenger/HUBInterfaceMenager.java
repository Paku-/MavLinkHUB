package com.paku.mavlinkhub.messenger;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.interfaces.IDataUpdateByteLog;
import com.paku.mavlinkhub.interfaces.IDroneConnected;
import com.paku.mavlinkhub.interfaces.IDroneConnectionFailed;
import com.paku.mavlinkhub.interfaces.IDataUpdateStats;
import com.paku.mavlinkhub.interfaces.IDataUpdateSysLog;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemReady;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemSent;
import com.paku.mavlinkhub.interfaces.IServerStarted;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

public class HUBInterfaceMenager {

	protected HUBGlobals app;

	class Frags {
		ArrayList<Fragment> fragsArray;

		public Frags() {
			fragsArray = new ArrayList<Fragment>(0);
		}
	}

	ArrayList<Frags> listeners = new ArrayList<Frags>(0);

	// lists holding fragments registered for particular interface
	ArrayList<IServerStarted> listenersIServerStarted = new ArrayList<IServerStarted>();

	ArrayList<IDroneConnected> listenersIDroneConnected = new ArrayList<IDroneConnected>();
	ArrayList<IDroneConnectionFailed> listenersIDroneConnectionFailed = new ArrayList<IDroneConnectionFailed>();

	ArrayList<IUiModeChanged> listenersIUiModeChanged = new ArrayList<IUiModeChanged>();

	ArrayList<IDataUpdateSysLog> listenersIDataUpdateSysLog = new ArrayList<IDataUpdateSysLog>();
	ArrayList<IDataUpdateByteLog> listenersIDataUpdateByteLog = new ArrayList<IDataUpdateByteLog>();
	ArrayList<IDataUpdateStats> listenersIDataUpdateStats = new ArrayList<IDataUpdateStats>();

	ArrayList<IQueueMsgItemReady> listenersIQueueMsgItemReady = new ArrayList<IQueueMsgItemReady>();
	ArrayList<IQueueMsgItemSent> listenersIQueueMsgItemSent = new ArrayList<IQueueMsgItemSent>();

	public FragmentActivity mainActivity;

	public HUBInterfaceMenager(HUBGlobals hubContext) {
		app = ((HUBGlobals) hubContext.getApplicationContext());

		// create empty fragments lists
		for (@SuppressWarnings("unused")
		APP_STATE msg : APP_STATE.values()) {
			Frags frags = new Frags();
			frags.fragsArray.clear();
			frags.fragsArray.trimToSize();
			listeners.add(frags);

		}
	}

	public void register(Fragment fragment, APP_STATE msg) {
		Frags frags = listeners.get(msg.ordinal());
		frags.fragsArray.add(fragment);
		frags.fragsArray.trimToSize();
	}

	public void unregister(Fragment fragment, APP_STATE msg) {
		Frags frags = listeners.get(msg.ordinal());
		frags.fragsArray.remove(fragment);
		frags.fragsArray.trimToSize();
	}

	public void call(APP_STATE msg) {

		// main activity is not a fragment :(
		if ((msg == APP_STATE.MSG_DATA_UPDATE_STATS) && (IDataUpdateStats) mainActivity != null) ((IDataUpdateStats) mainActivity).onDataUpdateStats();

		for (Fragment fragment : listeners.get(msg.ordinal()).fragsArray) {
			if (fragment != null) {
				APP_STATE[] msgs = APP_STATE.values();
				switch (msgs[msg.ordinal()]) {
				case MSG_QUEUE_MSGITEM_READY:
					((IQueueMsgItemReady) fragment).onQueueMsgItemReady();
					break;
				case MSG_QUEUE_MSGITEM_SENT:
					((IQueueMsgItemSent) fragment).onQueueMsgItemSent();
					break;
				case MSG_UI_MODE_CHANGED:
					((IUiModeChanged) fragment).onUiModeChanged();
					break;
				case MSG_DATA_UPDATE_STATS:
					((IDataUpdateStats) fragment).onDataUpdateStats();
					break;
				case MSG_DATA_UPDATE_SYSLOG:
					((IDataUpdateSysLog) fragment).onDataUpdateSysLog();
					break;
				case MSG_DATA_UPDATE_BYTELOG:
					((IDataUpdateByteLog) fragment).onDataUpdateByteLog();
					break;
				case MSG_DRONE_CONNECTED:
					((IDroneConnected) fragment).onDroneConnected();
					break;
				case MSG_SERVER_STARTED:
					((IServerStarted) fragment).onServerStarted();
					break;

				default:
					break;
				}
			}
		}
	}

	// string carring msgs only here ...
	public void call(APP_STATE msg, String txt) {
		for (Fragment fragment : listeners.get(msg.ordinal()).fragsArray) {
			if (fragment != null) {
				APP_STATE[] msgs = APP_STATE.values();
				switch (msgs[msg.ordinal()]) {
				case MSG_DRONE_CONNECTION_FAILED:
					((IDroneConnectionFailed) fragment).onDroneConnectionFailed(txt);
					break;
				default:
					break;
				}
			}
		}
	}

}
