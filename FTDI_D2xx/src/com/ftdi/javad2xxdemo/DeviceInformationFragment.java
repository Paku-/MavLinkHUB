package com.ftdi.javad2xxdemo;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ftdi.j2xx.D2xxManager;




public class DeviceInformationFragment  extends Fragment {
	static Context DeviceInformationContext;
	D2xxManager ftdid2xx;
	int devCount = 0;
	TextView NumberDeviceValue;
	TextView DeviceName;
	TextView DeviceSerialNo;
	TextView DeviceDescription;
	TextView DeviceID;
	TextView DeviceLocation;
	TextView Error_Information;
	TextView Library;
	Button btnRefreshDevice;
	
	// Empty Constructor
	public DeviceInformationFragment()
	{
	}
	
	/* Constructor */
	public DeviceInformationFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		DeviceInformationContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}
	
    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.device_information, container, false);
    
		NumberDeviceValue = (TextView)view.findViewById(R.id.numDev);
		DeviceName = (TextView)view.findViewById(R.id.devName);
		DeviceSerialNo = (TextView)view.findViewById(R.id.device_information_serialno);
		DeviceDescription = (TextView)view.findViewById(R.id.device_information_description);
		DeviceID = (TextView)view.findViewById(R.id.device_informatation_deviceid);
		DeviceLocation = (TextView)view.findViewById(R.id.device_informatation_devicelocation);
		Error_Information = (TextView)view.findViewById(R.id.ErrorInformation);
		Library = (TextView)view.findViewById(R.id.device_informatation_library);
		
		btnRefreshDevice = (Button)view.findViewById(R.id.device_informatation_refresh);
		
        btnRefreshDevice.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
					RefrestDeviceInformation(v);
            }
        });

		IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        DeviceInformationContext.getApplicationContext().registerReceiver(mUsbPlugEvents, filter);
        
        return view;
    }

    @Override
    public void onStart() {
    	super.onStart();
		
        try {
			GetDeviceInformation();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

	public void GetDeviceInformation() throws InterruptedException {

		int devCount = 0;

		devCount = ftdid2xx.createDeviceInfoList(DeviceInformationContext);

		Log.i("FtdiModeControl",
				"Device number = " + Integer.toString(devCount));
		if (devCount > 0) {
			D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
			ftdid2xx.getDeviceInfoList(devCount, deviceList);
			
			// deviceList[0] = ftdid2xx.getDeviceInfoListDetail(0);
			
			NumberDeviceValue.setText("Number of Devices: "
					+ Integer.toString(devCount));
			
			if (deviceList[0].serialNumber == null) {
				DeviceSerialNo.setText("Device Serial Number: " + deviceList[0].serialNumber + "(No Serial Number)");
			} else {
				DeviceSerialNo.setText("Device Serial Number: " + deviceList[0].serialNumber);
			}
			
			if (deviceList[0].description == null) {
				DeviceDescription.setText("Device Description: " + deviceList[0].description+ "(No Description)");
			} else {
				DeviceDescription.setText("Device Description: " + deviceList[0].description);
			}
			
			DeviceLocation.setText("Device Location: "
					+ Integer.toString(deviceList[0].location));

			DeviceID.setText("Device ID: " + Integer.toString(deviceList[0].id));
			Library.setText("Library Version: " + Integer.toString(D2xxManager.getLibraryVersion()));
			// display the chip type for the first device
			switch (deviceList[0].type) {
			case D2xxManager.FT_DEVICE_232B:
				DeviceName.setText("Device Name : FT232B device");
				break;

			case D2xxManager.FT_DEVICE_8U232AM:
				DeviceName.setText("Device Name : FT8U232AM device");
				break;

			case D2xxManager.FT_DEVICE_UNKNOWN:
				DeviceName.setText("Device Name : Unknown device");
				break;

			case D2xxManager.FT_DEVICE_2232:
				DeviceName.setText("Device Name : FT2232 device");
				break;

			case D2xxManager.FT_DEVICE_232R:
				DeviceName.setText("Device Name : FT232R device");
				break;

			case D2xxManager.FT_DEVICE_2232H:
				DeviceName.setText("Device Name : FT2232H device");
				break;

			case D2xxManager.FT_DEVICE_4232H:
				DeviceName.setText("Device Name : FT4232H device");
				break;

			case D2xxManager.FT_DEVICE_232H:
				DeviceName.setText("Device Name : FT232H device");
				break;
			case D2xxManager.FT_DEVICE_X_SERIES:
				DeviceName.setText("Device Name : FTDI X_SERIES");
				break;
			default:
				DeviceName.setText("Device Name : FT232B device");
				break;
			}
		} else {
			NumberDeviceValue.setText("Number of devices: 0");
			DeviceName.setText("Device Name : No device");
			DeviceSerialNo.setText("Device Serial Number:");
			DeviceDescription.setText("Device Description:");
			DeviceLocation.setText("Device Location:");
			DeviceID.setText("Device ID: ");
			Library.setText("Library Version: ");

		}

	}
    
    public void RefrestDeviceInformation(View view) {
        try {
			GetDeviceInformation();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			String s = e.getMessage();
			if (s != null) {
				Error_Information.setText(s);
			}
			e.printStackTrace();
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

	    Intent intent = getActivity().getIntent();
	    Log.d("Paris ::", "intent: " + intent);
	    String action = intent.getAction();
	    
	    String hotplug = "android.intent.action.MAIN";
	    if (hotplug.equals(action)) {
	    	
	    	try {
				GetDeviceInformation();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				String s = e.getMessage();
				if (s != null) {
					Error_Information.setText(s);
				}
				e.printStackTrace();
			}
	    } 
	}
   
	/**
	 * Hot plug for plug out solution 
	 */
	private BroadcastReceiver mUsbPlugEvents = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
		    	try {
					GetDeviceInformation();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					String s = e.getMessage();
					if (s != null) {
						Error_Information.setText(s);
					}
					e.printStackTrace();
				}
	        } 
	    }
	};
}
