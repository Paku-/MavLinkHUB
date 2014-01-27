package com.paku.mavlinkhub.queue.endpoints;

import java.io.IOException;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.queue.ConnectorBytes;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;

public abstract class DroneClient extends ConnectorBytes {

	@SuppressWarnings("unused")
	private static final String TAG = DroneClient.class.getSimpleName();

	// application handler used to report connection states

	private ItemPeerDevice myPeerDevice;

	public abstract void startClient(ItemPeerDevice drone);

	public abstract void stopClient();

	public abstract boolean isConnected();

	public abstract String getMyName();

	public abstract String getMyAddress();

	public abstract String getPeerName();

	public abstract String getPeerAddress();

	public abstract boolean writeBytes(byte[] bytes) throws IOException;

	protected DroneClient(HUBGlobals hub, int capacity) {
		super(hub, capacity);
	}

	public final ItemPeerDevice getMyPeerDevice() {
		return myPeerDevice;
	}

	public final void setMyPeerDevice(ItemPeerDevice myPeerDevice) {
		this.myPeerDevice = myPeerDevice;
	}

}
