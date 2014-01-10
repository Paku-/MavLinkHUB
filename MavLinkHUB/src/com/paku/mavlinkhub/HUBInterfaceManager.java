package com.paku.mavlinkhub;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.paku.mavlinkhub.interfaces.IByteLogDataLoggedIn;
import com.paku.mavlinkhub.interfaces.IConnectionFailed;
import com.paku.mavlinkhub.interfaces.ISysLogDataLoggedIn;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

public class HUBInterfaceManager {

	ArrayList<IUiModeChanged> listenersIUiModeChanged = new ArrayList<IUiModeChanged>();
	ArrayList<ISysLogDataLoggedIn> listenersISysLogDataLoggedIn = new ArrayList<ISysLogDataLoggedIn>();
	ArrayList<IByteLogDataLoggedIn> listenersIByteLogDataLoggedIn = new ArrayList<IByteLogDataLoggedIn>();
	ArrayList<IConnectionFailed> listenersIConnectionFailed = new ArrayList<IConnectionFailed>();

	public HUBInterfaceManager() {

	}

	// OnUiModeChanged
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

	// OnSysLogDataLoggedIn
	// *********************************************************
	public void registerForOnSysLogDataLoggedIn(Fragment fragment) {
		listenersISysLogDataLoggedIn.add((ISysLogDataLoggedIn) fragment);
	}

	public void unregisterFromOnSysLogDataLoggedIn(Fragment fragment) {
		listenersISysLogDataLoggedIn.remove((ISysLogDataLoggedIn) fragment);
	}

	public void processOnSysLogDataLoggedIn() {
		for (ISysLogDataLoggedIn listener : listenersISysLogDataLoggedIn) {
			if (listener != null) listener.onSysLogDataLoggedIn();
		}
	}

	// OnByteLogDataLoggedIn
	// *********************************************************
	public void registerForOnByteLogDataLoggedIn(Fragment fragment) {
		listenersIByteLogDataLoggedIn.add((IByteLogDataLoggedIn) fragment);
	}

	public void unregisterFromOnByteLogDataLoggedIn(Fragment fragment) {
		listenersIByteLogDataLoggedIn.remove((IByteLogDataLoggedIn) fragment);
	}

	public void processOnByteLogDataLoggedIn() {
		for (IByteLogDataLoggedIn listener : listenersIByteLogDataLoggedIn) {
			if (listener != null) listener.onByteLogDataLoggedIn();
		}
	}

	// OnConnectionFailed
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
