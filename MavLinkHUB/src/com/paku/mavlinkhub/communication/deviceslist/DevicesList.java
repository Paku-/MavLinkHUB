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

public class DevicesList {

	@SuppressWarnings("unused")
	private static final String TAG = "DevicesList";

	ArrayList<ItemPeerDevice> devList = new ArrayList<ItemPeerDevice>();

	public DevicesList() {
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

	public void setAllDevState(PEER_DEV_STATE state) {
		for (ItemPeerDevice dev : devList) {
			dev.setState(state);
		}
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

	public void sort() {
		Collections.sort(devList, new DevNameComparator());
	}

}
