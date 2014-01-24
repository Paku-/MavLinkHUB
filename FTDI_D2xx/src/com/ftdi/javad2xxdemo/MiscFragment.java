package com.ftdi.javad2xxdemo;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class MiscFragment extends Fragment {

	Context MiscFragment;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;
	int DevCount = -1;
	
	// loopback
	Button loopbackBtn;
	EditText writeText;
	TextView readText;
	
	// event
	TextView infoText;
	Button btnSetEvent;
	Timer timer;
	Lock lockEvent;
	Condition conEvent;
	long EventMask;
	EventThread eventThread;
	
	// mpse
	Button btnSetBigBangAsync;
	BitModeAsyncTask GPIO_Task;
	mpseThread mpThread;
	
	// pin-config
	Button btnDTR;
	Button btnRTS;
	static int iDtrFlag = 1;
	static int iRtsFlag = 1;
	
	// Empty Constructor
	public MiscFragment()
	{

	}

	/* Constructor */
	public MiscFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		MiscFragment = parentContext;
		ftdid2xx = ftdid2xxContext;

	}

    public int getShownIndex() {
        return getArguments().getInt("index", 3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_misc , container, false);

        // loopback
    	loopbackBtn = (Button)view.findViewById(R.id.loopbackBtn);
    	writeText = (EditText)view.findViewById(R.id.wrtieValue);
    	readText = (TextView)view.findViewById(R.id.readText);
    	
    	loopbackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				if(DevCount <= 0)
					ConnectFunction();
				
				if(DevCount > 0)
					loopbackWriteRead();
			}
		});    	
        
        // event
        infoText = (TextView)view.findViewById(R.id.InfoText);
