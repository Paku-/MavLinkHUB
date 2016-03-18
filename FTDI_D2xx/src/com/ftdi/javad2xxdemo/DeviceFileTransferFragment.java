package com.ftdi.javad2xxdemo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
import com.ftdi.j2xx.D2xxManager.D2xxException;


public class DeviceFileTransferFragment extends Fragment{

	// original ///////////////////////////////
	static Context DeviceFileTransferContext;
	D2xxManager ftdid2xx = null;
	FT_Device ftDev = null;
	int DevCount = -1;

    /*graphical objects*/
    EditText TimeText;
    Spinner baudSpinner;;
    Spinner stopSpinner;
    Spinner dataSpinner;
    Spinner paritySpinner;
    Spinner flowSpinner;


    Button configButton;
    Button savefileButton;
    Button sendfileButton1;

    int baudRate; /*baud rate*/
    byte stopBit; /*1:1stop bits, 2:2 stop bits*/
    byte dataBit; /*8:8bit, 7: 7bit*/
    byte parity;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    byte flowControl; /*0:none, 1: flow control(CTS,RTS)*/
    
    private static final String FILE_NAME = "SavedFile.txt";
    private static final String FILE_FOLDER = "j2xx";
    private static final String ACCESS_FILE = android.os.Environment.getExternalStorageDirectory()+ 
	        		java.io.File.separator  +FILE_FOLDER + java.io.File.separator + FILE_NAME;
    public FileInputStream fis_open;
    public FileOutputStream fos_save;
    public BufferedOutputStream buf_save;

	public save_file_thread save_file_Thread;
	public send_file_thread send_file_Thread;

    boolean WriteFileThread_start = false;

    public static final int readLength = 4096;
    public int readcount = 0;
    public int iavailable = 0;
    byte[] readData;
    //char[] readDataToText;
    public boolean bReadThreadGoing = false;

    public int iWriteSleepTime = 300;

    public long start_time, end_time;
	public long cal_time_1, cal_time_2;
	int iFileSize = 0;
	int sendByteCount = 0;

    boolean uart_configured = false;


	// Empty Constructor
	public DeviceFileTransferFragment()
	{
	}

	/* Constructor */
	public DeviceFileTransferFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		DeviceFileTransferContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 6);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

		super.onCreate(savedInstanceState);

		View view = inflater.inflate(R.layout.device_filetransfer, container, false);

		readData = new byte[readLength];

		TimeText = (EditText)view.findViewById(R.id.TimeValues);
        TimeText.setInputType(0);

		configButton = (Button)view.findViewById(R.id.configButton);
        savefileButton = (Button)view.findViewById(R.id.SaveFileButton);
        sendfileButton1 = (Button)view.findViewById(R.id.SendFileButton1);

		/* setup the baud rate list */
		baudSpinner = (Spinner) view.findViewById(R.id.baudRateValue);
		ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter.createFromResource(DeviceFileTransferContext, R.array.baud_rate,
						R.layout.my_spinner_textview);
		baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		baudSpinner.setAdapter(baudAdapter);
		baudSpinner.setGravity(0x10);
		baudSpinner.setSelection(4);
		/* by default it is 9600 */
		baudRate = 9600;

		/* stop bits */
		stopSpinner = (Spinner) view.findViewById(R.id.stopBitValue);
		ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(DeviceFileTransferContext, R.array.stop_bits,
						R.layout.my_spinner_textview);
		stopAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		stopSpinner.setAdapter(stopAdapter);
		stopSpinner.setGravity(0x01);
		/* default is stop bit 1 */
		stopBit = 1;

		/* data bits */
		dataSpinner = (Spinner) view.findViewById(R.id.dataBitValue);
		ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(DeviceFileTransferContext, R.array.data_bits,
						R.layout.my_spinner_textview);
		dataAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		dataSpinner.setAdapter(dataAdapter);
		dataSpinner.setGravity(0x11);
		dataSpinner.setSelection(1);
		/* default data bit is 8 bit */
		dataBit = 8;

		/* parity */
		paritySpinner = (Spinner) view.findViewById(R.id.parityValue);
		ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter.createFromResource(DeviceFileTransferContext, R.array.parity,
						R.layout.my_spinner_textview);
		parityAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		paritySpinner.setAdapter(parityAdapter);
		paritySpinner.setGravity(0x11);
		/* default is none */
		parity = 0;

		/* flow control */
		flowSpinner = (Spinner) view.findViewById(R.id.flowControlValue);
		ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter.createFromResource(DeviceFileTransferContext, R.array.flow_control,
						R.layout.my_spinner_textview);
		flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
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
		
		configButton.setOnClickListener(new View.OnClickListener() {
			// @Override
			public void onClick(View v) {
				if(DevCount <= 0)
					ConnectFunction();

				if(DevCount > 0)
					SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
			}

		});

