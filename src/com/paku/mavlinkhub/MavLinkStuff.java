package com.paku.mavlinkhub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedOutputStream;

import android.content.Context;
import android.util.Log;

import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

public class MavLinkStuff implements IBufferReady{
	
	private static final String TAG = "MavLinkStuff";
	
	private AppGlobals globalVars;
	
	private ByteArrayOutputStream mMavLinkInputStream;
	
	//private PipedOutputStream sss;
	
	private byte[] mMavLinkBuffer;
	
	public MavLinkStuff(Context mConext){
		
		globalVars = ((AppGlobals) mConext.getApplicationContext());
		
		mMavLinkInputStream = new ByteArrayOutputStream();
		mMavLinkInputStream.reset();
		
		globalVars.mBtConnector.registerForIBufferReady(this);
		
		
		
	}

	@Override
	public void onBufferReady() {		
		try {
			globalVars.mBtConnector.mConnectorStream.writeTo(mMavLinkInputStream);
		} catch (IOException e) {
			Log.d(TAG, "Stream copy: " + e.getMessage());
			e.printStackTrace();
			
		}
		
		Log.d(TAG, "Buff size " + mMavLinkInputStream.size());
		
		
	}

}
