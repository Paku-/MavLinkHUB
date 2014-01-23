package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBActivityMain;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.devicelist.ListPeerDevicesBluetooth;
import com.paku.mavlinkhub.communication.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.fragments.viewadapters.ViewAdapterPeerDevsList;
import com.paku.mavlinkhub.interfaces.IDroneConnected;
import com.paku.mavlinkhub.interfaces.IDroneConnectionFailed;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class FragmentConnectivity extends HUBFragment implements IUiModeChanged, IDroneConnectionFailed, IDroneConnected {

	private static final String TAG = FragmentConnectivity.class.getSimpleName();

	ListPeerDevicesBluetooth btDevList;
	ListView btDevListView;
	ViewAdapterPeerDevsList devListAdapter;

	View progressBarConnectingBIG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btDevList = new ListPeerDevicesBluetooth(hub);

	}

	@Override
	public void onStart() {
		super.onStart();

		refreshBtDevList();

	}

	@Override
	public void onResume() {
		super.onResume();
		hub.messenger.register(this, APP_STATE.MSG_UI_MODE_CHANGED);
		hub.messenger.register(this, APP_STATE.MSG_DRONE_CONNECTION_FAILED);
		hub.messenger.register(this, APP_STATE.MSG_DRONE_CONNECTED);
		onUiModeChanged();

	}

	@Override
	public void onPause() {
		super.onPause();

		hub.messenger.unregister(this, APP_STATE.MSG_UI_MODE_CHANGED);
		hub.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTION_FAILED);
		hub.messenger.unregister(this, APP_STATE.MSG_DRONE_CONNECTED);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View connView = inflater.inflate(R.layout.fragment_connectivity, container, false);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		btDevListView = (ListView) getView().findViewById(R.id.list_bt_bonded);
		progressBarConnectingBIG = getView().findViewById(R.id.RelativeLayoutProgressBarBig);

		// could be we do not need it here :)
		onUiModeChanged();

	}

	// to be called on possible peer BT device state change (connect disconnect
	// etc)
	private void refreshBtDevListView() {
		ArrayList<ItemPeerDevice> clone = new ArrayList<ItemPeerDevice>();
		clone.addAll(btDevList.getDeviceList());
		devListAdapter.clear();
		devListAdapter.addAll(clone);
	}

	// to be called on start and after re-enabling the BT module
	private void refreshBtDevList() {

		switch (btDevList.refresh()) {
		case ERROR_NO_ADAPTER:
			Toast.makeText(getActivity().getApplicationContext(), R.string.no_bluetooth_adapter_found, Toast.LENGTH_LONG).show();
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

		case LIST_OK:
			devListAdapter = new ViewAdapterPeerDevsList(hub, btDevList.getDeviceList());
			btDevListView.setAdapter(devListAdapter);
			btDevListView.setOnItemClickListener(btListClickListener);
			return;
		default:
			break;

		}

	}

	private final AdapterView.OnItemClickListener btListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			final ItemPeerDevice selectedDev = btDevList.getItem(position);
			// TextView txtView = (TextView)
			// view.findViewById(R.id.listViewItemTxt_dev_name);

			switch (selectedDev.getState()) {
			case DEV_STATE_UNKNOWN:
			case DEV_STATE_DISCONNECTED:
				if (!hub.droneClient.isConnected()) {
					hub.logger.sysLog(TAG, "Connecting...");
					hub.logger.sysLog(TAG, "Me  : " + hub.droneClient.getMyName() + " [" + hub.droneClient.getMyAddress() + "]");
					hub.logger.sysLog(TAG, "Peer: " + selectedDev.getName() + " [" + selectedDev.getAddress() + "]");

					hub.droneClient.startClient(selectedDev.getAddress());

					btDevList.setDevState(position, PEER_DEV_STATE.DEV_STATE_CONNECTED);
				}
				else {
					Log.d(TAG, "Connect on connected device attempt");
					Toast.makeText(getActivity(), R.string.disconnect_first, Toast.LENGTH_SHORT).show();
				}

				break;

			case DEV_STATE_CONNECTED:
				if (hub.droneClient.isConnected()) {
					hub.logger.sysLog(TAG, "Closing Connection ...");
					hub.droneClient.stopClient();
					btDevList.setDevState(position, PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
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

	// interfaces
	@Override
	public void onDroneConnectionFailed(String errorMsg) {

		hub.logger.sysLog(TAG, errorMsg);
		hub.droneClient.stopClient();

		Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();

		btDevList.setAllDevState(PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
		refreshBtDevListView();

	}

	@Override
	public void onDroneConnected() {

	}

	@Override
	public void onUiModeChanged() {

		// mostly used states set as defaults
		((HUBActivityMain) getActivity()).enableProgressBar(false);
		progressBarConnectingBIG.setVisibility(View.INVISIBLE);
		btDevListView.setVisibility(View.VISIBLE);

		switch (hub.uiMode) {
		case UI_MODE_CREATED:
			break;
		case UI_MODE_TURNING_ON:
			progressBarConnectingBIG.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_ON:
			refreshBtDevList();
			break;
		case UI_MODE_TURNING_OFF:
			progressBarConnectingBIG.setVisibility(View.VISIBLE);
			btDevListView.setVisibility(View.INVISIBLE);
			break;
		case UI_MODE_STATE_OFF:
			btDevListView.setVisibility(View.INVISIBLE);
			break;
		case UI_MODE_CONNECTED:
			((HUBActivityMain) getActivity()).enableProgressBar(true);
			refreshBtDevListView();
			break;
		case UI_MODE_DISCONNECTED:
			refreshBtDevListView();
			break;
		default:
			break;
		}

	}

}