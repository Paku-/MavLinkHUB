package com.ftdi.javad2xxdemo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.D2xxManager.D2xxException;


public class BitBangModeService extends Service {
	D2xxManager ftdid2xx = null;
	FT_Device ftDevice = null;
	Context myContext = null; 

	  private Looper mServiceLooper;
	  private ServiceHandler mServiceHandler;
	  int devCount;

	  // Handler that receives messages from the thread
	  private final class ServiceHandler extends Handler {
	      public ServiceHandler(Looper looper) {
	          super(looper);
	      }
	      @Override
	      public void handleMessage(Message msg) {
	          // Normally we would do some work here, like download a file.
	          // For our sample, we just sleep for 5 seconds.
	          long endTime = System.currentTimeMillis() + 10*1000;

	  		byte[] data1 = new byte [] {(byte)0xFF};
			byte[] data2 = new byte [] {(byte)0x00};

			// open our first device
			ftDevice = ftdid2xx.openByIndex(myContext, 0);
			// configure our port , Set to ASYNC BIT MODE
			ftDevice.setBitMode((byte) 0xFF, D2xxManager.FT_BITMODE_ASYNC_BITBANG);
			// configure Baud rate
			ftDevice.setBaudRate(9600);

	          while (System.currentTimeMillis() < endTime) {
	              synchronized (this) {
	                  try {
	                      // wait(endTime - System.currentTimeMillis());
	                	  ftDevice.write(data1, 1);
	                	  Thread.sleep(1000);
	                	  ftDevice.write(data2, 1);
	                	  Thread.sleep(1000);
	                	  Log.i("Device Loopback"," Get Modem Status " + Integer.toString(ftDevice.getModemStatus()));
	                	  Log.i("Device Loopback"," Get Line Status " + Integer.toString(ftDevice.getLineStatus()));
	                  } catch (Exception e) {
	                  }
	              }
	          }
	          // Stop the service using the startId, so that we don't stop
	          // the service in the middle of handling another job
	          stopSelf(msg.arg1);
	      }
	  }

	  @Override
	  public void onCreate() {
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments",
	            Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();

	    // Get the HandlerThread's Looper and use it for our Handler
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	    myContext = this;

	    // Create D2xx class
		try {
			ftdid2xx = D2xxManager.getInstance(this);
			devCount = ftdid2xx.createDeviceInfoList(this);
		} catch (D2xxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
	      Message msg = mServiceHandler.obtainMessage();
	      msg.arg1 = startId;
	      mServiceHandler.sendMessage(msg);

	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	      // We don't provide binding, so return null
	      return null;
	  }

	  @Override
	  public void onDestroy() {
	    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	  }
	}
