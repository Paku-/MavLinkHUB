package com.paku.mavlinkhub.communication;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class BTDevices {

	// private static final String TAG = "BTDevices";

	private BluetoothAdapter mBluetoothAdapter;
	Set<BluetoothDevice> pairedDevices;
	ArrayList<String> bondedDevicesNameList = new ArrayList<String>();

	private static final int LIST_OK = 1;
	private static final int ERROR_NO_ADAPTER = 2;
	private static final int ERROR_ADAPTER_OFF = 3;
	private static final int ERROR_NO_BONDED_DEV = 4;

	public BTDevices() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
	}

	public int RefreshList() {

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		// check for nulls ...

		if (mBluetoothAdapter == null) {
			return ERROR_NO_ADAPTER;

		} else if (!mBluetoothAdapter.isEnabled()) {
			return ERROR_ADAPTER_OFF;
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

			return LIST_OK;

		} else
			return ERROR_NO_BONDED_DEV;

	}

	public ArrayList<String> GetDeviceList() {
		return bondedDevicesNameList;
	}

}
