package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.devicelist.ListPeerDevicesBluetooth;
import com.paku.mavlinkhub.communication.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.fragments.viewadapter.ViewAdapterPeerDevsList;
import com.paku.mavlinkhub.interfaces.IConnectionFailed;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FragmentConnectivity extends Fragment implements IUiModeChanged, IConnectionFailed {

	private static final String TAG = "FragmentConnectivity";

	ListPeerDevicesBluetooth btDevList = new ListPeerDevicesBluetooth();
	ListView btDevListView;
	ViewAdapterPeerDevsList devListAdapter;

	ProgressBar connProgressBar;
	HUBGlobals globalVars;

	public FragmentConnectivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		globalVars = (HUBGlobals) getActivity().getApplication();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		refreshBtDevList();

	}

	@Override
	public void onResume() {
		super.onResume();
		globalVars.messanger.registerForOnUiModeChanged(this);
		globalVars.messanger.registerForOnConnectionFailed(this);
		refreshUI();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View connView = inflater.inflate(R.layout.fragment_connectivity, container, false);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		btDevListView = (ListView) getView().findViewById(R.id.list_bt_bonded);
		connProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);

		refreshUI();

	}

	public void refreshUI() {

		switch (globalVars.uiMode) {
		case UI_MODE_CREATED:
			connProgressBar.setVisibility(View.INVISIBLE);
			break;

		case UI_MODE_TURNING_ON:
			connProgressBar.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_ON:
			connProgressBar.setVisibility(View.INVISIBLE);
			btDevListView.setVisibility(View.VISIBLE);
			refreshBtDevList();
			break;
		case UI_MODE_TURNING_OFF:
			connProgressBar.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_OFF:
			connProgressBar.setVisibility(View.INVISIBLE);
			btDevListView.setVisibility(View.INVISIBLE);
			break;
		case UI_MODE_CONNECTED:
			connProgressBar.setVisibility(View.INVISIBLE);
			refreshBtDevListView();
			break;
		case UI_MODE_DISCONNECTED:
			connProgressBar.setVisibility(View.INVISIBLE);
			refreshBtDevListView();
			break;
		default:
			break;
		}

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
			Toast.makeText(getActivity().getApplicationContext(), R.string.no_bluetooth_adapter_found,
					Toast.LENGTH_LONG).show();
			return;
		case ERROR_ADAPTER_OFF:
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBtIntent, HUBGlobals.REQUEST_ENABLE_BT);
			return;
		case ERROR_NO_BONDED_DEV:
			Toast.makeText(getActivity().getApplicationContext(),
					R.string.error_no_paired_bt_devices_found_pair_device_first, Toast.LENGTH_LONG).show();
			return;

		case LIST_OK:
			devListAdapter = new ViewAdapterPeerDevsList(this.getActivity(), btDevList.getDeviceList());
			btDevListView.setAdapter(devListAdapter);
			btDevListView.setOnItemClickListener(btListClickListener);

			return;

		}

	}

	private final AdapterView.OnItemClickListener btListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			ItemPeerDevice selectedDev = btDevList.getItem(position);
			// TextView txtView = (TextView)
			// view.findViewById(R.id.listViewItemTxt_dev_name);

			switch (selectedDev.getState()) {
			case DEV_STATE_UNKNOWN:
			case DEV_STATE_DISCONNECTED:

				globalVars.logger.sysLog(TAG, "Connecting...");
				globalVars.logger.sysLog(TAG, "Me  : " + globalVars.connectorBluetooth.getBluetoothAdapter().getName()
						+ " [" + globalVars.connectorBluetooth.getBluetoothAdapter().getAddress() + "]");
				globalVars.logger.sysLog(TAG, "Peer: " + selectedDev.getName() + " [" + selectedDev.getAddress() + "]");

				globalVars.connectorBluetooth.openConnection(selectedDev.getAddress());
				globalVars.mMavLinkCollector.startMavLinkParserThread();

				btDevList.setDevState(position, PEER_DEV_STATE.DEV_STATE_CONNECTED);

				break;

			case DEV_STATE_CONNECTED:

				globalVars.logger.sysLog(TAG, "Closing Connection ...");
				globalVars.connectorBluetooth.closeConnection();
				globalVars.mMavLinkCollector.stopMavLinkParserThread();

				btDevList.setDevState(position, PEER_DEV_STATE.DEV_STATE_DISCONNECTED);

				break;

			default:
				break;
			}
			;

		}
	};

	// interfaces
	@Override
	public void onConnectionFailed(String errorMsg) {
		Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
		globalVars.logger.sysLog(TAG, errorMsg);
	}

	@Override
	public void onUiModeChanged() {
		refreshUI();
	}

}