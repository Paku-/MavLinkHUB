package com.paku.mavlinkhub.communication.deviceslist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.objects.ItemPeerDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class DevicesListBluetooth extends DevicesList {

	@SuppressWarnings("unused")
	private static final String TAG = "DevicesListBluetooth";

	public DevicesListBluetooth() {
		super();
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
				devList.add(new ItemPeerDevice(device.getName(), device.getAddress()));
			}
			sort();
			return DEV_LIST_STATE.LIST_OK;
		}
		else
			return DEV_LIST_STATE.ERROR_NO_BONDED_DEV;
	}

}
