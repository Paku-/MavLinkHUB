package com.paku.mavlinkhub.communication;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BTDevicesListHandler {

	@SuppressWarnings("unused")
	private static final String TAG = "BTDevicesListHandler";

	private BluetoothAdapter mBluetoothAdapter;
	Set<BluetoothDevice> pairedDevices;
	ArrayList<String> bondedDevicesNameList = new ArrayList<String>();

	public BTDevicesListHandler() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
	}

	public int RefreshList() {

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		// check for nulls ...

		if (mBluetoothAdapter == null) {
			return CommunicationHUB.ERROR_NO_ADAPTER;

		} else if (!mBluetoothAdapter.isEnabled()) {
			return CommunicationHUB.ERROR_ADAPTER_OFF;
		}

		bondedDevicesNameList.clear();

		// If there are paired devices
		if (pairedDevices.size() > 0) {

			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// btDevListView
				bondedDevicesNameList.add(device.getName() + "\n"
						+ device.getAddress());
			}

			return CommunicationHUB.LIST_OK;

		} else
			return CommunicationHUB.ERROR_NO_BONDED_DEV;

	}

	public ArrayList<String> GetDeviceList() {
		return bondedDevicesNameList;
	}

}
