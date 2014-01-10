package com.paku.mavlinkhub.communication.devicelist;

import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

public class ItemPeerDevice {

	String name;
	String address;
	PEER_DEV_STATE state;

	public ItemPeerDevice(String name, String address) {
		this.name = name;
		this.address = address;
		this.state = PEER_DEV_STATE.DEV_STATE_UNKNOWN;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public PEER_DEV_STATE getState() {
		return state;
	}

	public void setState(PEER_DEV_STATE state) {
		this.state = state;
	}

}
