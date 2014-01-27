// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters.devicelist.hardware;

import com.ftdi.j2xx.D2xxManager;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDeviceUSB;
import com.paku.mavlinkhub.viewadapters.devicelist.ListPeerDevices;

public class ListPeerDevicesUSB extends ListPeerDevices {

	@SuppressWarnings("unused")
	private static final String TAG = ListPeerDevicesUSB.class.getSimpleName();

	public ListPeerDevicesUSB(HUBGlobals hubContext) {
		super(hubContext);

	}

	public DEV_LIST_STATE refresh() {

		devList.clear();

		int devCount = HUBGlobals.usbHub.createDeviceInfoList(hub);

		if (devCount > 0) {
			D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
			HUBGlobals.usbHub.getDeviceInfoList(devCount, deviceList);

			for (int i = 0; i < devCount; i++) {
				ItemPeerDevice tmpItemPeerDevice = new ItemPeerDeviceUSB(DEVICE_INTERFACE.USB, deviceList[i].description, deviceList[i].serialNumber, deviceList[i].location);

				//check if connected !!!				
				if (hub.droneClient.isConnected() & (hub.droneClient.getPeerAddress().equals(tmpItemPeerDevice.getAddress()))) {
					tmpItemPeerDevice.setState(PEER_DEV_STATE.DEV_STATE_CONNECTED);
				}

				devList.add(tmpItemPeerDevice);
			}

		}

		sort();

		if (devList.size() > 0) {
			return DEV_LIST_STATE.LIST_OK_USB;
		}
		else {
			return DEV_LIST_STATE.ERROR_NO_USB_DEVICES;
		}

		// return DEV_LIST_STATE.ERROR_NO_ADAPTER;
		// return DEV_LIST_STATE.ERROR_ADAPTER_OFF;
		// return DEV_LIST_STATE.ERROR_NO_BONDED_DEV;

	}

}
