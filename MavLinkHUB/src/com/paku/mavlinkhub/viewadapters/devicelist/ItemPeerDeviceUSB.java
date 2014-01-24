package com.paku.mavlinkhub.viewadapters.devicelist;

import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

public class ItemPeerDeviceUSB extends ItemPeerDevice {

	int vendorId;
	int prodId;
	int usbInterface;
	String serialNumber;

	public ItemPeerDeviceUSB(DEVICE_INTERFACE devInterface, String deviceName, String serialNumber, int vendorId, int prodId, int channel) {
		super(devInterface, deviceName);
		this.vendorId = vendorId;
		this.prodId = prodId;
		this.usbInterface = channel;
		this.serialNumber = serialNumber;
	}

	@Override
	public String getAddress() {
		// return Integer.toHexString(vendorId) + ":" +
		// Integer.toHexString(prodId) + ":" + String.valueOf(usbInterface);
		return serialNumber;
	}

}
