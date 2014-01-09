package com.paku.mavlinkhub.objects;

import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

public class PeerDeviceItem {

	String name;
	String address;
	PEER_DEV_STATE state;

	public PeerDeviceItem(String name, String address) {
		this.name = name;
		this.address = address;
		this.state = PEER_DEV_STATE.DEV_STATE_DISCONNECTED;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

}
