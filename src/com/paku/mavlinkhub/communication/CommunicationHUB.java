package com.paku.mavlinkhub.communication;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;



public class CommunicationHUB extends Application{
	
	public static final int MESSAGE_READ = 101;	
	public static final int REQUEST_ENABLE_BT = 102;
	
	
	
	BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;
	Context appContext;
	
	BTConnectThread connThread;
	SocketThread	socketThread;
	Handler 		socketHandler;

	
	public void Init(Context mConext) {
		appContext = mConext;		
	}
	
	public void ConnectBT(BluetoothAdapter adapter, BluetoothDevice device) {

		//start connection threat		
		connThread = new BTConnectThread(adapter,device,this);
		connThread.start();
	}

	public void StartTransmission(BluetoothSocket socket) {		
		
		
		mBluetoothSocket = socket;
		
		socketHandler = new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg) {
				
				switch (msg.what) {
				// Received data from... somewhere
				case MESSAGE_READ:
					
					byte[] readBuf = (byte[]) msg.obj;
					String readMessage = new String(readBuf, 0, msg.arg1);
				
					Log.d("DATA",readMessage);
				
					break;
//				case MSG_SELF_DESTRY_SERVICE:
//					close();
//					break;
				default:
					super.handleMessage(msg);
				}
				
				
			}
		};

		
		socketThread = new SocketThread(socket,socketHandler);		
		socketThread.start();
		
		
		//CloseConnection();
	}
	
	
	public void CloseConnection() {

        	Log.d("BT","socketThread - Cancel..");
            socketThread.cancel();
		
        	Log.d("BT","connThread - Closing Socket..");
            connThread.disconnect();
		
	}
	

	

}


