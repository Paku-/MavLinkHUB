package com.paku.mavlinkhub.communication;

import java.util.ArrayList;
import java.util.Set;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.ui_helpers.ListView_BTDevices;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CommunicationHUB extends Application{
	

	public static final int REQUEST_ENABLE_BT = 1;
	
	BluetoothAdapter mBluetoothAdapter;
	Context appContext;

	
	public void Init(Context mConext) {
		appContext = mConext;		
	}
	
	public void ConnectBT() {
	
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        final ArrayList<String> pairedDevicesView = new ArrayList<String>();
		
		if (!mBluetoothAdapter.isEnabled()) {
			// No BT adapter or no paired devices.
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) appContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);						
		}

		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			pairedDevicesView.clear();
		    for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a ListView
		    	pairedDevicesView.add(device.getName() + "\n" + device.getAddress());
		    }
		    
		    
            Intent intentBtSelect = new Intent();
            intentBtSelect.setClass(appContext, ListView_BTDevices.class);
            intentBtSelect.putExtra("BTDevList",pairedDevicesView.toArray(new String[0]));
            ((Activity) appContext).startActivityForResult(intentBtSelect, 0);	            
		    
		    
		}		
		else{
			Toast.makeText(appContext, R.string.error_no_bt_adapter_or_paired_devices,
					   Toast.LENGTH_LONG).show();						
		}
					
		
	}

}


