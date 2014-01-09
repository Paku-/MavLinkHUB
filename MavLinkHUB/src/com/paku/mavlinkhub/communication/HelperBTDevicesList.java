package com.paku.mavlinkhub.communication;

import java.util.ArrayList;
import java.util.Set;

import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.objects.PeerDeviceItem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class HelperBTDevicesList {

	@SuppressWarnings("unused")
	private static final String TAG = "HelperBTDevicesList";

	private BluetoothAdapter mBluetoothAdapter;
	Set<BluetoothDevice> pairedDevices;
	ArrayList<PeerDeviceItem> devicesList = new ArrayList<PeerDeviceItem>();

	public HelperBTDevicesList() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
	}

	public DEV_LIST_STATE RefreshList() {

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		// check for nulls ...

		if (mBluetoothAdapter == null) {
			return DEV_LIST_STATE.ERROR_NO_ADAPTER;

		}
		else if (!mBluetoothAdapter.isEnabled()) {
			return DEV_LIST_STATE.ERROR_ADAPTER_OFF;
		}

		devicesList.clear();

		// If there are paired devices
		if (pairedDevices.size() > 0) {

			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// btDevListView
				devicesList.add(new PeerDeviceItem(device.getName(), device.getAddress()));
			}

			return DEV_LIST_STATE.LIST_OK;

		}
		else
			return DEV_LIST_STATE.ERROR_NO_BONDED_DEV;

	}

	public ArrayList<PeerDeviceItem> GetDeviceList() {
		return devicesList;
	}

	public PeerDeviceItem getItem(int pos) {
		return devicesList.get(pos);

	}

}
