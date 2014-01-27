// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters.devicelist.hardware;

import java.util.Set;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDeviceBT;
import com.paku.mavlinkhub.viewadapters.devicelist.ListPeerDevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class ListPeerDevicesBluetooth extends ListPeerDevices {

	@SuppressWarnings("unused")
	private static final String TAG = ListPeerDevicesBluetooth.class.getSimpleName();

	public ListPeerDevicesBluetooth(HUBGlobals hubContext) {
		super(hubContext);
	}

	public DEV_LIST_STATE refresh() {

		// check for nulls ...
		if (null == BluetoothAdapter.getDefaultAdapter()) {
			return DEV_LIST_STATE.ERROR_NO_ADAPTER;
		}
		else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			return DEV_LIST_STATE.ERROR_ADAPTER_OFF;
		}

		// get local adapter and paired dev list
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		final Set<BluetoothDevice> pairedDevList = mBluetoothAdapter.getBondedDevices();

		devList.clear();
		// If there are paired devices
		if (pairedDevList.size() > 0) {
			for (BluetoothDevice device : pairedDevList) {
				ItemPeerDevice tmpItemPeerDevice = new ItemPeerDeviceBT(DEVICE_INTERFACE.Bluetooth, device.getName(), device.getAddress());
				//check if connected !!!
				if (hub.droneClient.isConnected() & (hub.droneClient.getPeerAddress().equals(device.getAddress()))) {
					tmpItemPeerDevice.setState(PEER_DEV_STATE.DEV_STATE_CONNECTED);
				}
				devList.add(tmpItemPeerDevice);
			}
			sort();
			return DEV_LIST_STATE.LIST_OK_BT;
		}
		else {
			return DEV_LIST_STATE.ERROR_NO_BONDED_DEV;
		}
	}

}
