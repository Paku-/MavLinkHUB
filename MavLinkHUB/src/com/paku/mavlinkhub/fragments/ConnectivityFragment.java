package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.BTDevicesListHandler;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IUiModeChanged;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectivityFragment extends Fragment implements IUiModeChanged {

	@SuppressWarnings("unused")
	private static final String TAG = "ConnectivityFragment";

	BTDevicesListHandler btDevList = new BTDevicesListHandler();

	ListView btDevListView;
	Button disconnectButton;
	ProgressBar connProgressBar;
	AppGlobals globalVars;

	public ConnectivityFragment() {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View connView = inflater.inflate(R.layout.fragment_connectivity,
				container, false);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		btDevListView = (ListView) getView().findViewById(R.id.list_bt_bonded);
		disconnectButton = (Button) getView().findViewById(
				R.id.button_disconnect);
		connProgressBar = (ProgressBar) getView().findViewById(
				R.id.progressBar1);

		disconnectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				globalVars.mBtConnector.closeConnection();
				globalVars.mMavLinkStuff.stopMavLinkParserThread();

			}
		});

		refreshUI();

	}

	private final AdapterView.OnItemClickListener btListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) view).getText().toString();
			String address = info.substring(info.length() - 17);

			globalVars.mBtConnector.openConnection(address);
			globalVars.mMavLinkStuff.startMavLinkParserThread();

		}
	};

	public void refreshUI() {

		switch (globalVars.getUiMode()) {
		case AppGlobals.UI_MODE_CREATED:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			break;

		case AppGlobals.UI_MODE_TURNING_ON:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.VISIBLE);
			break;
		case AppGlobals.UI_MODE_STATE_ON:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			btDevListView.setVisibility(View.VISIBLE);
			RefreshBtDevList();
			break;
		case AppGlobals.UI_MODE_TURNING_OFF:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.VISIBLE);
			break;
		case AppGlobals.UI_MODE_STATE_OFF:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			btDevListView.setVisibility(View.INVISIBLE);
			break;
		case AppGlobals.UI_MODE_CONNECTED:
			disconnectButton.setVisibility(View.VISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			break;
		case AppGlobals.UI_MODE_DISCONNECTED:
			disconnectButton.setVisibility(View.INVISIBLE);
			connProgressBar.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}

	}

	private void RefreshBtDevList() {

		switch (btDevList.RefreshList()) {
		case AppGlobals.ERROR_NO_ADAPTER:
			Toast.makeText(getActivity().getApplicationContext(),
					"No Bluetooth Adapter found.", Toast.LENGTH_LONG).show();
			return;
		case AppGlobals.ERROR_ADAPTER_OFF:
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBtIntent,
					AppGlobals.REQUEST_ENABLE_BT);
			return;
		case AppGlobals.ERROR_NO_BONDED_DEV:
			Toast.makeText(
					getActivity().getApplicationContext(),
					R.string.error_no_paired_bt_devices_found_pair_device_first,
					Toast.LENGTH_LONG).show();
			return;

		case AppGlobals.LIST_OK:
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity().getApplicationContext(),
					android.R.layout.simple_list_item_1,
					// android.R.layout.simple_dropdown_item_1line,
					btDevList.GetDeviceList().toArray(new String[0])) {

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View view = super.getView(position, convertView, parent);

					TextView textView = (TextView) view
							.findViewById(android.R.id.text1);
					textView.setTextColor(Color.DKGRAY);

					return view;
				}
			};

			btDevListView.setAdapter(adapter);
			btDevListView.setOnItemClickListener(btListClickListener);

			return;

		}

	}

	@Override
	public void onUiModeChanged() {
		refreshUI();
	}

}