// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters.devicelist;

import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;

public class ItemPeerDeviceBT extends ItemPeerDevice {

	String address;

	public ItemPeerDeviceBT(DEVICE_INTERFACE devInterface, String deviceName, String address) {
		super(devInterface, deviceName);
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

}
