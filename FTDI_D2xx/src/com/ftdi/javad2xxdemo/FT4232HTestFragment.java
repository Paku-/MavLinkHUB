package com.ftdi.javad2xxdemo;

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


public class FT4232HTestFragment extends Fragment{

	// original ///////////////////////////////
	Context DeviceFT4232HTestContext;
	D2xxManager ftd2xx_0;

	FT_Device ft_device_0;
	FT_Device ft_device_1;
	FT_Device ft_device_2;
	FT_Device ft_device_3;

    /*graphical objects*/
    Spinner baudSpinner;;
    Spinner stopSpinner;
    Spinner dataSpinner;
    Spinner paritySpinner;
    Spinner flowSpinner;

    Button configButton_0;
    Button writeButton_0;
	EditText readText_0;
    EditText writeText_0;

    Button configButton_1;
    Button writeButton_1;
	EditText readText_1;
    EditText writeText_1;

    Button configButton_2;
    Button writeButton_2;
	EditText readText_2;
    EditText writeText_2;

    Button configButton_3;
    Button writeButton_3;
	EditText readText_3;
    EditText writeText_3;

    /*local variables*/
    int baudRate; 		/*baud rate*/
    byte stopBit; 		/*1:1stop bits, 2:2 stop bits*/
    byte dataBit; 		/*8:8bit, 7: 7bit*/
    byte parity;  		/* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    byte flowControl; 	/*0:none, 1: flow control(CTS,RTS)*/

    public static final int readLength = 64;
    public int readcount_0 = 0;
    public int iavailable_0 = 0;
    byte[] readData_0;
    char[] readDataToText_0;
    public boolean bReadThreadGoing_0 = false;
    public readThread read_thread_0;

    public int readcount_1 = 0;
    public int iavailable_1 = 0;
    byte[] readData_1;
    char[] readDataToText_1;
    public boolean bReadThreadGoing_1 = false;
    public readThread_1 read_thread_1;

    public int readcount_2 = 0;
    public int iavailable_2 = 0;
    byte[] readData_2;
    char[] readDataToText_2;
    public boolean bReadThreadGoing_2 = false;
    public readThread_2 read_thread_2;

    public int readcount_3 = 0;
    public int iavailable_3 = 0;
    byte[] readData_3;
    char[] readDataToText_3;
    public boolean bReadThreadGoing_3 = false;
    public readThread_3 read_thread_3;

    boolean uart_configured_0 = false;
    boolean uart_configured_1 = false;
    boolean uart_configured_2 = false;
    boolean uart_configured_3 = false;

	// Empty Constructor
	public FT4232HTestFragment()
	{
		Log.e(">>@@","DeviceLoopback2Fragment - Empty Constructor");
	}

