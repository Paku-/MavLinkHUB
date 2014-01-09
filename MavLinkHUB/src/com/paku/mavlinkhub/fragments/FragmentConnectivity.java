package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.HelperBTDevicesList;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;
import com.paku.mavlinkhub.objects.PeerDeviceItem;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FragmentConnectivity extends Fragment implements IUiModeChanged {

	private static final String TAG = "FragmentConnectivity";

	HelperBTDevicesList btDevList = new HelperBTDevicesList();
	ListView btDevListView;
	ViewAdapterPeerDevsList devListAdapter;

	Button disconnectButton;
	ProgressBar connProgressBar;
	AppGlobals globalVars;

	public FragmentConnectivity() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		globalVars = (AppGlobals) getActivity().getApplication();
		globalVars.registerForIUiModeChanged(this);

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

		RefreshBtDevList();

	}

	@Override
	public void onResume() {
		super.onResume();

		globalVars.registerForIUiModeChanged(this);
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
		disconnectButton = (Button) getView().findViewById(R.id.button_disconnect);
		connProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);

		disconnectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				globalVars.logger.sysLog(TAG, "Closing Connection ...");
				globalVars.mBtConnector.closeConnection();
				globalVars.mMavLinkCollector.stopMavLinkParserThread();

			}
		});

		refreshUI();

	}

	private final AdapterView.OnItemClickListener btListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			PeerDeviceItem selectedDev = btDevList.getItem(position);

			globalVars.logger.sysLog(TAG, "Connecting...");
			globalVars.logger.sysLog(TAG, "Me  : " + globalVars.mBtConnector.getBluetoothAdapter().getName() + " ["
					+ globalVars.mBtConnector.getBluetoothAdapter().getAddress() + "]");
			globalVars.logger.sysLog(TAG, "Peer: " + selectedDev.getName() + " [" + selectedDev.getAddress() + "]");

			globalVars.mBtConnector.openConnection(selectedDev.getAddress());
			globalVars.mMavLinkCollector.startMavLinkParserThread();

		}
	};

	public void refreshUI() {

		switch (globalVars.getUiMode()) {
		case UI_MODE_CREATED:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			break;

		case UI_MODE_TURNING_ON:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_ON:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			btDevListView.setVisibility(View.VISIBLE);
			RefreshBtDevList();
			break;
		case UI_MODE_TURNING_OFF:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.VISIBLE);
			break;
		case UI_MODE_STATE_OFF:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			btDevListView.setVisibility(View.INVISIBLE);
			break;
		case UI_MODE_CONNECTED:
			disconnectButton.setVisibility(View.VISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			break;
		case UI_MODE_DISCONNECTED:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}

	}

	private void RefreshBtDevList() {

		switch (btDevList.RefreshList()) {
		case ERROR_NO_ADAPTER:
			Toast.makeText(getActivity().getApplicationContext(), "No Bluetooth Adapter found.", Toast.LENGTH_LONG)
					.show();
			return;
		case ERROR_ADAPTER_OFF:
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBtIntent, AppGlobals.REQUEST_ENABLE_BT);
			return;
		case ERROR_NO_BONDED_DEV:
			Toast.makeText(getActivity().getApplicationContext(),
					R.string.error_no_paired_bt_devices_found_pair_device_first, Toast.LENGTH_LONG).show();
			return;

		case LIST_OK:

			devListAdapter = new ViewAdapterPeerDevsList(this.getActivity(), btDevList.GetDeviceList());
			btDevListView.setAdapter(devListAdapter);
			btDevListView.setOnItemClickListener(btListClickListener);

			return;

		}

	}

	@Override
	public void onUiModeChanged() {
		refreshUI();
	}

}