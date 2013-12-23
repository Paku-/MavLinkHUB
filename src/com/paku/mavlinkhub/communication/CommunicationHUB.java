package com.paku.mavlinkhub.communication;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;



public class CommunicationHUB extends Application{
	

	public static final int REQUEST_ENABLE_BT = 1;
	
	BluetoothAdapter mBluetoothAdapter;
	Context appContext;

	
	public void Init(Context mConext) {
		appContext = mConext;		
	}
	
	public void ConnectBT() {
					
		
	}

}


