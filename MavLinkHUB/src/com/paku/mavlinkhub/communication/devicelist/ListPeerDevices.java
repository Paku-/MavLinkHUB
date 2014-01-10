package com.paku.mavlinkhub.communication.devicelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

public abstract class ListPeerDevices {

	@SuppressWarnings("unused")
	private static final String TAG = "ListPeerDevices";

	ArrayList<ItemPeerDevice> devList = new ArrayList<ItemPeerDevice>();

	public ListPeerDevices() {
	}

	// fill the list with your devices
	abstract DEV_LIST_STATE refresh();

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
