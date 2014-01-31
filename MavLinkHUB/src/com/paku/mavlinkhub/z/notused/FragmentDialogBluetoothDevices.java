package com.paku.mavlinkhub.z.notused;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.fragments.FragmentConnection;
import com.paku.mavlinkhub.viewadapters.ViewAdapterPeerDevsList;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.viewadapters.devicelist.hardware.ListPeerDevicesBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentDialogBluetoothDevices extends DialogFragment {

	private static final String TAG = FragmentConnection.class.getSimpleName();

	HUBGlobals hub;

	ListPeerDevicesBluetooth listBTDevices;
	ListView listViewBTDevices;
	ViewAdapterPeerDevsList devListAdapter;

	public static FragmentDialogBluetoothDevices newInstance() {

		final FragmentDialogBluetoothDevices me = new FragmentDialogBluetoothDevices();

		return me;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hub = ((HUBGlobals) getActivity().getApplication());

		listBTDevices = new ListPeerDevicesBluetooth(hub);

		final int style = DialogFragment.STYLE_NORMAL;
		final int theme = android.R.style.Theme_Holo_Light_Dialog;

		setStyle(style, theme);
		setCancelable(true);

		/*
		 * style = DialogFragment.STYLE_NO_TITLE; style =
		 * DialogFragment.STYLE_NO_INPUT; style = DialogFragment.STYLE_NORMAL;
		 * style = DialogFragment.STYLE_NO_FRAME; theme =
		 * android.R.style.Theme_Holo; theme =
		 * android.R.style.Theme_Holo_Light_Dialog; theme =
		 * android.R.style.Theme_Holo_Light; theme =
		 * android.R.style.Theme_Holo_Light_Panel;
		 */

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().setTitle(R.string.dlg_devices_found);

		final View viewDlg = inflater.inflate(R.layout.fragment_dialog_select_peer_device, container, false);

		return viewDlg;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listViewBTDevices = (ListView) getView().findViewById(R.id.listView_select_peer_device);

		refreshBtDevList();

	}

	@SuppressWarnings("unused")
	private void refreshBtDevListView() {
		final ArrayList<ItemPeerDevice> clone = new ArrayList<ItemPeerDevice>();
		clone.addAll(listBTDevices.getDevicesList());
		devListAdapter.clear();
		devListAdapter.addAll(clone);
	}

	private void refreshBtDevList() {

		// call list to be refreshed getting BT status
		switch (listBTDevices.refresh()) {
		case ERROR_NO_ADAPTER:
			Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_bluetooth_adapter_found, Toast.LENGTH_LONG).show();
			return;
		case ERROR_ADAPTER_OFF:
			final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// this.startActivityForResult(enableBtIntent,
			// APP_STATE.REQUEST_ENABLE_BT);
			this.startActivity(enableBtIntent);
			return;
		case ERROR_NO_BONDED_DEV:
			Toast.makeText(getActivity().getApplicationContext(), R.string.error_no_paired_bt_devices_found_pair_device_first, Toast.LENGTH_LONG).show();
			return;

			// our list has fresh BT devs items and adapter is ready, we can
			// connect
		case LIST_OK_BT:
			devListAdapter = new ViewAdapterPeerDevsList(hub, listBTDevices.getDevicesList());
			listViewBTDevices.setAdapter(devListAdapter);
			listViewBTDevices.setOnItemClickListener(listViewBTClickListener);
			return;
		default:
			break;

		}

	}

	/*
	 * have to cover this as well
	 * @Override public void onDroneConnectionFailed() {
	 * btDevList.setAllDevState(PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
	 * refreshBtDevListView(); }
	 */

	private final AdapterView.OnItemClickListener listViewBTClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			final ItemPeerDevice selectedDev = listBTDevices.getItem(position);

			switch (selectedDev.getState()) {
			case DEV_STATE_UNKNOWN:
			case DEV_STATE_DISCONNECTED:
				if (!hub.droneClient.isConnected()) {
					HUBGlobals.logger.sysLog(TAG, "Connecting...");
					HUBGlobals.logger.sysLog(TAG, "Me  : " + hub.droneClient.getMyName() + " [" + hub.droneClient.getMyAddress() + "]");
					HUBGlobals.logger.sysLog(TAG, "Peer: " + selectedDev.getName() + " [" + selectedDev.getAddress() + "]");

					hub.droneClient.startClient(selectedDev);

					listBTDevices.setDevState(position, PEER_DEV_STATE.DEV_STATE_CONNECTED);
					Toast.makeText(getActivity(), R.string.txt_device_connecting, Toast.LENGTH_SHORT).show();
					dismiss();
				}
				else {
					Log.d(TAG, "Device already connected");
					Toast.makeText(getActivity(), R.string.error_disconnect_first, Toast.LENGTH_SHORT).show();
				}

				break;

			case DEV_STATE_CONNECTED:
				if (hub.droneClient.isConnected()) {
					HUBGlobals.logger.sysLog(TAG, "Closing Connection ...");
					hub.droneClient.stopClient();
					listBTDevices.setDevState(position, PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
					Toast.makeText(getActivity(), R.string.txt_device_disconnected, Toast.LENGTH_SHORT).show();
					dismiss();
				}
				else {
					Log.d(TAG, "Already disconnected ...");
				}

				break;

			default:
				break;
			}

		}
	};

}