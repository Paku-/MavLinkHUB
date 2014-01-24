package com.paku.mavlinkhub.fragments.dialogs;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.DEVICE_INTERFACE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.fragments.FragmentConnectionState;
import com.paku.mavlinkhub.viewadapters.ViewAdapterPeerDevsList;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.viewadapters.devicelist.ListPeerDevices;
import com.paku.mavlinkhub.viewadapters.devicelist.interfaces.ListPeerDevicesBluetooth;
import com.paku.mavlinkhub.viewadapters.devicelist.interfaces.ListPeerDevicesUSB;

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

public class FragmentDialogSelectDevice extends DialogFragment {

	private static final String TAG = FragmentConnectionState.class.getSimpleName();

	HUBGlobals hub;

	ListPeerDevices listDevices;
	ListView listViewDevices;
	ViewAdapterPeerDevsList devListAdapter;

	DEVICE_INTERFACE devType = DEVICE_INTERFACE.USB;

	public static FragmentDialogSelectDevice newInstance() {

		FragmentDialogSelectDevice me = new FragmentDialogSelectDevice();

		return me;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		hub = ((HUBGlobals) getActivity().getApplication());

		// / USB or BT ???
		listDevices = new ListPeerDevicesUSB(hub);

		int style = DialogFragment.STYLE_NORMAL;
		int theme = android.R.style.Theme_Holo_Light_Dialog;

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

		View viewDlg = inflater.inflate(R.layout.fragment_dialog_select_peer_device, container, false);

		return viewDlg;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listViewDevices = (ListView) getView().findViewById(R.id.listView_select_peer_device);

		refreshDeviceSelectionList();

	}

	private void refreshDeviceSelectionList() {

		// call list to be refreshed getting BT status
		getDialog().setTitle(R.string.txt_no_data);

		switch (listDevices.refresh()) {
		case ERROR_NO_ADAPTER:
			getDialog().setTitle(R.string.error_no_bluetooth_adapter_found);
			return;
		case ERROR_ADAPTER_OFF:
			final Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// this.startActivityForResult(enableBtIntent,
			// APP_STATE.REQUEST_ENABLE_BT);
			this.startActivity(enableBtIntent);
			return;
		case ERROR_NO_BONDED_DEV:
			getDialog().setTitle(R.string.error_no_paired_bt_devices_found_pair_device_first);
			return;
		case ERROR_NO_USB_DEVICES:
			getDialog().setTitle(R.string.error_no_usb_devices_found);
			return;
		case LIST_OK_BT:
		case LIST_OK_USB:

			getDialog().setTitle(R.string.dlg_select_peer_device_title);

			devListAdapter = new ViewAdapterPeerDevsList(hub, listDevices.getDeviceList());
			listViewDevices.setAdapter(devListAdapter);

			if (devType == DEVICE_INTERFACE.Bluetooth) {
				listViewDevices.setOnItemClickListener(listViewBTClickListener);
			}

			if (devType == DEVICE_INTERFACE.USB) {
				// listViewDevices.setOnItemClickListener(listViewBTClickListener);
			}

			return;
		default:
			break;

		}
		return;
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

			final ItemPeerDevice selectedDev = listDevices.getItem(position);

			switch (selectedDev.getState()) {
			case DEV_STATE_UNKNOWN:
			case DEV_STATE_DISCONNECTED:
				if (!hub.droneClient.isConnected()) {
					hub.logger.sysLog(TAG, "Connecting...");
					hub.logger.sysLog(TAG, "Me  : " + hub.droneClient.getMyName() + " [" + hub.droneClient.getMyAddress() + "]");
					hub.logger.sysLog(TAG, "Peer: " + selectedDev.getName() + " [" + selectedDev.getAddress() + "]");

					hub.droneClient.startClient(selectedDev);

					listDevices.setDevState(position, PEER_DEV_STATE.DEV_STATE_CONNECTED);
					Toast.makeText(getActivity(), R.string.txt_device_connecting, Toast.LENGTH_SHORT).show();
					dismiss();
				}
				else {
					Log.d(TAG, "Connect on connected device attempt");
					Toast.makeText(getActivity(), R.string.error_disconnect_first, Toast.LENGTH_SHORT).show();
				}

				break;

			case DEV_STATE_CONNECTED:
				if (hub.droneClient.isConnected()) {
					hub.logger.sysLog(TAG, "Closing Connection ...");
					hub.droneClient.stopClient();
					listDevices.setDevState(position, PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
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