package com.paku.mavlinkhub.communication.devicelist;

import java.util.Set;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class ListPeerDevicesBluetooth extends ListPeerDevices {

	@SuppressWarnings("unused")
	private static final String TAG = "ListPeerDevicesBluetooth";

	public ListPeerDevicesBluetooth(HUBGlobals hubGlobals) {
		super(hubGlobals);
	}

	public DEV_LIST_STATE refresh() {

		// check for nulls ...
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			return DEV_LIST_STATE.ERROR_NO_ADAPTER;
		}
		else if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			return DEV_LIST_STATE.ERROR_ADAPTER_OFF;
		}

		// get local adapter and paired dev list
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevList = mBluetoothAdapter.getBondedDevices();

		devList.clear();
		// If there are paired devices
		if (pairedDevList.size() > 0) {
			for (BluetoothDevice device : pairedDevList) {
				ItemPeerDevice tmpItemPeerDevice = new ItemPeerDevice(device.getName(), device.getAddress());
				if (globalVars.incommingConnector.isConnected()
						& (globalVars.incommingConnector.getPeerAddress().equals(device.getAddress()))) {
					tmpItemPeerDevice.setState(PEER_DEV_STATE.DEV_STATE_CONNECTED);
				}
				devList.add(tmpItemPeerDevice);
			}
			sort();
			return DEV_LIST_STATE.LIST_OK;
		}
		else
			return DEV_LIST_STATE.ERROR_NO_BONDED_DEV;
	}

}
