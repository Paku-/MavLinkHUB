package com.paku.mavlinkhub.viewadapters.devicelist;

import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

public abstract class ItemPeerDevice {

	String name;
	DEVICE_INTERFACE devInterface;
	PEER_DEV_STATE state;

	public ItemPeerDevice(DEVICE_INTERFACE devInterface, String deviceName) {
		this.name = deviceName;
		this.state = PEER_DEV_STATE.DEV_STATE_DISCONNECTED;
	}

	public String getName() {
		return name;
	}

	public PEER_DEV_STATE getState() {
		return state;
	}

	public void setState(PEER_DEV_STATE state) {
		this.state = state;
	}

	public DEVICE_INTERFACE getDevInterface() {
		return devInterface;
	}

	abstract public String getAddress();

}
