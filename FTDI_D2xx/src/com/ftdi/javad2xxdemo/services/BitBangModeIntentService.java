package com.ftdi.javad2xxdemo.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.D2xxManager.D2xxException;

public class BitBangModeIntentService extends IntentService {
	D2xxManager ftdid2xx = null;
	FT_Device ftDevice = null;
	Context myContext;

	int devCount;
	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public BitBangModeIntentService() {
		super("BitBangModeService");
	}


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		try {
			ftdid2xx = D2xxManager.getInstance(this);
		} catch (D2xxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// Normally we would do some work here, like download a file.
		// For our sample, we just sleep for 5 seconds.
		long endTime = System.currentTimeMillis() + 10*1000;

		byte[] data1 = new byte [] {(byte)0xFF};
		byte[] data2 = new byte [] {(byte)0x00};
		
		myContext = this;
		devCount = ftdid2xx.createDeviceInfoList(myContext);
		// open our first device
		ftDevice = ftdid2xx.openByIndex(myContext, 0);
		// configure our port , Set to ASYNC BIT MODE
		ftDevice.setBitMode((byte) 0xFF, D2xxManager.FT_BITMODE_ASYNC_BITBANG);
		// configure Baud rate
		ftDevice.setBaudRate(9600);

    	while (System.currentTimeMillis() < endTime) {
    		synchronized (this) {
    			try {
    				ftDevice.write(data1, 1);
      	    		Thread.sleep(1000);
      	    		ftDevice.write(data2, 1);
      	    		Thread.sleep(1000);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}

		ftDevice.close();
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
