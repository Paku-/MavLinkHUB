package com.ftdi.javad2xxdemo;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
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

public class OpenDeviceFragment extends Fragment {
	Context OpenDeviceFragmentContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;
	D2xxManager.DriverParameters d2xxDrvParameter;
	
	TextView ErrorInformation;
	TextView txtOpenIndex;
	TextView txtOpenSn;
	TextView txtOpenDesc;
	TextView txtOpenLocation;

	Button btnStart;
	Button btnReset;
	
	Button btnOpenDevParam;
	Button btnGetParam;
	EditText bufnumValue;
	EditText bufsizeValue;
	EditText transizeValue;
	EditText readtimeValue;
	TextView txtOpenIndex2;
	TextView txtOpenSn2;
	TextView txtOpenDesc2;
	TextView txtOpenLocation2;
	TextView txtDesc;
	TextView txtOpenUsbDev1;
	TextView txtOpenUsbDev2;
	
	UsbDevice usbDev = null;
	
	// Empty Constructor
	public OpenDeviceFragment()
	{
	}

	/* Constructor */
	public OpenDeviceFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		OpenDeviceFragmentContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) 
    {
        if (container == null) {
            return null;
        }
        d2xxDrvParameter = new D2xxManager.DriverParameters();
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_opendevice , container, false);

        btnStart = (Button)view.findViewById(R.id.device_misc_start);
        btnReset = (Button)view.findViewById(R.id.device_reset);

        txtOpenIndex = (TextView)view.findViewById(R.id.device_misc_open_index);
        txtOpenSn = (TextView)view.findViewById(R.id.device_misc_open_sn);
        txtOpenDesc = (TextView)view.findViewById(R.id.device_misc_open_description);
        txtOpenLocation = (TextView)view.findViewById(R.id.device_misc_open_location);
        ErrorInformation = (TextView)view.findViewById(R.id.ErrorInformation);

        btnOpenDevParam = (Button)view.findViewById(R.id.opendevice_param);
        btnGetParam = (Button)view.findViewById(R.id.get_param);
        
    	bufnumValue = (EditText)view.findViewById(R.id.bufnumValue);
    	bufsizeValue = (EditText)view.findViewById(R.id.bufsizeValue);
    	transizeValue = (EditText)view.findViewById(R.id.transizeValue);
    	readtimeValue = (EditText)view.findViewById(R.id.readtimeValue);
    	
    	txtOpenIndex2 = (TextView)view.findViewById(R.id.device_misc_open_index_2);
    	txtOpenSn2 = (TextView)view.findViewById(R.id.device_misc_open_sn_2);
    	txtOpenDesc2 = (TextView)view.findViewById(R.id.device_misc_open_description_2);
    	txtOpenLocation2 = (TextView)view.findViewById(R.id.device_misc_open_location_2);
    	txtDesc = (TextView)view.findViewById(R.id.openusbdevdesc);
    	txtOpenUsbDev1 = (TextView)view.findViewById(R.id.device_misc_open_usbdevice_1);
    	txtOpenUsbDev2 = (TextView)view.findViewById(R.id.device_misc_open_usbdevice_2);

        btnStart.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	startOpenDev();
            }
        });

        btnReset.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	resetClick();
            }
        });
        
        btnOpenDevParam.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	if(true == setParameter())
            	{
            		startOpenDevParam();
				}
            }
        });
        
        btnGetParam.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	getParameter();
            }
        });
        
    	return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void resetClick()
    {
		int devCount = 0;
		
		devCount = ftdid2xx.createDeviceInfoList(OpenDeviceFragmentContext);
		Log.i("Misc Function Test ",
				"Device number = " + Integer.toString(devCount));

		if (devCount > 0) {
			ftDevice = ftdid2xx.openByIndex(OpenDeviceFragmentContext, 0);
			ftDevice.resetDevice();
			ftDevice.close();
		}
		
		txtOpenIndex.setText("Open By Index:");
		txtOpenSn.setText("Open By Serial Number:");
		txtOpenLocation.setText("Open By Location:");
		txtOpenDesc.setText("Open By Description:");
		bufnumValue.setText("");
		bufsizeValue.setText("");
		transizeValue.setText("");
		readtimeValue.setText("");
		txtOpenIndex2.setText("Open By Index:");
		txtOpenSn2.setText("Open By Serial Number:");
		txtOpenLocation2.setText("Open By Location:");
		txtOpenDesc2.setText("Open By Description:");
		txtOpenUsbDev1.setText("Open By UsbDevice:");
		txtOpenUsbDev2.setText("Open By UsbDevice with Parameter:");		
    }
    
	public void startOpenDev()
	{
		int devCount = 0;
		
		devCount = ftdid2xx.createDeviceInfoList(OpenDeviceFragmentContext);
		Log.i("Misc Function Test ",
				"Device number = " + Integer.toString(devCount));

		if (devCount > 0) 
		{			
			D2xxManager.FtDeviceInfoListNode deviceList = ftdid2xx.getDeviceInfoListDetail(0);

			// openByIndex
			ftDevice = ftdid2xx.openByIndex(OpenDeviceFragmentContext, 0, d2xxDrvParameter);
			if (ftDevice.isOpen()) {
				txtOpenIndex.setText("Open By Index: Pass");
			} 
			else {
				txtOpenIndex.setText("Open By Index: Fail");
			}
			ftDevice.close();
			
			// openBySerialNumber
			if (deviceList.serialNumber != null) {
				ftDevice = ftdid2xx.openBySerialNumber(OpenDeviceFragmentContext, deviceList.serialNumber);
				if (ftDevice.isOpen()) {
					txtOpenSn.setText("Open By Serial Number: Pass");
				} else {
					txtOpenSn.setText("Open By Serial Number: Fail");
				}
				ftDevice.close();
			} else {
				txtOpenSn.setText("Open By Serial Number: Skip(No serial number)");
			}

			
			// openByLocation
			ftDevice = ftdid2xx.openByLocation(OpenDeviceFragmentContext, deviceList.location);
			if (ftDevice.isOpen()) {
				txtOpenLocation.setText("Open By Location: Pass");
			} else {
				txtOpenLocation.setText("Open By Location: Fail");
			}
			ftDevice.close();

			
			// openByDescription
			if (deviceList.description != null){
				ftDevice = ftdid2xx.openByDescription(OpenDeviceFragmentContext, deviceList.description);
				if (ftDevice.isOpen()) {
					txtOpenDesc.setText("Open By Description: Pass");
				} else {
					txtOpenDesc.setText("Open By Description: Fail");
				}
				ftDevice.close();
			} else {
				txtOpenDesc.setText("Open By Description: Skip(No description)");
			}
			
			// openByUsbDevice
		} 
		else 
		{
			txtOpenIndex.setText("Open By Index: Fail");
			txtOpenSn.setText("Open By Serial Number: Fail");
			txtOpenDesc.setText("Open By Description: Fail");
			txtOpenLocation.setText("Open By Location: Fail");
		}
	}
	
	public boolean setParameter()
	{
		int ibufnum, ibufsize, itransize;
		int ireadtimeout;
		
		if(d2xxDrvParameter == null)
			return false;
		
		// 2 ~ 16
		if(bufnumValue.length() == 0x00){
		    ibufnum = d2xxDrvParameter.getBufferNumber();
		}
		else
		{
			ibufnum = Integer.parseInt(bufnumValue.getText().toString());			
			if(ibufnum < 2){
				ibufnum = 2;
			}
			else if(ibufnum > 16){
				ibufnum = 16;
			}
		}
		
		// 64 ~ 16384
		if(bufsizeValue.length() == 0x00){
			ibufsize = d2xxDrvParameter.getMaxBufferSize();	
		}
		else
		{
			ibufsize = Integer.parseInt(bufsizeValue.getText().toString());
			if(ibufsize < 64){
				ibufsize = 64;
			}
			else if(ibufsize > 16384){
				ibufsize = 16384;
			}
		}
		
		// 64 ~ 16384
		if(transizeValue.length() == 0x00){
			itransize = d2xxDrvParameter.getMaxTransferSize();	
		}
		else
		{
			itransize = Integer.parseInt(transizeValue.getText().toString());
			if(itransize < 64){
				itransize = 64;
			}
			else if(itransize > 16384){
				itransize = 16384;
			}
		}
		
		if(readtimeValue.length() == 0x00){
			ireadtimeout = d2xxDrvParameter.getReadTimeout();			
		}
		else
		{
			ireadtimeout = Integer.parseInt(readtimeValue.getText().toString());
		}
		
		d2xxDrvParameter.setBufferNumber(ibufnum);
		d2xxDrvParameter.setMaxBufferSize(ibufsize);
		d2xxDrvParameter.setMaxTransferSize(itransize);
		d2xxDrvParameter.setReadTimeout(ireadtimeout);
		bufnumValue.setText("" + ibufnum);
		bufsizeValue.setText("" + ibufsize);
		transizeValue.setText("" + itransize);
		readtimeValue.setText("" + ireadtimeout);
		return true;
	}
	
	public void getParameter()
	{
		if(d2xxDrvParameter != null)
		{
			bufnumValue.setText("" + d2xxDrvParameter.getBufferNumber());
			bufsizeValue.setText("" + d2xxDrvParameter.getMaxBufferSize());
			transizeValue.setText("" + d2xxDrvParameter.getMaxTransferSize());
			readtimeValue.setText("" + d2xxDrvParameter.getReadTimeout());
		}
		else
		{
			Toast.makeText(OpenDeviceFragmentContext, "Get parameter fail...", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void startOpenDevParam()
	{
		int devCount = 0;		
		devCount = ftdid2xx.createDeviceInfoList(OpenDeviceFragmentContext);

		if (devCount > 0) 
		{
			D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
			ftdid2xx.getDeviceInfoList(devCount, deviceList);

			// openByIndex
			ftDevice = ftdid2xx.openByIndex(OpenDeviceFragmentContext, 0, d2xxDrvParameter);
			if (ftDevice.isOpen()) {
				txtOpenIndex2.setText("Open By Index: Pass");
			} 
			else {
				txtOpenIndex2.setText("Open By Index: Fail");
			}
			ftDevice.close();
			
			// openBySerialNumber
			if (deviceList[0].serialNumber != null) {
				ftDevice = ftdid2xx.openBySerialNumber(OpenDeviceFragmentContext, deviceList[0].serialNumber, d2xxDrvParameter);
				if (ftDevice.isOpen()) {
					txtOpenSn2.setText("Open By Serial Number: Pass");
				} 
				else {
					txtOpenSn2.setText("Open By Serial Number: Fail");
				}
				ftDevice.close();
			} 
			else {
				txtOpenSn2.setText("Open By Serial Number: Skip(No serial number)");
			}

			// openByLocation
			ftDevice = ftdid2xx.openByLocation(OpenDeviceFragmentContext, deviceList[0].location, d2xxDrvParameter);
			if (ftDevice.isOpen()) {
				txtOpenLocation2.setText("Open By Location: Pass");
			} 
			else {
				txtOpenLocation2.setText("Open By Location: Fail");
			}
			ftDevice.close();
			
			// openByDescription
			if (deviceList[0].description != null) {
				ftDevice = ftdid2xx.openByDescription(OpenDeviceFragmentContext, deviceList[0].description, d2xxDrvParameter);
				if (ftDevice.isOpen()) {
					txtOpenDesc2.setText("Open By Description: Pass");
				}
				else {
					txtOpenDesc2.setText("Open By Description: Fail");
				}
				ftDevice.close();
			} else {
				txtOpenDesc2.setText("Open By Description: Skip(No Description)");
			}
			
		} 
		else 
		{
			txtOpenIndex2.setText("Open By Index: Fail");
			txtOpenSn2.setText("Open By Serial Number: Fail");
			txtOpenDesc2.setText("Open By Description: Fail");
			txtOpenLocation2.setText("Open By Location: Fail");
		}		
	}
	
    public void notifyUSBDeviceAttach(Intent intent)
    {	
		ftdid2xx.createDeviceInfoList(OpenDeviceFragmentContext);
    	usbDev = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
    	
		if (ftdid2xx.isFtDevice(usbDev)) 
		{	
			ftDevice = ftdid2xx.openByUsbDevice(OpenDeviceFragmentContext, usbDev);
			if(ftDevice == null)
			{
				txtOpenUsbDev1.setText("Open By UsbDevice: Fail(ftDevice == null)");
			}
			else
			{
				if (ftDevice.isOpen()) {
					txtOpenUsbDev1.setText("Open By UsbDevice: Pass");
				} else {
					txtOpenUsbDev1.setText("Open By UsbDevice: Fail");
				}
				ftDevice.close();
			}
			
			ftDevice = ftdid2xx.openByUsbDevice(OpenDeviceFragmentContext, usbDev, d2xxDrvParameter);
			if(ftDevice == null)
			{
				txtOpenUsbDev2.setText("Open By UsbDevice with Parameter: Fail(ftDevice == null)");
			}
			else				
			{
				if (ftDevice.isOpen()) {
					txtOpenUsbDev2.setText("Open By UsbDevice with Parameter: Pass");
				} 
				else {
					txtOpenUsbDev2.setText("Open By UsbDevice with Parameter: Fail");
				}
				ftDevice.close();
			}
		}
    }
}
