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
import android.os.Bundle;
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
import com.ftdi.j2xx.D2xxManager.D2xxException;


public class FtdiEventFragment extends Fragment {

	Context FtdiEventContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;
	int DevCount = -1;
	
	TextView ErrorInformation;
	TextView eventText;
	Button SendMessage;
	EditText dataToWrite;
	EditText myData;
	Button btnSetEvent;
	Timer timer;
	Lock lockEvent;
	Condition conEvent;
	long EventMask;
	EventThread eventThread;
	// Empty Constructor
	public FtdiEventFragment()
	{

	}

	/* Constructor */
	public FtdiEventFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		FtdiEventContext = parentContext;
		ftdid2xx = ftdid2xxContext;

	}

    public int getShownIndex() {
        return getArguments().getInt("index", 13);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_event , container, false);

        ErrorInformation = (TextView)view.findViewById(R.id.ErrorInfo);
        eventText = (TextView)view.findViewById(R.id.EventStatus);
        SendMessage = (Button)view.findViewById(R.id.button_send);
    	dataToWrite = (EditText)view.findViewById(R.id.send_message);
    	myData = (EditText)view.findViewById(R.id.reveive_message);
    	btnSetEvent = (Button)view.findViewById(R.id.set_event);

		lockEvent = new ReentrantLock();
		conEvent  = lockEvent.newCondition();



        SendMessage.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				if(DevCount <= 0)
					ConnectFunction();

				if(DevCount > 0)
				SendMessage(v);
            }
        });

		btnSetEvent.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				try {
					if(DevCount <= 0)
						ConnectFunction();
					
					if(DevCount > 0)
					ClickSetEvent(v);
				} catch (D2xxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				eventText.setText("EventStatus : " + ftDevice.getEventStatus());
			}
		});

		return view;
    }


	@Override
	public void onStart() {
    	Log.e(">>@@","event onStart...");
    	super.onStart();
    	ConnectFunction();
    }


	public void ConnectFunction() {
		int openIndex = 0;

		if (DevCount > 0)
			return;

		DevCount = ftdid2xx.createDeviceInfoList(FtdiEventContext);
		// Log.e(">>@@","devCount:"+ DevCount);

		if (DevCount > 0) {
			ftDevice = ftdid2xx.openByIndex(FtdiEventContext, openIndex);

			if(ftDevice == null)
			{
				Toast.makeText(FtdiEventContext,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}
			
			if (true == ftDevice.isOpen()) {
		ftDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
		ftDevice.setBaudRate(9600);
		ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
				D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
		ftDevice.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x00, (byte) 0x00);
		ftDevice.setLatencyTimer((byte) 16);
		ftDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

				Toast.makeText(FtdiEventContext,"devCount:" + DevCount + " open index:" + openIndex,Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(FtdiEventContext, "Need to get permission!",Toast.LENGTH_SHORT).show();
			}
		}
		else 
		{
			Log.e(">>@@", "DevCount <= 0");
		}
    }

	@Override
	public void onStop() {
		Log.e(">>@@","event onStop...");
		super.onStop();

		if(DevCount > 0)
		{
      // Unregister since the activity is about to be closed.
      if((EventMask & D2xxManager.FT_EVENT_RXCHAR) != 0)
    	  LocalBroadcastManager.getInstance(FtdiEventContext).unregisterReceiver(mRXCHARMessageReceiver);

      if((EventMask & D2xxManager.FT_EVENT_MODEM_STATUS) != 0)
    	  LocalBroadcastManager.getInstance(FtdiEventContext).unregisterReceiver(mMODEMMessageReceiver);

      if((EventMask & D2xxManager.FT_EVENT_LINE_STATUS) != 0)
		LocalBroadcastManager.getInstance(FtdiEventContext).unregisterReceiver(mLINEMessageReceiver);
		}

		if(ftDevice != null && true == ftDevice.isOpen())
		{			
      ftDevice.close();
			Log.e(">>@@","event onStop -  ftDevice.close()");
		}
	}


/*    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
  	  @Override
  	  public void onReceive(Context context, Intent intent) {
  	    // Get extra data included in the Intent
  	    String message = intent.getStringExtra("message");
  	    Log.d("receiver", "Got message: " + message);
  	  }
  	};
*/
    static int iStartTestSetEventFlag = 0;
    public void ClickSetEvent (View view) throws D2xxException {
    	if(iStartTestSetEventFlag == 0 ) {
    		eventThread = new EventThread();
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
  	    // Get extra data included in the Intent
  		// lockEvent.lock();
  		// conEvent.signal();
  		// lockEvent.unlock();
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

    class EventThread extends Thread {
    	@Override
		public void run() {
    		EventMask = EventMask | D2xxManager.FT_EVENT_RXCHAR;

    		if((EventMask & D2xxManager.FT_EVENT_RXCHAR) != 0)
    			LocalBroadcastManager.getInstance(FtdiEventContext).registerReceiver(mRXCHARMessageReceiver, new IntentFilter("FT_EVENT_RXCHAR"));

    		if((EventMask & D2xxManager.FT_EVENT_MODEM_STATUS) != 0)
    			LocalBroadcastManager.getInstance(FtdiEventContext).registerReceiver(mMODEMMessageReceiver, new IntentFilter("FT_EVENT_MODEM_STATUS"));

    		if((EventMask & D2xxManager.FT_EVENT_LINE_STATUS) != 0)
    			LocalBroadcastManager.getInstance(FtdiEventContext).registerReceiver(mLINEMessageReceiver, new IntentFilter("FT_EVENT_LINE_STATUS"));

			lockEvent.lock();
			ftDevice.setEventNotification(EventMask);

			// btnSetEvent.setText("Before Wait");
			Log.i("ftdi Set Event Notifcation ................. ",
					"  EventThread ---------------------- Before Wait ");
			try {
				conEvent.await();
				// ftdid2xx.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// btnSetEvent.setText("After Wait");
			Log.i("ftdi Set Event Notifcation ................. ",
					"  EventThread ---------------------- After Wait ");

			lockEvent.unlock();
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

    /** Called when the user clicks the Send button */
	public void SendMessage(View view) {
		timer = new Timer(true);

		String writeData = dataToWrite.getText().toString();
		byte[] OutData = writeData.getBytes();
		ftDevice.write(OutData, writeData.length());

		fTimer = false;
		timer.schedule(new timerTask(), 5000, 5000);
		int rxq = 0;
		while (rxq < writeData.length() && (fTimer == false)) {
			rxq = ftDevice.getQueueStatus();
		}
		timer.cancel();

		if (rxq > 0) {
			byte[] InData = new byte[rxq];
			ftDevice.read(InData, rxq);

			myData.setText(new String(InData));
		} else
			myData.setText("");
	}
}
