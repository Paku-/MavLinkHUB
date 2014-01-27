// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters.devicelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.enums.DEV_LIST_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

public abstract class ListPeerDevices {

	@SuppressWarnings("unused")
	private static final String TAG = ListPeerDevices.class.getSimpleName();

	protected ArrayList<ItemPeerDevice> devList = new ArrayList<ItemPeerDevice>();
	protected final HUBGlobals hub;

	protected ListPeerDevices(HUBGlobals hubContext) {
		hub = hubContext;
	}

	// fill the list with your devices
	public abstract DEV_LIST_STATE refresh();

	public ArrayList<ItemPeerDevice> getDevicesList() {
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
	private static class DevNameComparator implements Comparator<ItemPeerDevice> {
		public int compare(ItemPeerDevice left, ItemPeerDevice right) {
			// if (left.getId() > right.getId()) return 1;
			// if (left.getId() < right.getId()) return -1;
			// return 0;
			return left.getName().compareTo(right.getName());
		}
	}

	protected void sort() {
		Collections.sort(devList, new DevNameComparator());
	}

}