	/* Constructor */
	public FT4232HTestFragment(Context parentContext , D2xxManager d2xx_0)
	{
		Log.e(">>@@","DeviceLoopback2Fragment - Constructor");
		DeviceFT4232HTestContext = parentContext;
		ftd2xx_0 = d2xx_0;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 9);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        Log.e(">>@@","onCreateView");


		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.device_ft4232htest, container, false);

		readData_0 = new byte[readLength];
		readDataToText_0 = new char[readLength];

		readData_1 = new byte[readLength];
		readDataToText_1 = new char[readLength];

		readData_2 = new byte[readLength];
		readDataToText_2 = new char[readLength];

		readData_3 = new byte[readLength];
		readDataToText_3 = new char[readLength];


		/* create editable text objects */
		readText_0 = (EditText) view.findViewById(R.id.ReadValues);
		readText_0.setInputType(0);
		writeText_0 = (EditText) view.findViewById(R.id.WriteValues);
		configButton_0 = (Button) view.findViewById(R.id.configButton);
		writeButton_0 = (Button) view.findViewById(R.id.WriteButton);

		readText_1 = (EditText) view.findViewById(R.id.ReadValues1);
		readText_1.setInputType(0);
		writeText_1 = (EditText) view.findViewById(R.id.WriteValues1);
		configButton_1 = (Button) view.findViewById(R.id.configButton1);
		writeButton_1 = (Button) view.findViewById(R.id.WriteButton1);

		readText_2 = (EditText) view.findViewById(R.id.ReadValues2);
		readText_2.setInputType(0);
		writeText_2 = (EditText) view.findViewById(R.id.WriteValues2);
		configButton_2 = (Button) view.findViewById(R.id.configButton2);
		writeButton_2 = (Button) view.findViewById(R.id.WriteButton2);

		readText_3 = (EditText) view.findViewById(R.id.ReadValues3);
		readText_3.setInputType(0);
		writeText_3 = (EditText) view.findViewById(R.id.WriteValues3);
		configButton_3 = (Button) view.findViewById(R.id.configButton3);
		writeButton_3 = (Button) view.findViewById(R.id.WriteButton3);


		/* setup the baud rate list */
		baudSpinner = (Spinner) view.findViewById(R.id.baudRateValue);
		ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter.createFromResource(DeviceFT4232HTestContext, R.array.baud_rate,
						android.R.layout.simple_spinner_item);
		baudAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		baudSpinner.setAdapter(baudAdapter);
		baudSpinner.setGravity(0x10);
		baudSpinner.setSelection(4);
		/* by default it is 9600 */
		baudRate = 9600;

		/* stop bits */
		stopSpinner = (Spinner) view.findViewById(R.id.stopBitValue);
		ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(DeviceFT4232HTestContext, R.array.stop_bits,
						android.R.layout.simple_spinner_item);

		stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stopSpinner.setAdapter(stopAdapter);
		stopSpinner.setGravity(0x01);
		/* default is stop bit 1 */
		stopBit = 1;

		/* data bits */
		dataSpinner = (Spinner) view.findViewById(R.id.dataBitValue);
		ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(DeviceFT4232HTestContext, R.array.data_bits,
						android.R.layout.simple_spinner_item);

		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataSpinner.setAdapter(dataAdapter);
		dataSpinner.setGravity(0x11);
		dataSpinner.setSelection(1);
		/* default data bit is 8 bit */
		dataBit = 8;

		/* parity */
		paritySpinner = (Spinner) view.findViewById(R.id.parityValue);
		ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter.createFromResource(DeviceFT4232HTestContext, R.array.parity,
						android.R.layout.simple_spinner_item);

		parityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		paritySpinner.setAdapter(parityAdapter);
		paritySpinner.setGravity(0x11);
		/* default is none */
		parity = 0;

		/* flow control */
		flowSpinner = (Spinner) view.findViewById(R.id.flowControlValue);
		ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter.createFromResource(DeviceFT4232HTestContext, R.array.flow_control,
						android.R.layout.simple_spinner_item);

		flowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		flowSpinner.setAdapter(flowAdapter);
		flowSpinner.setGravity(0x11);
		/* default flow control is is none */
		flowControl = 0;


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

		configButton_0.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				// configButton.setBackgroundResource(drawable.start);
				Log.e(">>@@","configButton_0 - onClick+");

				if(true == SetConfig(0, baudRate, dataBit, stopBit, parity, flowControl))
				{
			        read_thread_0 = new readThread(handler);
			        read_thread_0.start();
				}
				Log.e(">>@@","configButton_0 - onClick-");
			}

		});

		configButton_1.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				// configButton.setBackgroundResource(drawable.start);
				Log.e(">>@@","configButton_1 - onClick+");

				if(true == SetConfig(1, baudRate, dataBit, stopBit, parity, flowControl))
				{
			        read_thread_1 = new readThread_1(handler);
			        read_thread_1.start();
				}
				Log.e(">>@@","configButton_1 - onClick-");
			}

		});

		configButton_2.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				// configButton.setBackgroundResource(drawable.start);
				Log.e(">>@@","configButton_2 - onClick+");

				if(true == SetConfig(2, baudRate, dataBit, stopBit, parity, flowControl))
				{
			        read_thread_2 = new readThread_2(handler);
			        read_thread_2.start();
				}
				Log.e(">>@@","configButton_2 - onClick-");
			}

		});

		configButton_3.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				// configButton.setBackgroundResource(drawable.start);
				Log.e(">>@@","configButton_3 - onClick+");

				if(true == SetConfig(3, baudRate, dataBit, stopBit, parity, flowControl))
				{
			        read_thread_3 = new readThread_3(handler);
			        read_thread_3.start();
				}
				Log.e(">>@@","configButton_3 - onClick-");
			}

		});


		/* handle write click */
		writeButton_0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.e(">>@@","writeButton_1 - onClick+");
						SendMessage(v);
					Log.e(">>@@","writeButton_1 - onClick-");
			}
		});

		writeButton_1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.e(">>@@","writeButton_1 - onClick+");
						SendMessage_1(v);
					Log.e(">>@@","writeButton_1 - onClick-");
			}
		});

		writeButton_2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.e(">>@@","writeButton_2 - onClick+");
						SendMessage_2(v);
					Log.e(">>@@","writeButton_2 - onClick-");
			}
		});

		writeButton_3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.e(">>@@","writeButton_3 - onClick+");
						SendMessage_3(v);
					Log.e(">>@@","writeButton_3 - onClick-");
			}
		});
		return view;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e(">>@@", "onActivityCreated");

		super.onActivityCreated(savedInstanceState);

		ft_device_0 = ftd2xx_0.openByIndex(DeviceFT4232HTestContext, 0);
		ft_device_1 = ftd2xx_0.openByIndex(DeviceFT4232HTestContext, 1);
		ft_device_2 = ftd2xx_0.openByIndex(DeviceFT4232HTestContext, 2);
		ft_device_3 = ftd2xx_0.openByIndex(DeviceFT4232HTestContext, 3);

		Log.e(">>@@", "onActivityCreated-");
	}

	@Override
	public void onDestroy() {
		Log.e(">>@@","onDestroy");
		bReadThreadGoing_0 = false;
		bReadThreadGoing_1 = false;
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		ft_device_0.close();
		ft_device_1.close();
		ft_device_2.close();
		ft_device_3.close();
		super.onDestroy();
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

			if(parityString.compareTo("odd") == 0)
			{
				parity = 1;
			}

			if(parityString.compareTo("even") == 0)
			{
				parity = 2;
			}

			if(parityString.compareTo("mark") == 0)
			{
				parity = 3;
			}

			if(parityString.compareTo("space") == 0)
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

			if(flowString.compareTo("CTS/RTS")==0)
			{
				flowControl = 1;
			}
		}

		public void onNothingSelected(AdapterView<?> parent)
		{}
    }

 	public boolean SetConfig(int index, int baud, byte dataBits, byte stopBits,
 						 byte parity, byte flowControl)
 {
		FT_Device ftDev;

		switch (index) {
		case 0:
			ftDev = ft_device_0;
			break;
		case 1:
			ftDev = ft_device_1;
			break;
		case 2:
			ftDev = ft_device_2;
			break;
		case 3:
			ftDev = ft_device_3;
			break;
		default:
			ftDev = ft_device_0;
			break;
		}

		if (ftDev.isOpen() == false) {
			Log.e(">>@@", "SetConfig: ftDev not open!!!!!!  index:" + index);
		} else {
			Log.e(">>@@", "SetConfig: ftDev open, index:" + index);

		}
		// configure our port
		// reset to UART mode for 232 devices
		ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		// set 230400 baud rate
		// ftdid2xx.setBaudRate(9600 );
		ftDev.setBaudRate(baud);

		// set 8 data bits, 1 stop bit, no parity
		// ftdid2xx.setDataCharacteristics(D2xx.FT_DATA_BITS_8,
		// D2xx.FT_STOP_BITS_1, D2xx.FT_PARITY_NONE);
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

		// TODO : check xon, xoff
		// TODO : check xon, xoff
		ftDev.setFlowControl(flowCtrlSetting, (byte) 0x00, (byte) 0x00);

		switch (index) {
		case 0:
			uart_configured_0 = true;
			Toast.makeText(DeviceFT4232HTestContext, "Port 0 config done",
					Toast.LENGTH_SHORT).show();
			break;
		case 1:
			uart_configured_1 = true;
			Toast.makeText(DeviceFT4232HTestContext, "Port 1 config done",
					Toast.LENGTH_SHORT).show();
			break;
		case 2:
			uart_configured_2 = true;
			Toast.makeText(DeviceFT4232HTestContext, "Port 2 config done",
					Toast.LENGTH_SHORT).show();
			break;
		case 3:
			uart_configured_3 = true;
			Toast.makeText(DeviceFT4232HTestContext, "Port 3 config done",
					Toast.LENGTH_SHORT).show();
			break;
		default:
			ftDev = ft_device_0;
			break;
		}


 		Log.e(">>@@","SCon[" + index
 				+ "] 0:0x" + Integer.toHexString(ft_device_0.getModemStatus())
 				+ " 1:0x" + Integer.toHexString(ft_device_1.getModemStatus())
 				+ " 2:0x" + Integer.toHexString(ft_device_2.getModemStatus())
 				+ " 3:0x" + Integer.toHexString(ft_device_3.getModemStatus())
 				);

		return true;
	}

    /** Called when the user clicks the Send button */
    public void SendMessage(View view)
    {
        // Do something in response to button
 		FT_Device ftDev;
    	if( uart_configured_0 == false )
    	{
    		Toast.makeText(DeviceFT4232HTestContext, "UART Port 0 not configured yet...", Toast.LENGTH_SHORT).show();
    		return;
    	}
	    ftDev = ft_device_0;

		if(ftDev.isOpen() == false)
		{
			Log.e(">>@@","SendMessage: ftDev not open!!!!!!  index: 0");
		}
		else
		{
			Log.e(">>@@","SendMessage: ftDev open, index: 0");


/*        	Log.e(">>@@","port 0 isn:" + ftDev.deviceInfoNode.iSerialNumber
                	+ " bcd:" + ftDev.deviceInfoNode.bcdDevice
                	+ " id:" + ftDev.deviceInfoNode.id
                	+ " loc:" + ftDev.deviceInfoNode.location
                	+ " sn:" + ftDev.deviceInfoNode.serialNumber);
*/		}

		ftDev.setLatencyTimer((byte) 16);

		// ftDev.Purge(true, true);

		String writeData = writeText_0.getText().toString();
		byte[] OutData = writeData.getBytes();
		int iLen = ftDev.write(OutData, writeData.length());
		Log.e(">>@@", "Port 0 write Len:" + iLen + " s:" + writeData);
		Toast.makeText(DeviceFT4232HTestContext,
				"Port 0 write Len:" + iLen + " s:" + writeData,
				Toast.LENGTH_SHORT).show();
    }

    /** Called when the user clicks the Send button */
    public void SendMessage_1(View view)
    {
        // Do something in response to button
 		FT_Device ftDev;
    	if( uart_configured_1 == false )
    	{
    		Toast.makeText(DeviceFT4232HTestContext, "UART Port 1 not configured yet...", Toast.LENGTH_SHORT).show();
    		return;
    	}
	    ftDev = ft_device_1;

		if(ftDev.isOpen() == false)
		{
			Log.e(">>@@","SendMessage: ftDev not open!!!!!!  index: 1");
		}
		else
		{
			Log.e(">>@@","SendMessage: ftDev open, index: 1");


/*        	Log.e(">>@@","port 1 isn:" + ftDev.deviceInfoNode.iSerialNumber
                	+ " bcd:" + ftDev.deviceInfoNode.bcdDevice
                	+ " id:" + ftDev.deviceInfoNode.id
                	+ " loc:" + ftDev.deviceInfoNode.location
                	+ " sn:" + ftDev.deviceInfoNode.serialNumber);
*/		}

		ftDev.setLatencyTimer((byte) 16);

		// ftDev.Purge(true, true);

		String writeData = writeText_1.getText().toString();
		byte[] OutData = writeData.getBytes();
		int iLen = ftDev.write(OutData, writeData.length());
		Log.e(">>@@", "Port 1 write Len:" + iLen + " s:" + writeData);
		Toast.makeText(DeviceFT4232HTestContext,
				"Port 1 write Len:" + iLen + " s:" + writeData,
				Toast.LENGTH_SHORT).show();
    }

    /** Called when the user clicks the Send button */
    public void SendMessage_2(View view)
    {
        // Do something in response to button
 		FT_Device ftDev;
    	if( uart_configured_2 == false )
    	{
    		Toast.makeText(DeviceFT4232HTestContext, "UART Port 2 not configured yet...", Toast.LENGTH_SHORT).show();
    		return;
    	}
	    ftDev = ft_device_2;

		if(ftDev.isOpen() == false)
		{
			Log.e(">>@@","SendMessage: ftDev not open!!!!!!  index: 2");
		}
		else
		{
			Log.e(">>@@","SendMessage: ftDev open, index: 2");


/*        	Log.e(">>@@","port 2 isn:" + ftDev.deviceInfoNode.iSerialNumber
                	+ " bcd:" + ftDev.deviceInfoNode.bcdDevice
                	+ " id:" + ftDev.deviceInfoNode.id
                	+ " loc:" + ftDev.deviceInfoNode.location
                	+ " sn:" + ftDev.deviceInfoNode.serialNumber);
*/		}

		ftDev.setLatencyTimer((byte) 16);

		// ftDev.Purge(true, true);

		String writeData = writeText_2.getText().toString();
		byte[] OutData = writeData.getBytes();
		int iLen = ftDev.write(OutData, writeData.length());
		Log.e(">>@@", "Port 2 write Len:" + iLen + " s:" + writeData);
		Toast.makeText(DeviceFT4232HTestContext,
				"Port 2 write Len:" + iLen + " s:" + writeData,
				Toast.LENGTH_SHORT).show();
    }

    /** Called when the user clicks the Send button */
    public void SendMessage_3(View view)
    {
        // Do something in response to button
 		FT_Device ftDev;
    	if( uart_configured_3 == false )
    	{
    		Toast.makeText(DeviceFT4232HTestContext, "UART Port 3 not configured yet...", Toast.LENGTH_SHORT).show();
    		return;
    	}
	    ftDev = ft_device_3;

		if(ftDev.isOpen() == false)
		{
			Log.e(">>@@","SendMessage: ftDev not open!!!!!!  index: 3");
		}
		else
		{
			Log.e(">>@@","SendMessage: ftDev open, index: 3");


/*        	Log.e(">>@@","port 3 isn:" + ftDev.deviceInfoNode.iSerialNumber
                	+ " bcd:" + ftDev.deviceInfoNode.bcdDevice
                	+ " id:" + ftDev.deviceInfoNode.id
                	+ " loc:" + ftDev.deviceInfoNode.location
                	+ " sn:" + ftDev.deviceInfoNode.serialNumber);
*/		}

		ftDev.setLatencyTimer((byte) 16);

		// ftDev.Purge(true, true);

		String writeData = writeText_3.getText().toString();
		byte[] OutData = writeData.getBytes();
		int iLen = ftDev.write(OutData, writeData.length());
		Log.e(">>@@", "Port 3 write Len:" + iLen + " s:" + writeData);
		Toast.makeText(DeviceFT4232HTestContext,
				"Port 3 write Len:" + iLen + " s:" + writeData,
				Toast.LENGTH_SHORT).show();
    }


	final Handler handler =  new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		Log.e(">>@@","handleMessage+ v0930");

    		if(msg.what == 0)
    		{
    			Log.e(">>@@","handleMessage-0");
	    		if(iavailable_0 > 0)
	    		{
	    			Log.e(">>@@","handle 0 iava:"+ iavailable_0 + " Data:" + String.copyValueOf(readDataToText_0, 0, iavailable_0));
	    			readText_0.append(String.copyValueOf(readDataToText_0, 0, iavailable_0));
	    			Toast.makeText(DeviceFT4232HTestContext, "Port 0 get data:" + String.copyValueOf(readDataToText_0, 0, iavailable_0), Toast.LENGTH_SHORT).show();
	    		}
    		}
    		else if(msg.what == 1)
    		{
    			Log.e(">>@@","handleMessage-1");
    			if(iavailable_1 > 0)
	    		{
	    			Log.e(">>@@","handle 1 iava:"+ iavailable_1 + " Data:" + String.copyValueOf(readDataToText_1, 0, iavailable_1));
	    			readText_1.append(String.copyValueOf(readDataToText_1, 0, iavailable_1));
	    			Toast.makeText(DeviceFT4232HTestContext, "Port 1 get data:" + String.copyValueOf(readDataToText_1, 0, iavailable_1), Toast.LENGTH_SHORT).show();
	    		}
    		}
    		else if(msg.what == 2)
    		{
    			Log.e(">>@@","handleMessage-2");
    			if(iavailable_2 > 0)
	    		{
	    			Log.e(">>@@","handle 2 iava:"+ iavailable_2 + " Data:" + String.copyValueOf(readDataToText_2, 0, iavailable_2));
	    			readText_2.append(String.copyValueOf(readDataToText_2, 0, iavailable_2));
	    			Toast.makeText(DeviceFT4232HTestContext, "Port 2 get data:" + String.copyValueOf(readDataToText_2, 0, iavailable_2), Toast.LENGTH_SHORT).show();
	    		}
    		}
    		else if(msg.what == 3)
    		{
    			Log.e(">>@@","handleMessage-3");
    			if(iavailable_3 > 0)
	    		{
	    			Log.e(">>@@","handle 3 iava:"+ iavailable_3 + " Data:" + String.copyValueOf(readDataToText_3, 0, iavailable_3));
	    			readText_3.append(String.copyValueOf(readDataToText_3, 0, iavailable_3));
	    			Toast.makeText(DeviceFT4232HTestContext, "Port 3 get data:" + String.copyValueOf(readDataToText_3, 0, iavailable_3), Toast.LENGTH_SHORT).show();
	    		}
    		}
    		else
    		{
    			Log.e(">>@@","handleMessage-other");
    		}

    		Log.e(">>@@","handleMessage-");
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
			int i, iLen;
			bReadThreadGoing_0 = true;

			Log.e(">>@@","readThread 0 +");

			while(true == bReadThreadGoing_0)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}

				//Message msg = mHandler.obtainMessage();

				// readData = new byte[readLength];
				iavailable_0 = ft_device_0.getQueueStatus();
				if (iavailable_0 > 0) {
					iLen = ft_device_0.read(readData_0, iavailable_0);
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_RX));
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_TX));
					for (i = 0; i < iavailable_0; i++) {
						readDataToText_0[i] = (char) readData_0[i];
					}
					Log.e(">>@@", "read avai:" + iavailable_0 + " iLen:" + iLen
							+ " readDataToText[0]:" + readDataToText_0[0]);
					Message msg = mHandler.obtainMessage(0);
					mHandler.sendMessage(msg);
				}
			}

			Log.e(">>@@","readThread 0 -");
		}
	}

	private class readThread_1  extends Thread
	{
		Handler mHandler;

		readThread_1(Handler h){
			mHandler = h;
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run()
		{
			int i;
			bReadThreadGoing_1 = true;

			Log.e(">>@@","readThread 1 +");

			while(true == bReadThreadGoing_1)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}

				//Message msg = mHandler.obtainMessage();

				// readData = new byte[readLength];
				iavailable_1 = ft_device_1.getQueueStatus();
				if (iavailable_1 > 0) {
					ft_device_1.read(readData_1, iavailable_1);
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_RX));
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_TX));
					for (i = 0; i < iavailable_1; i++) {
						readDataToText_1[i] = (char) readData_1[i];
					}
					Log.e(">>@@", "read avai:" + iavailable_1
							+ " readDataToText[0]:" + readDataToText_1[0]);
					Message msg = mHandler.obtainMessage(1);
					mHandler.sendMessage(msg);
				}
			}

			Log.e(">>@@","readThread 1 -");
		}
	}

	private class readThread_2  extends Thread
	{
		Handler mHandler;

		readThread_2(Handler h){
			mHandler = h;
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run()
		{
			int i;
			bReadThreadGoing_2 = true;

			Log.e(">>@@","readThread 2 +");

			while(true == bReadThreadGoing_2)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}

				//Message msg = mHandler.obtainMessage();

				// readData = new byte[readLength];
				iavailable_2 = ft_device_2.getQueueStatus();
				if (iavailable_2 > 0) {
					ft_device_2.read(readData_2, iavailable_2);
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_RX));
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_TX));
					for (i = 0; i < iavailable_2; i++) {
						readDataToText_2[i] = (char) readData_1[i];
					}
					Log.e(">>@@", "read avai:" + iavailable_2
							+ " readDataToText[0]:" + readDataToText_2[0]);
					Message msg = mHandler.obtainMessage(2);
					mHandler.sendMessage(msg);
				}
			}

			Log.e(">>@@","readThread 2 -");
		}
	}

	private class readThread_3  extends Thread
	{
		Handler mHandler;

		readThread_3(Handler h){
			mHandler = h;
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run()
		{
			int i;
			bReadThreadGoing_3 = true;

			Log.e(">>@@","readThread 3 +");

			while(true == bReadThreadGoing_3)
			{
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}

				//Message msg = mHandler.obtainMessage();

				// readData = new byte[readLength];
				iavailable_3 = ft_device_3.getQueueStatus();
				if (iavailable_3 > 0) {
					ft_device_3.read(readData_3, iavailable_3);
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_RX));
					// ftdid2xx.purge((byte) (D2xx.FT_PURGE_TX));
					for (i = 0; i < iavailable_3; i++) {
						readDataToText_3[i] = (char) readData_3[i];
					}
					Log.e(">>@@", "read avai:" + iavailable_3
							+ " readDataToText[0]:" + readDataToText_3[0]);
					Message msg = mHandler.obtainMessage(3);
					mHandler.sendMessage(msg);
				}
			}

			Log.e(">>@@","readThread 3 -");
		}
	}
}

