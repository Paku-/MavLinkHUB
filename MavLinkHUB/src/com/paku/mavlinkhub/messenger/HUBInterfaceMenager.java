// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
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
import com.paku.mavlinkhub.interfaces.IDroneConnectionLost;
import com.paku.mavlinkhub.interfaces.IDroneDisconnected;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemReady;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemSent;
import com.paku.mavlinkhub.interfaces.IServerStarted;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

public class HUBInterfaceMenager {

	@SuppressWarnings("unused")
	private static final String TAG = HUBInterfaceMenager.class.getSimpleName();

	protected HUBGlobals hub;

	static class Frags {
		APP_STATE msg_name;
		ArrayList<Fragment> fragsArray;

		public Frags(APP_STATE name) {
			msg_name = name;
			fragsArray = new ArrayList<Fragment>(0);
		}
	}

	ArrayList<Frags> listeners = new ArrayList<Frags>(0);

	public FragmentActivity mainActivity;

	public HUBInterfaceMenager(HUBGlobals hubContext) {
		hub = ((HUBGlobals) hubContext.getApplicationContext());

		// create empty fragments lists, for every interface we have defined in
		// APP_STATE enum
		for (APP_STATE msg : APP_STATE.values()) {
			Frags frags = new Frags(msg);
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

	// msgs not having payload.
	public void callFragments(APP_STATE msg) {

		//		Log.d(TAG, "call(): " + msg.toString());

		// main activity is not a fragment :(
		if ((msg == APP_STATE.MSG_DATA_UPDATE_STATS) && null != (IDataUpdateStats) mainActivity) ((IDataUpdateStats) mainActivity).onDataUpdateStats();

		for (Fragment fragment : listeners.get(msg.ordinal()).fragsArray) {
			if (null != fragment) {
				APP_STATE[] msgs = APP_STATE.values();
				switch (msgs[msg.ordinal()]) {
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
				case MSG_DRONE_DISCONNECTED:
					((IDroneDisconnected) fragment).onDroneDisconnected();
					break;
				case MSG_DRONE_CONNECTION_LOST:
					((IDroneConnectionLost) fragment).onDroneConnectionLost();
					break;
				case MSG_DRONE_CONNECTION_ATTEMPT_FAILED:
					((IDroneConnectionFailed) fragment).onDroneConnectionFailed();
					break;
				default:
					break;
				}
			}
		}
	}

	// ItemMavLinkMsg msgs only here ...
	public void callFragments(APP_STATE msg, ItemMavLinkMsg msgItem) {

		//		Log.d(TAG, "call(mgsItem): " + msg.toString());

		for (Fragment fragment : listeners.get(msg.ordinal()).fragsArray) {
			if (null != fragment) {
				APP_STATE[] msgs = APP_STATE.values();
				switch (msgs[msg.ordinal()]) {
				case MSG_QUEUE_MSGITEM_READY:
					((IQueueMsgItemReady) fragment).onQueueMsgItemReady(msgItem);
					break;
				default:
					break;
				}
			}
		}
	}

	// string msgs only here ... 

	public void callFragments(APP_STATE msg, String txt) {

		//		Log.d(TAG, "call(txt): " + msg.toString());

		for (Fragment fragment : listeners.get(msg.ordinal()).fragsArray) {
			if (fragment != null) {
				APP_STATE[] msgs = APP_STATE.values();
				switch (msgs[msg.ordinal()]) {
				case MSG_SERVER_STARTED:
					((IServerStarted) fragment).onServerStarted(txt);
					break;
				default:
					break;
				}
			}
		}
	}

}
