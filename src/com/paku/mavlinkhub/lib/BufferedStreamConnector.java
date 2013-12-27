package com.paku.mavlinkhub.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import android.bluetooth.BluetoothSocket;
import android.util.Log;



public abstract class BufferedStreamConnector {
	
	public ByteArrayOutputStream buffer;
	public boolean lockBuffer = false;
	
	protected abstract boolean openConnection(String address); // throws UnknownHostException,IOException;
	protected abstract void closeConnection(); // throws UnknownHostException,IOException;
	protected abstract boolean isConnected();
	protected abstract String getPeerName();
	protected abstract void startTransmission(BluetoothSocket socket);
	
	public BufferedStreamConnector(int capacity)
	{		

		buffer = new ByteArrayOutputStream(capacity);
		buffer.reset();
		
	}
	
	public void waitForBufferLock(){
		while(lockBuffer){};
		lockBuffer = true;		
	}
	
	public void releaseBuffer(){
		lockBuffer = false;		
	}
	
	public void processBuffer(){
		
		waitForBufferLock();
		
		Log.d("DATA","["+ String.valueOf(buffer.size())+"]:"+buffer.toString());
		buffer.reset();

		releaseBuffer();

	}
	
	private void resetBuffer(){		
		waitForBufferLock();
		buffer.reset();		
		releaseBuffer();
	}
	
	public void copyandResetBuffer(OutputStream targetStream) throws IOException{
		waitForBufferLock();
		buffer.writeTo(targetStream);
		resetBuffer();
		releaseBuffer();		
	}
	


}
