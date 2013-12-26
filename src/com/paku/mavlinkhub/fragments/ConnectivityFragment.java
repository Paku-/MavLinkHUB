package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.BTDevicesListHandler;
import com.paku.mavlinkhub.communication.CommunicationHUB;

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
import android.widget.TextView;
import android.widget.Toast;

public class ConnectivityFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = "ConnectivityFragment";

	BTDevicesListHandler btDevList = new BTDevicesListHandler();
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

/*		
		if (comHUB.IsConnected()) 
		{
			button.setVisibility(View.VISIBLE);			
		}else
			button.setVisibility(View.INVISIBLE);		
*/
		
		refreshUI();
		
		return connView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				comHUB.mBtConnector.CloseConnection();

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

			comHUB.mBtConnector.ConnectBT(address);

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void refreshUI() {
		
		switch (comHUB.getUiMode()) {
		case CommunicationHUB.UI_MODE_CREATED:
			button.setVisibility(View.INVISIBLE);
			break;
			
		case CommunicationHUB.UI_MODE_CONNECTED:
			button.setVisibility(View.VISIBLE);
			break;

		case CommunicationHUB.UI_MODE_DISCONNECTED:
			button.setVisibility(View.INVISIBLE);			
			break;			

		default:
			break;
		}
				
				
		
	}

	private void RefreshBtDevList() {

		switch (btDevList.RefreshList()) {
		case CommunicationHUB.ERROR_NO_ADAPTER:
			Toast.makeText(getActivity().getApplicationContext(),
					"No Bluetooth Adapter found.", Toast.LENGTH_LONG).show();
			return;
		case CommunicationHUB.ERROR_ADAPTER_OFF:
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			this.startActivityForResult(enableBtIntent, CommunicationHUB.REQUEST_ENABLE_BT);
			return;
		case CommunicationHUB.ERROR_NO_BONDED_DEV:
			Toast.makeText(
					getActivity().getApplicationContext(),
					R.string.error_no_paired_bt_devices_found_pair_device_first,
					Toast.LENGTH_LONG).show();
			return;

		case CommunicationHUB.LIST_OK:
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