//////////////////save file button + //////////////////
		/*handle write click*/
		savefileButton.setOnClickListener(new View.OnClickListener() {

			//@Override
			public void onClick(View v) {
				
				if(ftDev == null)
				{
					Toast.makeText(DeviceFileTransferContext,"Device is not opened...",Toast.LENGTH_SHORT).show();
					return;
				}
				
		    	if( uart_configured == false || DevCount <= 0)
		    	{
		    		Toast.makeText(DeviceFileTransferContext, "UART not configured yet...", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	else
		    	{
					if(false == WriteFileThread_start)
					{
						TimeText.setText("");
						Toast.makeText(DeviceFileTransferContext, "Prepare to save data to file ...", Toast.LENGTH_SHORT).show();

						if(false == createDirIfNotExists(FILE_FOLDER))
						{
							Toast.makeText(DeviceFileTransferContext, "Create folder:" + FILE_FOLDER + " fail!", Toast.LENGTH_SHORT).show();
							return;
						}

				        try
				        {
				        	fos_save = new FileOutputStream(ACCESS_FILE);
						buf_save =  new BufferedOutputStream(fos_save);
					}
				        catch (FileNotFoundException e)
				        {e.printStackTrace();}

						WriteFileThread_start = true;

						save_file_Thread = new save_file_thread(handler, buf_save);
						save_file_Thread.start();
					}
					else
					{
						WriteFileThread_start = false;
						try
						{
							//fos_save.flush();
							buf_save.flush();
							buf_save.close();
							fos_save.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
		    	}
			}
		});
//////////////////save file button - //////////////////

//////////////////send file button + //////////////////
		sendfileButton1.setOnClickListener(new View.OnClickListener()
		{
			//@Override
			public void onClick(View v)
			{
				File file = new File(ACCESS_FILE);
				
				if(ftDev == null)
				{
					Toast.makeText(DeviceFileTransferContext,"Device is not opened...",Toast.LENGTH_SHORT).show();
					return;
				}				
				
		    	if( uart_configured == false || DevCount <= 0)
		    	{
		    		Toast.makeText(DeviceFileTransferContext, "UART not configured yet...", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	else if(false == file.exists())
		    	{
		    		Toast.makeText(DeviceFileTransferContext, "No file for sending...", Toast.LENGTH_SHORT).show();
		    		return;		    		
		    	}
		    	else
				{
					Toast.makeText(DeviceFileTransferContext, "Start sending SavedFile.txt...", Toast.LENGTH_SHORT).show();

			        try {
			        	iFileSize = Integer.parseInt(String.valueOf(file.length()));
						fis_open = new FileInputStream(ACCESS_FILE);	
					} catch (FileNotFoundException e) {e.printStackTrace();}

			        TimeText.setText("Testing...");
					//uartInterface.StartWriteFileThread();
			        send_file_Thread = new send_file_thread(handler, fis_open);
			        send_file_Thread.start();
				}
			}
		});
//////////////////send file button - //////////////////

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

		DevCount = ftdid2xx.createDeviceInfoList(DeviceFileTransferContext);
		if (DevCount > 0) {
			ftDev = ftdid2xx.openByIndex(DeviceFileTransferContext, openIndex);
			
			if(ftDev == null)
			{
				Toast.makeText(DeviceFileTransferContext,"ftDev == null",Toast.LENGTH_LONG).show();
				return;
			}			
			
			if (true == ftDev.isOpen()) {
				Toast.makeText(DeviceFileTransferContext,
						"devCount:" + DevCount + " open index:" + openIndex,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(DeviceFileTransferContext,
						"Need to get permission!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Log.e("j2xx", "DevCount <= 0");
		}
	}

	@Override
	public void onStop() {
		bReadThreadGoing = false;
		try {
			Thread.sleep(50);
		}
		catch (InterruptedException e) {
		}
		
		if(ftDev != null && true == ftDev.isOpen())
		{
			ftDev.close();
		}
		super.onStop();
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
	
 	public void SetConfig(int baud, byte dataBits, byte stopBits,
 						 byte parity, byte flowControl)
 {
		// configure our port
		// reset to UART mode for 232 devices
		ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		ftDev.setBaudRate(baud);

		// TODO: remove this workaround when write queue is ok
		switch (baud) {
		case 300:
			iWriteSleepTime = 4400;
			break;
		case 600:
			iWriteSleepTime = 2200;
			break;
		case 1200:
			iWriteSleepTime = 1100;
			break;
		case 4800:
			iWriteSleepTime = 285;
			break;
		case 9600:
			iWriteSleepTime = 140;
			break;
		case 19200:
			iWriteSleepTime = 70;
			break;
		case 38400:
			iWriteSleepTime = 34;
			break;
		case 57600:
			iWriteSleepTime = 23;
			break;
		case 115200:
			iWriteSleepTime = 11;
			break;
		case 230400:
			iWriteSleepTime = 5;
			break;
		case 460800:
			iWriteSleepTime = 4;
			break;
		case 921600:
			iWriteSleepTime = 2;
			break;
		default:
			iWriteSleepTime = 140;
			break;
		}

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
		ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0c);
		
		uart_configured = true;
		Toast.makeText(DeviceFileTransferContext, "Config done",
				Toast.LENGTH_SHORT).show();
	}

 	public static boolean createDirIfNotExists(String path) {
 	    boolean ret = true;

 	    File file = new File( android.os.Environment.getExternalStorageDirectory(), path);
 	    if (!file.exists())
 	    {
 	        if (!file.mkdirs())
 	        {
 	            Log.e("j2xx", "Create folder:" + path + " fail!");
 	            ret = false;
 	        }
 	    }
 	    return ret;
 	}
 	
	final Handler handler =  new Handler()
    {
    	@Override
    	public void handleMessage(Message msg)
    	{
    		if(msg.what == 0)
    		{
    			String temp;
    			if(sendByteCount <= 10240)
    				temp = "Get:" + sendByteCount + "Bytes";
    			else
    				temp = "Get:" + (double)(sendByteCount/1024) + "KBytes";				
				TimeText.setText(temp);   		
    		}
    		else if(msg.what == 1)
    		{
    			String temp;
    			if(sendByteCount <= 10240)
    				temp = "Get:" + sendByteCount + "Bytes";
    			else
    				temp = "Get:" + (double)(sendByteCount/1024) + "KBytes";
    			    			
	    		Double diffime = (double)(end_time-start_time)/1000;
	    		temp = temp + " in " + diffime.toString() + "sec";
    			TimeText.setText(temp);
	    		Toast.makeText(DeviceFileTransferContext, "Stop read data", Toast.LENGTH_SHORT).show();
    		}
    		else if(msg.what == 2)
    		{
    			String temp;
    			if(iFileSize == 0)
    			{
    				TimeText.setText("The saved file is 0 byte.");
    			}
    			else if(iFileSize < 100)
    			{
    				temp = "Send:" + sendByteCount + "Bytes("+(sendByteCount*100/iFileSize)+"%)";
    				TimeText.setText(temp);
    			}
    			else
    			{
	    			if(sendByteCount <= 10240)
	    				temp = "Send:" + sendByteCount + "Bytes("+(sendByteCount/(iFileSize/100))+"%)";
	    			else
	    				temp = "Send:" + (double)(sendByteCount/1024) + "KBytes("+(sendByteCount/(iFileSize/100))+"%)";
					//String tmp = temp.replace("\\n", "\n");
					TimeText.setText(temp);
    			}
					
    		}
//    		else if(msg.what == 3)
//    		{
//	    		Double difftime = (double)(end_time-start_time)/1000;
//	    		String temp;
//    			if(iFileSize <= 10240)
//    				temp = "Send " + iFileSize + "Bytes in " + difftime.toString() + " sec";
//    			else
//    				temp = "Send " + (double)(iFileSize/1024) + "KBytes in " + difftime.toString() + " sec";
//    				
//    			temp = temp + "\nPerformance:" + (double)((iFileSize/1024)/difftime) + "KB/sec";
//    			String tmp = temp.replace("\\n", "\n");
//    			TimeText.setText(tmp);
//	    		//TimeText.append(difftime.toString());
//	    		Toast.makeText(DeviceFileTransferContext, "Send file done", Toast.LENGTH_SHORT).show();
//    		}
    	}
    };

	// receive data and save data to file
	private class save_file_thread  extends Thread
	{
		Handler mHandler;
		BufferedOutputStream outstream;

		save_file_thread(Handler h, BufferedOutputStream stream){
			mHandler = h;
			outstream = stream;
			this.setPriority(Thread.MAX_PRIORITY);
		}

		@Override
		public void run()
		{
			try
			{
				sendByteCount = 0;				

				ftDev.setLatencyTimer((byte)16);
				// ftDev.setReadTimeout(1000);
				ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

				start_time = System.currentTimeMillis();

				cal_time_1 = System.currentTimeMillis();
				while(true == WriteFileThread_start)
				{
					iavailable = ftDev.getQueueStatus();
					if(iavailable > 0)
					{	
						if(iavailable > 4096)
							iavailable = 4096;
						ftDev.read(readData,iavailable);
		            	outstream.write(readData, 0, iavailable);
		            	sendByteCount += iavailable;

		            	cal_time_2 = System.currentTimeMillis();			            	
						if(((cal_time_2 - cal_time_1)/1000) >= 2) // update progress every 2 seconds
						{
							Message msg = mHandler.obtainMessage(0);
							mHandler.sendMessage(msg);
							cal_time_1 = cal_time_2;
						}
						
		            	////////////////////////////////////////////
		            	iavailable = ftDev.getQueueStatus();
		            	while(iavailable > 0)
						{
							if(iavailable > 4096)
								iavailable = 4096;
							ftDev.read(readData,iavailable);
			            	outstream.write(readData, 0, iavailable);

			            	sendByteCount += iavailable;
			            	cal_time_2 = System.currentTimeMillis();			            	
							if(((cal_time_2 - cal_time_1)/1000) >= 2) // update progress every 2 seconds
							{
								Message msg = mHandler.obtainMessage(0);
								mHandler.sendMessage(msg);
								cal_time_1 = cal_time_2;
							}
							
			            	iavailable = ftDev.getQueueStatus();
						}
					}
				}

				end_time = System.currentTimeMillis();
				Message msg = mHandler.obtainMessage(1);
            	mHandler.sendMessage(msg);
			}
	    	catch (D2xxException e)
	    	{
	    		Log.e("j2xx","save_file_thread:" + e.getMessage());
	    	}
			catch (IOException e){}
		}
	}

	// read data from file and send data
	private class send_file_thread  extends Thread
	{
		Handler mHandler;
		FileInputStream instream;

		send_file_thread(Handler h, FileInputStream stream ){
			mHandler = h;
			instream = stream;
			this.setPriority(Thread.MAX_PRIORITY);
		}

		@Override
		public void run()
		{
			try
			{
				sendByteCount = 0;
				if(instream != null)
				{
					
					start_time = System.currentTimeMillis();
					cal_time_1 = System.currentTimeMillis();

					ftDev.setLatencyTimer((byte)16);
					ftDev.purge((byte) (D2xxManager.FT_PURGE_TX | D2xxManager.FT_PURGE_RX));

					readcount = instream.read(readData,0,readLength);
					while(readcount > 0)
					{
						try{Thread.sleep(iWriteSleepTime);}
						catch (InterruptedException e) {e.printStackTrace();}

						ftDev.write(readData, readcount);
						sendByteCount += readcount;
						
						cal_time_2 = System.currentTimeMillis();
						if(((cal_time_2 - cal_time_1)/1000) >= 2) // update progress every 5 seconds
						{
							Message msg = mHandler.obtainMessage(2);
							mHandler.sendMessage(msg);
							cal_time_1 = cal_time_2;
						}
						
						readcount = instream.read(readData,0,readLength);
					}
					end_time = System.currentTimeMillis();
					Message msg = mHandler.obtainMessage(2);
					mHandler.sendMessage(msg);
				}
			}
	    	catch (D2xxException e)
	    	{
	    		Log.e("jx22","send_file_thread:" + e.getMessage());
	    	}
			catch (IOException e){}
		}
	}
}
