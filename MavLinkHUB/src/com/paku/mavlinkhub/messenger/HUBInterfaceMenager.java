package com.paku.mavlinkhub.messenger;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.paku.mavlinkhub.HUBGlobals;
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
	}

	// IQueueMsgItemReady();
	// *********************************************************
	public void registerForOnQueueMsgItemReady(Fragment fragment) {
		listenersIQueueMsgItemReady.add((IQueueMsgItemReady) fragment);
	}

	public void unregisterFromOnQueueMsgItemReady(Fragment fragment) {
		listenersIQueueMsgItemReady.remove((IQueueMsgItemReady) fragment);
	}

	public void processOnQueueMsgItemReady() {
		for (IQueueMsgItemReady listener : listenersIQueueMsgItemReady) {
			if (listener != null) listener.onQueueMsgItemReady();
		}
	}

	// IQueueMsgItemSent();
	// *********************************************************
	public void registerForOnQueueMsgItemSent(Fragment fragment) {
		listenersIQueueMsgItemSent.add((IQueueMsgItemSent) fragment);
	}

	public void unregisterFromOnQueueMsgItemSent(Fragment fragment) {
		listenersIQueueMsgItemSent.remove((IQueueMsgItemSent) fragment);
	}

	public void processOnQueueMsgItemSent() {
		for (IQueueMsgItemSent listener : listenersIQueueMsgItemSent) {
			if (listener != null) listener.onQueueMsgItemSent();
		}
	}

	// IUiModeChanged
	// *********************************************************
	public void registerForOnUiModeChanged(Fragment fragment) {
		listenersIUiModeChanged.add((IUiModeChanged) fragment);
	}

	public void unregisterFromOnUiModeChanged(Fragment fragment) {
		listenersIUiModeChanged.remove((IUiModeChanged) fragment);
	}

	public void processOnUiModeChanged() {
		for (IUiModeChanged listener : listenersIUiModeChanged) {
			if (listener != null) listener.onUiModeChanged();
		}
	}

	// IDataUpdateStats
	// *********************************************************
	public void registerForOnDataUpdateStats(Fragment fragment) {
		listenersIDataUpdateStats.add((IDataUpdateStats) fragment);
	}

	public void unregisterFromOnDataUpdateStats(Fragment fragment) {
		listenersIDataUpdateStats.remove((IDataUpdateStats) fragment);
	}

	public void processOnDataUpdateStats() {
		for (IDataUpdateStats listener : listenersIDataUpdateStats) {
			if (listener != null) listener.onDataUpdateStats();
		}
		// call main activity as well.
		if ((IDataUpdateStats) mainActivity != null) ((IDataUpdateStats) mainActivity).onDataUpdateStats();
	}

	// IDataUpdateSysLog
	// *********************************************************
	public void registerForOnDataUpdateSysLog(Fragment fragment) {
		listenersIDataUpdateSysLog.add((IDataUpdateSysLog) fragment);
	}

	public void unregisterFromOnDataUpdateSysLog(Fragment fragment) {
		listenersIDataUpdateSysLog.remove((IDataUpdateSysLog) fragment);
	}

	public void processOnDataUpdateSysLog() {
		for (IDataUpdateSysLog listener : listenersIDataUpdateSysLog) {
			if (listener != null) listener.onDataUpdateSysLog();
		}
	}

	// IDataUpdateByteLog
	// *********************************************************
	public void registerForOnDataUpdateByteLog(Fragment fragment) {
		listenersIDataUpdateByteLog.add((IDataUpdateByteLog) fragment);
	}

	public void unregisterFromOnDataUpdateByteLog(Fragment fragment) {
		listenersIDataUpdateByteLog.remove((IDataUpdateByteLog) fragment);
	}

	public void processOnDataUpdateByteLog() {
		for (IDataUpdateByteLog listener : listenersIDataUpdateByteLog) {
			if (listener != null) listener.onDataUpdateByteLog();
		}
	}

	// IDroneConnectionFailed
	// *********************************************************
	public void registerForOnDroneConnectionFailed(Fragment fragment) {
		listenersIDroneConnectionFailed.add((IDroneConnectionFailed) fragment);
	}

	public void unregisterFromOnDroneConnectionFailed(Fragment fragment) {
		listenersIDroneConnectionFailed.remove((IDroneConnectionFailed) fragment);
	}

	public void processOnDroneConnectionFailed(String msg) {
		for (IDroneConnectionFailed listener : listenersIDroneConnectionFailed) {
			if (listener != null) listener.onDroneConnectionFailed(msg);
		}
	}

	// IDroneConnected
	// *********************************************************
	public void registerForOnDroneConnected(Fragment fragment) {
		listenersIDroneConnected.add((IDroneConnected) fragment);
	}

	public void unregisterFromOnDroneConnected(Fragment fragment) {
		listenersIDroneConnected.remove((IDroneConnected) fragment);
	}

	public void processOnDroneConnected() {
		for (IDroneConnected listener : listenersIDroneConnected) {
			if (listener != null) listener.onDroneConnected();
		}
	}

	// IServerStarted
	// *********************************************************
	public void registerForOnServerStarted(Fragment fragment) {
		listenersIServerStarted.add((IServerStarted) fragment);
	}

	public void unregisterFromOnServerStarted(Fragment fragment) {
		listenersIServerStarted.remove((IServerStarted) fragment);
	}

	public void processOnServerStarted() {
		for (IServerStarted listener : listenersIServerStarted) {
			if (listener != null) listener.onServerStarted();
		}
	}

}