//        eventText = (TextView)view.findViewById(R.id.EventStatus);
    	btnSetEvent = (Button)view.findViewById(R.id.set_event);

		lockEvent = new ReentrantLock();
		conEvent  = lockEvent.newCondition();

		btnSetEvent.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {

					if(DevCount <= 0)
						ConnectFunction();
					
					if(DevCount > 0)
						ClickSetEvent(v);
			}
		});
		
		// mpse
        btnSetBigBangAsync = (Button)view.findViewById(R.id.bit_bang_async_mode);

    	btnSetBigBangAsync.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetBigBangAsyncClick(v);
            }
        });
    	
    	// pin-config
    	 btnDTR = (Button)view.findViewById(R.id.btn_dtr);
         btnRTS = (Button)view.findViewById(R.id.btn_rts);

         btnDTR.setOnClickListener(new OnClickListener() {
             public void onClick(final View v) {
					if(DevCount <= 0)
						ConnectFunction();
					
					if(DevCount > 0)
 						btn_dtr_click(v);
             }
         });

         btnRTS.setOnClickListener(new OnClickListener() {
             public void onClick(final View v) {
					if(DevCount <= 0)
						ConnectFunction();
					
					if(DevCount > 0)
 						btn_rts_Click(v);
             }
         });

		return view;
    }


	@Override
	public void onStart() {
    	super.onStart();
    	DevCount = -1;
    	ConnectFunction();
    }


	public void ConnectFunction() {
		int openIndex = 0;

		if (DevCount > 0)
			return;

		DevCount = ftdid2xx.createDeviceInfoList(MiscFragment);

		if (DevCount > 0) {
			ftDevice = ftdid2xx.openByIndex(MiscFragment, openIndex);

			if(ftDevice == null)
			{
				Toast.makeText(MiscFragment,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}
			
			if (true == ftDevice.isOpen())
			{
				ftDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
				ftDevice.setBaudRate(9600);
				ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
				D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
				ftDevice.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x00, (byte) 0x00);
				ftDevice.setLatencyTimer((byte) 16);
				ftDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

				Toast.makeText(MiscFragment,"devCount:" + DevCount + " open index:" + openIndex,Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(MiscFragment, "Need to get permission!",Toast.LENGTH_SHORT).show();
			}
		}
		else 
		{
			Log.e("j2xx", "DevCount <= 0");
		}
    }

	@Override
	public void onStop() {
		super.onStop();

		if(DevCount > 0)
		{
	      // Unregister since the activity is about to be closed.
	      if((EventMask & D2xxManager.FT_EVENT_RXCHAR) != 0)
	    	  LocalBroadcastManager.getInstance(MiscFragment).unregisterReceiver(mRXCHARMessageReceiver);
	
	      if((EventMask & D2xxManager.FT_EVENT_MODEM_STATUS) != 0)
	    	  LocalBroadcastManager.getInstance(MiscFragment).unregisterReceiver(mMODEMMessageReceiver);
	
	      if((EventMask & D2xxManager.FT_EVENT_LINE_STATUS) != 0)
			LocalBroadcastManager.getInstance(MiscFragment).unregisterReceiver(mLINEMessageReceiver);
		}

		if(ftDevice != null && true == ftDevice.isOpen())
		{			
			ftDevice.close();
		}
	}

	
	//---------------------------    loopback  -------------------------------------------------//
		public void loopbackWriteRead() {
			String writeData = writeText.getText().toString();
			
			if(writeText.length() == 0)
			{
				readText.setText("Read Data:");	
				return;
			}
			
			byte[] OutData = writeData.getBytes();
			ftDevice.write(OutData);
    		
			LoopbackReadThread lrThread = new LoopbackReadThread(handler, OutData.length); 
			lrThread.start();		
		}		

		private class LoopbackReadThread  extends Thread
		{
			Handler mHandler;
			int len;

			LoopbackReadThread(Handler h, int len){
				mHandler = h;
				this.len = len;
			}

			@Override
			public void run()
			{
				int checkTimes = 0;
				boolean bGetData = true;

				int rxq = 0;
				while (rxq < len) {
		    		try {
						Thread.sleep(50);
					} catch (InterruptedException e) {	
						e.printStackTrace();
					}
					rxq = ftDevice.getQueueStatus();

					checkTimes++;					
					if(40 == checkTimes)
					{
						Log.e("misc","get data error");
						bGetData = false;
						break;
					}
				}
				
				if(true == bGetData)
				{
					byte[] InData = new byte[rxq];
					ftDevice.read(InData);
					String ts = new String(InData);	
					Message msg = mHandler.obtainMessage(2, ts);
		        	mHandler.sendMessage(msg);
				}
			}
		}
	//---------------------------    event  -------------------------------------------------//
    static int iStartTestSetEventFlag = 0;
    public void ClickSetEvent (View view) {
    	if(iStartTestSetEventFlag == 0 ) {
    		eventThread = new EventThread(handler);
    		eventThread.start();
    		iStartTestSetEventFlag = 1;
    		btnSetEvent.setText("Disable Set Event");
    	}
    	else {
    		ftDevice.setEventNotification(0);
    		eventThread.interrupt();
    		eventThread = null;
    		iStartTestSetEventFlag = 0;
    		btnSetEvent.setText("Enable Set Event");
    	}
    }

    private BroadcastReceiver mRXCHARMessageReceiver = new BroadcastReceiver() {
    	  @Override
		public void onReceive(Context context, Intent intent) {
    	    // Get extra data included in the Intent
    		lockEvent.lock();
    		conEvent.signal();
    		lockEvent.unlock();
    	    String message = intent.getStringExtra("message");
    	    Log.d("receiver", "Got RXCHAR message: " + message);
    	  }
    };

    private BroadcastReceiver mMODEMMessageReceiver = new BroadcastReceiver() {
  	  @Override
	public void onReceive(Context context, Intent intent) {
  	    String message = intent.getStringExtra("message");
  	    Log.d("receiver", "Got MODEM message: " + message);
  	  }
    };

    private BroadcastReceiver mLINEMessageReceiver = new BroadcastReceiver() {
  	  @Override
	public void onReceive(Context context, Intent intent) {
  	    // Get extra data included in the Intent
  		lockEvent.lock();
  		conEvent.signal();
  		lockEvent.unlock();
  	    String message = intent.getStringExtra("message");
  	    Log.d("receiver", "Got LINE message: " + message);
  	  }
    };

	final Handler handler =  new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		if(msg.what == 0)
    		{
    			infoText.setText("EventStatus: " + ftDevice.getEventStatus() + " - Wait");		
    		}
    		else if(msg.what == 1)
    		{			
    			infoText.setText("EventStatus: " + ftDevice.getEventStatus() + " - Done");
    		}    		
    		else if(msg.what == 2)
    		{	
    			readText.setText("Read Data: " + (String)msg.obj);	
    		}
    	}
    };
    
    
    class EventThread extends Thread {
    	Handler mHandler;
    	
    	EventThread(Handler h){
			mHandler = h;
		}
    	
    	@Override
		public void run() {
    		EventMask = EventMask | D2xxManager.FT_EVENT_RXCHAR;

    		if((EventMask & D2xxManager.FT_EVENT_RXCHAR) != 0)
    			LocalBroadcastManager.getInstance(MiscFragment).registerReceiver(mRXCHARMessageReceiver, new IntentFilter("FT_EVENT_RXCHAR"));

    		if((EventMask & D2xxManager.FT_EVENT_MODEM_STATUS) != 0)
    			LocalBroadcastManager.getInstance(MiscFragment).registerReceiver(mMODEMMessageReceiver, new IntentFilter("FT_EVENT_MODEM_STATUS"));

    		if((EventMask & D2xxManager.FT_EVENT_LINE_STATUS) != 0)
    			LocalBroadcastManager.getInstance(MiscFragment).registerReceiver(mLINEMessageReceiver, new IntentFilter("FT_EVENT_LINE_STATUS"));

			lockEvent.lock();
			ftDevice.setEventNotification(EventMask);

			Message msg = mHandler.obtainMessage(0);
        	mHandler.sendMessage(msg);
        	
			Log.i("ftdi Set Event Notifcation ................. ",
					"  EventThread ---------------------- Before Wait ");
			try {
				conEvent.await();				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			msg = mHandler.obtainMessage(1);
        	mHandler.sendMessage(msg);

			Log.i("ftdi Set Event Notifcation ................. ",
					"  EventThread ---------------------- After Wait ");

			lockEvent.unlock();
        }
    }
    
    
    // --------------------------------- mpse ----------------------------------------------------------------//

    static int iStartTestSetBigBangFlag = 0;
    public void btnSetBigBangAsyncClick (View view)  {
    	if(iStartTestSetBigBangFlag == 0 ) {
    		mpThread = new mpseThread();
    		mpThread.start();
    		iStartTestSetBigBangFlag = 1;
    		btnSetBigBangAsync.setText("Disable ASYNC_BITBANG  Mode Test");
    	}
    	else {
    		iStartTestSetBigBangFlag = 0;
    		try {
				Thread.sleep(50);
			} catch (InterruptedException e) {	
				e.printStackTrace();
			}
    		mpThread.interrupt();
    		mpThread = null;
    		btnSetBigBangAsync.setText("Enable ASYNC_BITBANG  Mode Test");
    	}
    }

    static int iReadEventFlag = 0;

	private class mpseThread  extends Thread
	{
		@Override
		public void run()
		{
//			byte[] data1 = new byte [] {(byte)0xFF};
//	    	byte[] data2 = new byte [] {(byte)0x00};
			byte[] data1 = new byte [] {(byte)0x31};
	    	byte[] data2 = new byte [] {(byte)0x32};	    	
    		// reset device setting
    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_RESET);
			short BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
			Log.i("FTDI-Debug","Get Modem Status 1 :" + Integer.toString(BitModeValue));
			
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {	
				e.printStackTrace();
			}
    
    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_ASYNC_BITBANG);
	    		ftDevice.setBaudRate(9600);
    		
//    		try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {	
//				e.printStackTrace();
//			}
    		
			BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
			Log.i("FTDI-Debug","Get Modem Status 2: " + Integer.toString(BitModeValue));
			
			while(iStartTestSetBigBangFlag == 1) {
				ftDevice.write(data1, 1);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ftDevice.write(data2, 1);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    class BitModeAsyncTask extends AsyncTask<Void, Void ,Void>{
    	 D2xxManager d2xx;
    	 Context AsyncTasContext;
    	/* Constructor */
    	public BitModeAsyncTask(Context parentContext,D2xxManager ftdid2xxContext)
    	{
    		d2xx = ftdid2xxContext;
    		AsyncTasContext = parentContext;
    	}

    	 @Override
		protected void onPreExecute() {
    		 super.onPreExecute();
    	 }

    	 protected Void onPostExecute() {
    		 super.onPostExecute(null);
			return null;
    	 }

		@Override
		protected Void doInBackground(Void... params) {
			byte[] data1 = new byte [] {(byte)0xFF};
	    	byte[] data2 = new byte [] {(byte)0x00};
	    	try {
	    
		    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_RESET);
					short BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
					Log.i("FTDI-Debug","Get Modem Status 1 :" + Integer.toString(BitModeValue));
					
		    		Thread.sleep(1000);

		    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_ASYNC_BITBANG);
		    		ftDevice.setBaudRate(9600);
		    		
					BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
					Log.i("FTDI-Debug","Get Modem Status 2: " + Integer.toString(BitModeValue));
					
					while(true) {
						synchronized (this) {
						ftDevice.write(data1, 1);
						Thread.sleep(1000);
						ftDevice.write(data2, 1);
						Thread.sleep(1000);
						}
					}
	    	} catch (InterruptedException e) {

				e.printStackTrace();
			}
			return null;
		}
    }

	static boolean fTimer = false;
	public class timerTask extends TimerTask
	{
		@Override
		public void run()
		{
			 fTimer = true;
		}
	};
	
	
	// ------------------------------------- pin-config ------------------------------------------//
	public void btn_dtr_click(View view) {

//		// reset to UART mode for 232 devices
//		ftDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
//
//		// set 230400 baud rate
//		ftDevice.setBaudRate(230400);
//
//		// set 8 data bits, 1 stop bit, no parity
//		ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
//				D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
//
//		// set RTS/CTS flow control
//		ftDevice.setFlowControl(D2xxManager.FT_FLOW_RTS_CTS, (byte) 0x00, (byte) 0x00);

		if (iDtrFlag == 1) {
			iDtrFlag = 0;
			
			if(true == ftDevice.clrDtr())
			{
				infoText.setText("clrDtr: PASS");
			}
			else
			{
				infoText.setText("clrDtr: NG");
			}
			btnDTR.setText("Enable DTR");
		} else {
			iDtrFlag = 1;
			
			if(true == ftDevice.setDtr())
			{
				infoText.setText("setDtr: PASS");
			}
			else
			{
				infoText.setText("setDtr: NG");
			}
			btnDTR.setText("Disable DTR");
		}
		// close our device
		//ftDevice.close();

	}

	public void btn_rts_Click(View view) {
		// open our first device

		if (iRtsFlag == 1) {
			iRtsFlag = 0;
			
			if(true == ftDevice.clrRts())
			{
				infoText.setText("clrRts: PASS");
			}
			else
			{
				infoText.setText("clrRts: NG");
			}			
			btnRTS.setText("Enable RTS");
		} else {
			iRtsFlag = 1;

			if(true == ftDevice.setRts())
			{
				infoText.setText("setRts: PASS");
			}
			else
			{
				infoText.setText("setRts: NG");
			}
			btnRTS.setText("Disable RTS");
		}
		// close our device
		//ftDevice.close();
	}
}
