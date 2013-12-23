package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;
import java.util.Set;

import com.paku.mavlinkhub.R;
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

	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	ListView btDevListView;
	Button button;

	public ConnectivityFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View connView = inflater.inflate(R.layout.fragment_connectivity,
				container, false);

		btDevListView = (ListView) connView.findViewById(R.id.list_bt_bonded);
		button = (Button) connView.findViewById(R.id.button_connect);

		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CommunicationHUB comHUB = (CommunicationHUB) getActivity()
						.getApplication();
				comHUB.ConnectBT();
			}
		});

		button.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onResume() {
		super.onResume();

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		final ArrayList<String> bondedDevicesList = new ArrayList<String>();

		if (!mBluetoothAdapter.isEnabled()) {

			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			bondedDevicesList.clear();
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// btDevListView
				bondedDevicesList.add(device.getName() + "\n"
						+ device.getAddress());
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity().getApplicationContext(),
					android.R.layout.simple_list_item_1,
					//android.R.layout.simple_dropdown_item_1line,
					bondedDevicesList.toArray(new String[0])) {

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View view = super.getView(position, convertView, parent);

					TextView textView = (TextView) view
							.findViewById(android.R.id.text1);
					textView.setTextColor(Color.DKGRAY);

					return view;
				}
			}

			;

			btDevListView.setAdapter(adapter);
			btDevListView.setOnItemClickListener(btListClickListener);

		} else {

			Toast.makeText(
					getActivity().getApplicationContext(),
					R.string.error_no_paired_bt_devices_found_pair_device_first,
					Toast.LENGTH_LONG).show();

		}

	}

	private final AdapterView.OnItemClickListener btListClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			button.setVisibility(View.VISIBLE);

		}
	};

}