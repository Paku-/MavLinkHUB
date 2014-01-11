package com.paku.mavlinkhub;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.paku.mavlinkhub.interfaces.IDataUpdateByteLog;
import com.paku.mavlinkhub.interfaces.IConnectionFailed;
import com.paku.mavlinkhub.interfaces.IDataUpdateStats;
import com.paku.mavlinkhub.interfaces.IDataUpdateSysLog;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

public class HUBInterfaceManager {

	ArrayList<IConnectionFailed> listenersIConnectionFailed = new ArrayList<IConnectionFailed>();
	ArrayList<IUiModeChanged> listenersIUiModeChanged = new ArrayList<IUiModeChanged>();
	ArrayList<IDataUpdateSysLog> listenersIDataUpdateSysLog = new ArrayList<IDataUpdateSysLog>();
	ArrayList<IDataUpdateByteLog> listenersIDataUpdateByteLog = new ArrayList<IDataUpdateByteLog>();
	ArrayList<IDataUpdateStats> listenersIDataUpdateStats = new ArrayList<IDataUpdateStats>();

	public HUBInterfaceManager() {

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

	// IConnectionFailed
	// *********************************************************
	public void registerForOnConnectionFailed(Fragment fragment) {
		listenersIConnectionFailed.add((IConnectionFailed) fragment);
	}

	public void unregisterFromOnConnectionFailed(Fragment fragment) {
		listenersIConnectionFailed.remove((IConnectionFailed) fragment);
	}

	public void processOnConnectionFailed(String msg) {
		for (IConnectionFailed listener : listenersIConnectionFailed) {
			if (listener != null) listener.onConnectionFailed(msg);
		}
	}
}
