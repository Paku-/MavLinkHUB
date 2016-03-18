package com.ftdi.javad2xxdemo;

import java.util.Timer;

import android.app.Fragment;
import android.content.Context;
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


public class DeviceStatusFragment extends Fragment {

	Context DeviceStatusContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;
	int DevCount = -1;
	
	TextView ErrorInformation;
	Button btnMiscTest;
	TextView modemText;
	TextView lineText;
	TextView latencyText;
	TextView descText;
	TextView bitmodeText1;
	TextView bitmodeText2;
	TextView bitmodeText3;
	TextView bitmodeText4;
	TextView bitmodeText5;
	TextView bitmodeText6;
	TextView bitmodeText7;
	TextView bitmodeText8;
	Button btnSetBreak;
	Button btnSetChar;
	Timer timer;
	// Empty Constructor
	public DeviceStatusFragment()
	{
	}

	/* Constructor */
	public DeviceStatusFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		DeviceStatusContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_status, container, false);

        ErrorInformation = (TextView)view.findViewById(R.id.ErrorInfo);
    	modemText = (TextView)view.findViewById(R.id.ModemStatus);
    	lineText = (TextView)view.findViewById(R.id.LineStatus);
    	latencyText = (TextView)view.findViewById(R.id.LatencyTimer);
    	descText = (TextView)view.findViewById(R.id.Description);
    	bitmodeText1 = (TextView)view.findViewById(R.id.BitMode1);
    	bitmodeText2 = (TextView)view.findViewById(R.id.BitMode2);
    	bitmodeText3 = (TextView)view.findViewById(R.id.BitMode3);
    	bitmodeText4 = (TextView)view.findViewById(R.id.BitMode4);
    	bitmodeText5 = (TextView)view.findViewById(R.id.BitMode5);
    	bitmodeText6 = (TextView)view.findViewById(R.id.BitMode6);
    	bitmodeText7 = (TextView)view.findViewById(R.id.BitMode7);
    	bitmodeText8 = (TextView)view.findViewById(R.id.BitMode8);

        btnMiscTest = (Button)view.findViewById(R.id.button_misc_test);
    	btnSetChar = (Button)view.findViewById(R.id.btn_start_set_char);
    	btnSetBreak = (Button)view.findViewById(R.id.btn_start_test_break);

    	btnMiscTest.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				if(DevCount <= 0)
					ConnectFunction();

				if(DevCount > 0)
				{
					uartSetting();
					GetStatus();
				}
            }
        });

    	btnSetBreak.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				if(DevCount <= 0)
					ConnectFunction();
				
				if(DevCount > 0)
					StartTestBreak();
            }
        });

        btnSetChar.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				if(DevCount <= 0)
					ConnectFunction();
				
				if(DevCount > 0)
					StartTestSetChar();            	
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
	
	@Override
	public void onStop() {
		if(ftDevice != null && true == ftDevice.isOpen())
		{			
			ftDevice.close();
		}
		super.onStop();
	}
	
	public void ConnectFunction() {
		int openIndex = 0;

		if (DevCount > 0)
			return;

		DevCount = ftdid2xx.createDeviceInfoList(DeviceStatusContext);

		if (DevCount > 0) {
			ftDevice = ftdid2xx.openByIndex(DeviceStatusContext, openIndex);

			if(ftDevice == null)
			{
				Toast.makeText(DeviceStatusContext,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}
			
			if (true == ftDevice.isOpen()) {
				Toast.makeText(DeviceStatusContext,
						"devCount:" + DevCount + " open index:" + openIndex,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(DeviceStatusContext, "Need to get permission!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e("j2xx", "DevCount <= 0");
		}
    }

    public void StartTestSetChar (){
    	uartSetting();
    	
    	boolean bRet = ftDevice.setChars((byte)0x65,(byte)0x65,(byte)0x45,(byte)0x45);    	
    	if(true == bRet)
    	{
    		btnSetChar.setText("Test Set Char - Pass");
    	}
    	else
    	{
    		btnSetChar.setText("Test Set Char - Fail");
    	}    	
    }

    public void StartTestBreak (){
    	uartSetting();

    	boolean bRet1 = ftDevice.setBreakOn();
    	try {
    		Thread.sleep(200);
    	} catch (InterruptedException e) {	
    		e.printStackTrace();
    	}
    	boolean bRet2 = ftDevice.setBreakOff();
    	
    	if(bRet1 && bRet2)
    	{
    		btnSetBreak.setText("Test Set Break - Pass");
    	}
    	else
    	{
    		btnSetBreak.setText("Test Set Break - Fail");
    	}
    }
    
    public void uartSetting()
    {
    	if(null == ftDevice)
    	{
    		DevCount = -1;
    		return;
    	}
    	
		ftDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
		ftDevice.setBaudRate(9600);
		ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
				D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
		ftDevice.setFlowControl(D2xxManager.FT_FLOW_NONE, (byte) 0x00, (byte) 0x00);
		ftDevice.setLatencyTimer((byte) 16);
		ftDevice.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));
    }

	public void GetStatus()
	{
		short sModem = ftDevice.getModemStatus();
		modemText.setText("ModemStatus : " + Integer.toHexString(sModem));
		
		// try
		// {
			short sLine = ftDevice.getLineStatus();
			lineText.setText("LineStatus : " + Integer.toHexString(sLine));
		// }
		//catch(D2xxException e){e.printStackTrace();}
		
		byte ltime = ftDevice.getLatencyTimer();
		latencyText.setText("LatencyTimer : " + ltime);		
				
		descText.setText("Description : " + ftDevice.getDeviceInfo().description);

		boolean setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_RESET);	
		//bitmodeText1.setText("RESET BitMode Value : "+ ftDevice.getBitMode());		
		bitmodeText1.setText("Set RESET BitMode : "+ (setBM?"OK":"NG"));
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_ASYNC_BITBANG);
		bitmodeText2.setText("Set ASYNC BITBANG BitMode : "+ (setBM?"OK":"NG"));		
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_MPSSE);
		bitmodeText3.setText("Set MPSSE BitMode : "+ (setBM?"OK":"NG"));
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_SYNC_BITBANG);
		bitmodeText4.setText("Set SYNC BITBANG BitMode : "+ (setBM?"OK":"NG"));
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_MCU_HOST);
		bitmodeText5.setText("Set MCU HOST BitMode : "+ (setBM?"OK":"NG"));
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_FAST_SERIAL);
		bitmodeText6.setText("Set FAST SERIAL BitMode : "+ (setBM?"OK":"NG"));
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_CBUS_BITBANG);
		bitmodeText7.setText("Set CBUS BITBANG BitMode : "+ (setBM?"OK":"NG"));
		
		setBM = ftDevice.setBitMode((byte)0xFF , D2xxManager.FT_BITMODE_SYNC_FIFO);
		bitmodeText8.setText("Set SYNC FIFO BitMode : "+ (setBM?"OK":"NG"));
		
	}
}
