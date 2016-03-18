// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters.devicelist;

import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;

public class ItemPeerDeviceUSB extends ItemPeerDevice {

	private int vendorId;
	private int prodId;
	private int usbInterface;
	private final String serialNumber;
	private int location;

	public ItemPeerDeviceUSB(DEVICE_INTERFACE devInterface, String deviceName, String serialNumber, int vendorId, int prodId, int channel) {
		super(devInterface, deviceName);
		this.vendorId = vendorId;
		this.prodId = prodId;
		usbInterface = channel;
		this.serialNumber = serialNumber;
	}

	public ItemPeerDeviceUSB(DEVICE_INTERFACE devInterface, String description, String serialNumber, int location) {
		super(devInterface, description); // for name
		this.serialNumber = serialNumber;
		this.location = location;
	}

	public String getDescription() {
		return getName();
	}

	public int getLocation() {
		return location;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	@Override
	public String getAddress() {
		if (serialNumber.length() < 1) {
			return Integer.toHexString(vendorId) + ":" + Integer.toHexString(prodId) + ":" + String.valueOf(usbInterface) + "@" + location;
		}
		else {
			return serialNumber + "@" + location;
		}

	}

}
