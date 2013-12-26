package com.paku.mavlinkhub.communication;

import android.app.Application;
import android.content.Context;

public class CommunicationHUB extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = "CommHUB";

	// GUI state machine constants
	public static final int UI_MODE_CREATED = 200;
	public static final int UI_MODE_BT_OFF = 201;
	public static final int UI_MODE_DISCONNECTED = 202;
	public static final int UI_MODE_CONNECTED = 203;
	
	// BT Dev List state machine constants
	public static final int LIST_OK = 1;
	public static final int ERROR_NO_ADAPTER = 2;
	public static final int ERROR_ADAPTER_OFF = 3;
	public static final int ERROR_NO_BONDED_DEV = 4;	

	//other constants
	public static final int MESSAGE_READ = 101;
	public static final int REQUEST_ENABLE_BT = 102;
	

	public Context appContext;
	
	public int ui_Mode = CommunicationHUB.UI_MODE_CREATED;
	
	//main BT connector 
	public BtConnector mBtConnector;

	public void Init(Context mConext) {
		appContext = mConext;
		mBtConnector = new BtConnector();

	}
	
	
	public int getUiMode() {
		return ui_Mode;
		
	}
	
	public void setUiMode(int mode) {
		ui_Mode = mode;		
	}
	


}
