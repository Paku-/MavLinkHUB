package com.ftdi.javad2xxdemo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.javad2xxdemo.services.BitBangModeIntentService;
import com.ftdi.javad2xxdemo.services.BitBangModeService;

public class MPSEFragment extends Fragment {
	Context MPSEFragmentContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;
	int DevCount = -1;

	TextView ErrorInformation;
	Button btnSetBigBangAsync;
	Button btnSetBigBandService;
	Button btnSetBigBandIntentService;
	BitModeAsyncTask GPIO_Task;
	mpseThread mpThread;
	
	Timer timer;
	// Empty Constructor
	public MPSEFragment()
	{
	}

	/* Constructor */
	public MPSEFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		MPSEFragmentContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 12);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_mpse, container, false);

        ErrorInformation = (TextView)view.findViewById(R.id.ErrorInformation);

        btnSetBigBangAsync = (Button)view.findViewById(R.id.bit_bang_async_mode);
        btnSetBigBandService = (Button)view.findViewById(R.id.bit_bang_service_mode);
    	btnSetBigBandIntentService = (Button)view.findViewById(R.id.bit_bang_intent_service_mode);

    	btnSetBigBangAsync.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetBigBangAsyncClick(v);
            }
        });

    	btnSetBigBandService.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetBigBandServiceClick(v);
            }
        });

    	btnSetBigBandIntentService.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetBigBandIntentServiceClick(v);
            }
        });

    	return view;
    }


    @Override
	public void onStart() {
    	Log.e(">>@@","mpse onStart...");
    	super.onStart();
    	ConnectFunction();
    }


	public void ConnectFunction() {
		int openIndex = 0;

		if (DevCount > 0)
			return;

		DevCount = ftdid2xx.createDeviceInfoList(MPSEFragmentContext);
		// Log.e(">>@@","devCount:"+ DevCount);

		if (DevCount > 0) {
			ftDevice = ftdid2xx.openByIndex(MPSEFragmentContext, openIndex);

			if(ftDevice == null)
			{
				Toast.makeText(MPSEFragmentContext,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}
			
			if (true == ftDevice.isOpen()) {

				// Log.e(">>@@","devCount:"+ DevCount + " open OK");
				Toast.makeText(MPSEFragmentContext,"devCount:" + DevCount + " open index:" + openIndex,Toast.LENGTH_SHORT).show();
			} else {
				// Log.e(">>@@","devCount:"+ DevCount + " open fail");
				Toast.makeText(MPSEFragmentContext, "Need to get permission!",Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e(">>@@", "DevCount <= 0");
		}
	}

	@Override
	public void onStop() {
		Log.e(">>@@","mpse onStop...");
		super.onStop();
		
		if(ftDevice != null && true == ftDevice.isOpen())
		{
			Log.e(">>@@","mpse onStop -  ftDevice.close()");
			ftDevice.close();
		}
	}

    static int iStartTestSetEventFlag = 0;
    public void btnSetBigBangAsyncClick (View view)  {
    	if(iStartTestSetEventFlag == 0 ) {
    		mpThread = new mpseThread();
    		mpThread.start();
    		iStartTestSetEventFlag = 1;
    		btnSetBigBangAsync.setText("Disable Test Break");
    	}
    	else {
    		mpThread.interrupt();
    		mpThread = null;
    		iStartTestSetEventFlag = 0;
    		btnSetBigBangAsync.setText("Enable Test Break");
    	}
    }
    public void btnSetBigBandServiceClick(View view) {
    	Intent intent = new Intent(getActivity(),BitBangModeService.class);
    	getActivity().startService(intent);
    }

    public void btnSetBigBandIntentServiceClick(View view) {

    	Context ctx = MPSEFragment.this.getActivity();
		Intent intent = new Intent(ctx,BitBangModeIntentService.class);

    	MPSEFragment.this.getActivity().startService(intent);
    }

    static int iReadEventFlag = 0;

	private class mpseThread  extends Thread
	{
		@Override
		public void run()
		{
			byte[] data1 = new byte [] {(byte)0xFF};
	    	byte[] data2 = new byte [] {(byte)0x00};
	    	
    		// reset device setting
    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_RESET);
			short BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
			Log.i("FTDI-Debug","Get Modem Status 1 :" + Integer.toString(BitModeValue));
			
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		// configure our port , Set to ASYNC BIT MODE
    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_ASYNC_BITBANG);
			// configure Baud rate
    		ftDevice.setBaudRate(9600);
    		
			BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
			Log.i("FTDI-Debug","Get Modem Status 2: " + Integer.toString(BitModeValue));
			
			while(true) {
				// synchronized (this) {
				Log.i("Paris ------------- Read","Write  ------------------1 !!!!!");
				ftDevice.write(data1, 1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ftDevice.write(data2, 1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//}
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
			// TODO Auto-generated method stub
			byte[] data1 = new byte [] {(byte)0xFF};
	    	byte[] data2 = new byte [] {(byte)0x00};
	    	try {
	    		// devCount = d2xx.createDeviceInfoList(AsyncTasContext);
	    		ErrorInformation = (TextView)getActivity().findViewById(R.id.ErrorInformation);
	    		//if ( devCount > 0) {

		    		// reset device setting
		    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_RESET);
					short BitModeValue = (short) (ftDevice.getBitMode() & 0x00ff); 
					Log.i("FTDI-Debug","Get Modem Status 1 :" + Integer.toString(BitModeValue));
					
		    		Thread.sleep(1000);
		    		// configure our port , Set to ASYNC BIT MODE
		    		ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_ASYNC_BITBANG);
					// configure Baud rate
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

	    		// }

	    	} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
}
