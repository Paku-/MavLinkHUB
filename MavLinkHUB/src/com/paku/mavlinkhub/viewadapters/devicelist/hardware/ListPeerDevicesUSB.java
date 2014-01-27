// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters.devicelist.hardware;

import java.util.HashMap;
import java.util.Iterator;
import com.ftdi.j2xx.D2xxManager;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDeviceUSB;
import com.paku.mavlinkhub.viewadapters.devicelist.ListPeerDevices;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class ListPeerDevicesUSB extends ListPeerDevices {

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

	// ============= NOT USED NOW ===============================

	public DEV_LIST_STATE refresh_FramewaorkBasedNotUsed() {

		devList.clear();

		UsbManager manager = (UsbManager) hub.getSystemService(Context.USB_SERVICE);

		// UsbAccessory accessory = (UsbAccessory)
		// intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
		// UsbAccessory[] accessoryList = manager.getAccessoryList();

		HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();

			if (HUBGlobals.usbHub.isFtDevice(device)) {
				Log.d(TAG, "FTDI DEVICE:" + device.getVendorId() + ":" + device.getProductId() + "/" + device.getDeviceClass() + ":" + device.getDeviceSubclass());

				ItemPeerDevice tmpItemPeerDevice = new ItemPeerDeviceUSB(DEVICE_INTERFACE.USB, device.getDeviceName(), "", device.getVendorId(), device.getProductId(), device.getInterface(0).getId());
				devList.add(tmpItemPeerDevice);

				Log.d(TAG, "DEVICE:" + device.getVendorId() + ":" + device.getProductId() + "/" + device.getDeviceClass() + ":" + device.getDeviceSubclass());

				for (int i = 0; i < device.getInterfaceCount(); i++) {
					Log.d(TAG, "Interface: " + device.getInterface(i).toString());
					for (int x = 0; x < device.getInterface(i).getEndpointCount(); x++) {
						Log.d(TAG, "Endpoint:" + device.getInterface(i).getEndpoint(x).toString());
					}
				}
			}

		}

		sort();

		if (devList.size() > 0) {
			return DEV_LIST_STATE.LIST_OK_USB;
		}
		else {
			return DEV_LIST_STATE.ERROR_NO_USB_DEVICES;
		}

	}

}
