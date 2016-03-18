package com.ftdi.javad2xxdemo;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;


public class DeviceUARTFragment extends Fragment{

	// original ///////////////////////////////
	static Context DeviceUARTContext;
	D2xxManager ftdid2xx;
	FT_Device ftDev = null;
	int DevCount = -1;
    int currentIndex = -1;
    int openIndex = 0;
	
    /*graphical objects*/
	EditText readText;
    EditText writeText;
    Spinner baudSpinner;;
    Spinner stopSpinner;
    Spinner dataSpinner;
    Spinner paritySpinner;
    Spinner flowSpinner;
    Spinner portSpinner;
    ArrayAdapter<CharSequence> portAdapter;

    Button configButton;
    Button openButton;
    Button readEnButton;
    Button writeButton;
    static int iEnableReadFlag = 1;

    /*local variables*/
    int baudRate; /*baud rate*/
    byte stopBit; /*1:1stop bits, 2:2 stop bits*/
    byte dataBit; /*8:8bit, 7: 7bit*/
    byte parity;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    byte flowControl; /*0:none, 1: flow control(CTS,RTS)*/
    int portNumber; /*port number*/
    ArrayList<CharSequence> portNumberList;


    public static final int readLength = 512;
    public int readcount = 0;
    public int iavailable = 0;
    byte[] readData;
    char[] readDataToText;
    public boolean bReadThreadGoing = false;
    public readThread read_thread;

    boolean uart_configured = false;

	// Empty Constructor
	public DeviceUARTFragment()
	{
	}

