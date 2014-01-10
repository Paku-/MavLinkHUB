package com.paku.mavlinkhub.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.objects.ItemPeerDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class HelperBTDevicesList {

	@SuppressWarnings("unused")
	private static final String TAG = "HelperBTDevicesList";

	ArrayList<ItemPeerDevice> devList = new ArrayList<ItemPeerDevice>();

	public HelperBTDevicesList() {
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
			Collections.sort(devList, new DevNameComparator());
			return DEV_LIST_STATE.LIST_OK;
		}
		else
			return DEV_LIST_STATE.ERROR_NO_BONDED_DEV;
	}

	public ArrayList<ItemPeerDevice> getDeviceList() {
		return devList;
	}

	public ItemPeerDevice getItem(int pos) {
		return devList.get(pos);

	}

	public void setDevState(int pos, PEER_DEV_STATE state) {
		devList.get(pos).setState(state);
	}

	// sorting comparator
	private class DevNameComparator implements Comparator<ItemPeerDevice> {
		public int compare(ItemPeerDevice left, ItemPeerDevice right) {
			// if (left.getId() > right.getId()) return 1;
			// if (left.getId() < right.getId()) return -1;
			// return 0;
			return left.getName().compareTo(right.getName());
		}
	}

}
