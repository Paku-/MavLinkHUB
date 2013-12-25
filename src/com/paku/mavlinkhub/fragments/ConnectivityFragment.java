package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.BTDevices;
import com.paku.mavlinkhub.communication.CommunicationHUB;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.widget.TextView;
import android.widget.Toast;

public class ConnectivityFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = "ConnectivityFragment";

	private static final int REQUEST_ENABLE_BT = 123;

	private static final int LIST_OK = 1;
	private static final int ERROR_NO_ADAPTER = 2;
	private static final int ERROR_ADAPTER_OFF = 3;
	private static final int ERROR_NO_BONDED_DEV = 4;

	BTDevices btDevList = new BTDevices();
	ListView btDevListView;
	Button button;
	CommunicationHUB comHUB;

	public ConnectivityFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		comHUB = (CommunicationHUB) getActivity().getApplication();

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
		button.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View connView = inflater.inflate(R.layout.fragment_connectivity,
				container, false);

		btDevListView = (ListView) connView.findViewById(R.id.list_bt_bonded);
		button = (Button) connView.findViewById(R.id.button_disconnect);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				comHUB.CloseConnection();

			}
		});

	}

	private final AdapterView.OnItemClickListener btListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) view).getText().toString();
			String address = info.substring(info.length() - 17);

			comHUB.ConnectBT(address);

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void refreshUI(String action, int state) {

		if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {

			switch (state) {
			case BluetoothAdapter.STATE_CONNECTING:
				button.setVisibility(View.INVISIBLE);
				break;
			case BluetoothAdapter.STATE_CONNECTED:
				button.setVisibility(View.VISIBLE);
				break;
			case BluetoothAdapter.STATE_DISCONNECTING:
				button.setVisibility(View.VISIBLE);
				break;
			case BluetoothAdapter.STATE_DISCONNECTED:
				button.setVisibility(View.INVISIBLE);
				break;
			}
		}

		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

			switch (state) {
			case BluetoothAdapter.STATE_OFF:

				break;
			case BluetoothAdapter.STATE_TURNING_OFF:

				break;
			case BluetoothAdapter.STATE_ON:

				break;
			case BluetoothAdapter.STATE_TURNING_ON:

				break;
			}
		}
		
		if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
			button.setVisibility(View.VISIBLE);
		}
		
		if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
			button.setVisibility(View.INVISIBLE);
		}
		

	}

	private void RefreshBtDevList() {

		switch (btDevList.RefreshList()) {
		case ERROR_NO_ADAPTER:
			Toast.makeText(getActivity().getApplicationContext(),
					"No Bluetooth Adapter found.", Toast.LENGTH_LONG).show();
			return;
		case ERROR_ADAPTER_OFF:
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return;
		case ERROR_NO_BONDED_DEV:
			Toast.makeText(
					getActivity().getApplicationContext(),
					R.string.error_no_paired_bt_devices_found_pair_device_first,
					Toast.LENGTH_LONG).show();
			return;

		case LIST_OK:
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

}