	/* Constructor */
	public DeviceUARTFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		DeviceUARTContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.device_uart, container, false);

		readData = new byte[readLength];
		readDataToText = new char[readLength];

		readText = (EditText) view.findViewById(R.id.ReadValues);
		readText.setInputType(0);
		writeText = (EditText) view.findViewById(R.id.WriteValues);

		openButton = (Button) view.findViewById(R.id.openButton);
		configButton = (Button) view.findViewById(R.id.configButton);
		readEnButton = (Button) view.findViewById(R.id.readEnButton);
		writeButton = (Button) view.findViewById(R.id.WriteButton);

		baudSpinner = (Spinner) view.findViewById(R.id.baudRateValue);
		ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.baud_rate,
						R.layout.my_spinner_textview);
		baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		baudSpinner.setAdapter(baudAdapter);
		baudSpinner.setSelection(4);
		/* by default it is 9600 */
		baudRate = 9600;

		stopSpinner = (Spinner) view.findViewById(R.id.stopBitValue);
		ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.stop_bits,
						R.layout.my_spinner_textview);
		stopAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		stopSpinner.setAdapter(stopAdapter);
		/* default is stop bit 1 */
		stopBit = 1;

		dataSpinner = (Spinner) view.findViewById(R.id.dataBitValue);
		ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.data_bits,
						R.layout.my_spinner_textview);
		dataAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		dataSpinner.setAdapter(dataAdapter);
		dataSpinner.setSelection(1);
		/* default data bit is 8 bit */
		dataBit = 8;

		paritySpinner = (Spinner) view.findViewById(R.id.parityValue);
		ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.parity,
						R.layout.my_spinner_textview);
		parityAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		paritySpinner.setAdapter(parityAdapter);
		/* default is none */
		parity = 0;

		flowSpinner = (Spinner) view.findViewById(R.id.flowControlValue);
		ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.flow_control,
						R.layout.my_spinner_textview);
		flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		flowSpinner.setAdapter(flowAdapter);
		/* default flow control is is none */
		flowControl = 0;


		portSpinner = (Spinner) view.findViewById(R.id.portValue);
		portAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.port_list_1,
						R.layout.my_spinner_textview);	
		portAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		portSpinner.setAdapter(portAdapter);
		portNumber = 1; 	 	 	
		
		/* set the adapter listeners for baud */
		baudSpinner.setOnItemSelectedListener(new MyOnBaudSelectedListener());
		/* set the adapter listeners for stop bits */
		stopSpinner.setOnItemSelectedListener(new MyOnStopSelectedListener());
		/* set the adapter listeners for data bits */
		dataSpinner.setOnItemSelectedListener(new MyOnDataSelectedListener());
		/* set the adapter listeners for parity */
		paritySpinner.setOnItemSelectedListener(new MyOnParitySelectedListener());
		/* set the adapter listeners for flow control */
		flowSpinner.setOnItemSelectedListener(new MyOnFlowSelectedListener());
		/* set the adapter listeners for port number */
		portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());

		openButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(DevCount <= 0)
				{
					createDeviceList();
				}
				else
				{
					connectFunction();
				}
			}
		});
		
		configButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(DevCount <= 0 || ftDev == null)
		    	{
		    		Toast.makeText(DeviceUARTContext, "Device not open yet...", Toast.LENGTH_SHORT).show();		    	
		    	}
				else
				{
					SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
				}
			}
		});
		
        readEnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				if(DevCount <= 0 || ftDev == null)
		    	{
		    		Toast.makeText(DeviceUARTContext, "Device not open yet...", Toast.LENGTH_SHORT).show();		    	
		    	}
				else if( uart_configured == false)
		    	{
		    		Toast.makeText(DeviceUARTContext, "UART not configure yet...", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	else
				{
					EnableRead();
				}
            }
        });
		
		writeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(DevCount <= 0 || ftDev == null)
		    	{
		    		Toast.makeText(DeviceUARTContext, "Device not open yet...", Toast.LENGTH_SHORT).show();		    	
		    	}
				else if( uart_configured == false)
		    	{
		    		Toast.makeText(DeviceUARTContext, "UART not configure yet...", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	else
				{
					SendMessage();
				}
			}
		});
		
        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

    @Override
	public void onStart() {
    	super.onStart();
    	createDeviceList();
    }

	@Override
	public void onStop()
	{
		disconnectFunction();
		super.onStop();
	}
	
	public void notifyUSBDeviceAttach()
	{
		createDeviceList();
	}
	
	public void notifyUSBDeviceDetach()
	{
		disconnectFunction();
	}	

	public void createDeviceList()
	{
		int tempDevCount = ftdid2xx.createDeviceInfoList(DeviceUARTContext);
		
		if (tempDevCount > 0)
		{
			if( DevCount != tempDevCount )
			{
				DevCount = tempDevCount;
				updatePortNumberSelector();
			}
		}
		else
		{
			DevCount = -1;
			currentIndex = -1;
		}
	}
	
	public void disconnectFunction()
	{
		DevCount = -1;
		currentIndex = -1;
		bReadThreadGoing = false;
		try {
			Thread.sleep(50);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(ftDev != null)
		{
			synchronized(ftDev)
			{
				if( true == ftDev.isOpen())
				{
					ftDev.close();
				}
			}
		}
	}
	
	public void connectFunction()
	{
		int tmpProtNumber = openIndex + 1;

		if( currentIndex != openIndex )
		{
			if(null == ftDev)
			{
				ftDev = ftdid2xx.openByIndex(DeviceUARTContext, openIndex);
			}
			else
			{
				synchronized(ftDev)
				{
					ftDev = ftdid2xx.openByIndex(DeviceUARTContext, openIndex);
				}
			}
			uart_configured = false;
		}
		else
		{
			Toast.makeText(DeviceUARTContext,"Device port " + tmpProtNumber + " is already opened",Toast.LENGTH_LONG).show();
			return;
		}

		if(ftDev == null)
		{
			Toast.makeText(DeviceUARTContext,"open device port("+tmpProtNumber+") NG, ftDev == null", Toast.LENGTH_LONG).show();
			return;
		}
			
		if (true == ftDev.isOpen())
		{
			currentIndex = openIndex;
			Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") OK", Toast.LENGTH_SHORT).show();
				
			if(false == bReadThreadGoing)
			{
				read_thread = new readThread(handler);
				read_thread.start();
				bReadThreadGoing = true;
			}
		}
		else 
		{			
			Toast.makeText(DeviceUARTContext, "open device port(" + tmpProtNumber + ") NG", Toast.LENGTH_LONG).show();
			//Toast.makeText(DeviceUARTContext, "Need to get permission!", Toast.LENGTH_SHORT).show();			
		}
	}
	
	public void updatePortNumberSelector()
	{
		//Toast.makeText(DeviceUARTContext, "updatePortNumberSelector:" + DevCount, Toast.LENGTH_SHORT).show();
		
		if(DevCount == 2)
		{
			portAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.port_list_2,
							R.layout.my_spinner_textview);
			portAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
			portSpinner.setAdapter(portAdapter);
			portAdapter.notifyDataSetChanged();
			Toast.makeText(DeviceUARTContext, "2 port device attached", Toast.LENGTH_SHORT).show();
			//portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());
		}
		else if(DevCount == 4)
		{
			portAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.port_list_4,
							R.layout.my_spinner_textview);
			portAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
			portSpinner.setAdapter(portAdapter);
			portAdapter.notifyDataSetChanged();
			Toast.makeText(DeviceUARTContext, "4 port device attached", Toast.LENGTH_SHORT).show();
			//portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());
		}
		else
		{
			portAdapter = ArrayAdapter.createFromResource(DeviceUARTContext, R.array.port_list_1,
							R.layout.my_spinner_textview);
			portAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
			portSpinner.setAdapter(portAdapter);
			portAdapter.notifyDataSetChanged();	
			Toast.makeText(DeviceUARTContext, "1 port device attached", Toast.LENGTH_SHORT).show();
			//portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());
		}

	}

	public class MyOnBaudSelectedListener implements OnItemSelectedListener
    {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			baudRate = Integer.parseInt(parent.getItemAtPosition(pos).toString());
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

    public class MyOnStopSelectedListener implements OnItemSelectedListener
    {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			stopBit = (byte)Integer.parseInt(parent.getItemAtPosition(pos).toString());
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

    public class MyOnDataSelectedListener implements OnItemSelectedListener
    {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			dataBit = (byte)Integer.parseInt(parent.getItemAtPosition(pos).toString());
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

    public class MyOnParitySelectedListener implements OnItemSelectedListener
    {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			String parityString = new String(parent.getItemAtPosition(pos).toString());
			if(parityString.compareTo("none") == 0)
			{
				parity = 0;
			}
			else if(parityString.compareTo("odd") == 0)
			{
				parity = 1;
			}
			else if(parityString.compareTo("even") == 0)
			{
				parity = 2;
			}
			else if(parityString.compareTo("mark") == 0)
			{
				parity = 3;
			}
			else if(parityString.compareTo("space") == 0)
			{
				parity = 4;
			}
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

    public class MyOnFlowSelectedListener implements OnItemSelectedListener
    {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			String flowString = new String(parent.getItemAtPosition(pos).toString());
			if(flowString.compareTo("none")==0)
			{
				flowControl = 0;
			}
			else if(flowString.compareTo("CTS/RTS")==0)
			{
				flowControl = 1;
			}
			else if(flowString.compareTo("DTR/DSR")==0)
			{
				flowControl = 2;
			}
			else if(flowString.compareTo("XOFF/XON")==0)
			{
				flowControl = 3;
			}
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

	public class MyOnPortSelectedListener implements OnItemSelectedListener
    {
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
		{
			openIndex = Integer.parseInt(parent.getItemAtPosition(pos).toString()) - 1;
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

 	public void SetConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl)
 {
		if (ftDev.isOpen() == false) {
			Log.e("j2xx", "SetConfig: device not open");
			return;
		}

		// configure our port
		// reset to UART mode for 232 devices
		ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		ftDev.setBaudRate(baud);

		switch (dataBits) {
		case 7:
			dataBits = D2xxManager.FT_DATA_BITS_7;
			break;
		case 8:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		default:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		}

		switch (stopBits) {
		case 1:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		case 2:
			stopBits = D2xxManager.FT_STOP_BITS_2;
			break;
		default:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		}

		switch (parity) {
		case 0:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		case 1:
			parity = D2xxManager.FT_PARITY_ODD;
			break;
		case 2:
			parity = D2xxManager.FT_PARITY_EVEN;
			break;
		case 3:
			parity = D2xxManager.FT_PARITY_MARK;
			break;
		case 4:
			parity = D2xxManager.FT_PARITY_SPACE;
			break;
		default:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		}

		ftDev.setDataCharacteristics(dataBits, stopBits, parity);

		short flowCtrlSetting;
		switch (flowControl) {
		case 0:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		case 1:
			flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
			break;
		case 2:
			flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
			break;
		case 3:
			flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
			break;
		default:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		}

		// TODO : flow ctrl: XOFF/XOM
		// TODO : flow ctrl: XOFF/XOM
		ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);

		uart_configured = true;
		Toast.makeText(DeviceUARTContext, "Config done", Toast.LENGTH_SHORT).show();
	}

    public void EnableRead (){    	
    	iEnableReadFlag = (iEnableReadFlag + 1)%2;
    	    	
		if(iEnableReadFlag == 1) {
			ftDev.purge((byte) (D2xxManager.FT_PURGE_TX));
			ftDev.restartInTask();
			readEnButton.setText("Read Enabled");
		}
		else{
			ftDev.stopInTask();
			readEnButton.setText("Read Disabled");
		}
    }

    public void SendMessage() {
		if (ftDev.isOpen() == false) {
			Log.e("j2xx", "SendMessage: device not open");
			return;
		}

		ftDev.setLatencyTimer((byte) 16);
//		ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

		String writeData = writeText.getText().toString();
		byte[] OutData = writeData.getBytes();
		ftDev.write(OutData, writeData.length());
    }

	final Handler handler =  new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		if(iavailable > 0)
    		{
    			readText.append(String.copyValueOf(readDataToText, 0, iavailable));
    		}
    	}
    };

	private class readThread  extends Thread
	{
		Handler mHandler;

		readThread(Handler h){
			mHandler = h;
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run()
		{
			int i;

			while(true == bReadThreadGoing)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}

				synchronized(ftDev)
				{
					iavailable = ftDev.getQueueStatus();				
					if (iavailable > 0) {
						
						if(iavailable > readLength){
							iavailable = readLength;
						}
						
						ftDev.read(readData, iavailable);
						for (i = 0; i < iavailable; i++) {
							readDataToText[i] = (char) readData[i];
						}
						Message msg = mHandler.obtainMessage();
						mHandler.sendMessage(msg);
					}
				}
			}
		}

	}
	
    /**
     * Hot plug for plug in solution
     * This is workaround before android 4.2 . Because BroadcastReceiver can not
     * receive ACTION_USB_DEVICE_ATTACHED broadcast
     */

	@Override
	public void onResume() {
	    super.onResume();
		DevCount = 0;
		createDeviceList();
		if(DevCount > 0)
		{
			connectFunction();
			SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
		}	    
	} 
}

