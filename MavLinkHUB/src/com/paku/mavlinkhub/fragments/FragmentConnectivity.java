package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.ActivityMain;
import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.devicelist.ListPeerDevicesBluetooth;
import com.paku.mavlinkhub.communication.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.fragments.viewadapters.ViewAdapterPeerDevsList;
import com.paku.mavlinkhub.interfaces.IConnectionFailed;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FragmentConnectivity extends HUBFragment implements IUiModeChanged, IConnectionFailed {

	private static final String TAG = "FragmentConnectivity";

	ListPeerDevicesBluetooth btDevList;
	ListView btDevListView;
	ViewAdapterPeerDevsList devListAdapter;

	View progressBarConnectingBIG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btDevList = new ListPeerDevicesBluetooth(globalVars);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onStart() {
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
		progressBarConnectingBIG = (View) getView().findViewById(R.id.RelativeLayoutProgressBarBig);

		refreshUI();

	}

	public void refreshUI() {

		// mostly used states
		((ActivityMain) getActivity()).enableProgressBar(false);
		progressBarConnectingBIG.setVisibility(View.INVISIBLE);
		btDevListView.setVisibility(View.VISIBLE);

		switch (globalVars.uiMode) {
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
			((ActivityMain) getActivity()).enableProgressBar(true);
			refreshBtDevListView();
			break;
		case UI_MODE_DISCONNECTED:
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
				if (!globalVars.incommingConnector.isConnected()) {
					globalVars.logger.sysLog(TAG, "Connecting...");
					globalVars.logger.sysLog(TAG, "Me  : " + globalVars.incommingConnector.getPeerName() + " ["
							+ globalVars.incommingConnector.getPeerAddress() + "]");
					globalVars.logger.sysLog(TAG, "Peer: " + selectedDev.getName() + " [" + selectedDev.getAddress()
							+ "]");

					globalVars.incommingConnector.openConnection(selectedDev.getAddress());
					globalVars.mMavLinkCollector.startMavLinkParserThread();

					btDevList.setDevState(position, PEER_DEV_STATE.DEV_STATE_CONNECTED);
				}
				else {
					Log.d(TAG, "Connect on connected device attempt");
					Toast.makeText(getActivity(), R.string.disconnect_first, Toast.LENGTH_SHORT).show();
				}

				break;

			case DEV_STATE_CONNECTED:
				if (globalVars.incommingConnector.isConnected()) {
					globalVars.logger.sysLog(TAG, "Closing Connection ...");
					globalVars.incommingConnector.closeConnection();
					globalVars.mMavLinkCollector.stopMavLinkParserThread();
					btDevList.setDevState(position, PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
				}
				else {
					Log.d(TAG, "Already disconnected ...");
				}

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
		Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
		globalVars.logger.sysLog(TAG, errorMsg);

		btDevList.setAllDevState(PEER_DEV_STATE.DEV_STATE_DISCONNECTED);
		refreshBtDevListView();
	}

	@Override
	public void onUiModeChanged() {
		refreshUI();
	}

}