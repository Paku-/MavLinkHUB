package com.ftdi.javad2xxdemo;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class DevicePIDVIDFragment extends Fragment {

	Context DevicePIDVIDContext;
	D2xxManager ftdid2xx = null;
	FT_Device ftDevice = null;

	Button btnSetPidVid;
	EditText Edit_VID;
	EditText Edit_PID;
	
	Button btnGetPidVid;
	ScrollView scrollView;
	TextView readText;

	// Empty Constructor
	public DevicePIDVIDFragment()
	{
	}

	/* Constructor */
	public DevicePIDVIDFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		DevicePIDVIDContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.device_pidvid, container, false);

        btnSetPidVid = (Button)view.findViewById(R.id.set_pidvid);
    	Edit_VID = (EditText)view.findViewById(R.id.edit_vid);
    	Edit_PID= (EditText)view.findViewById(R.id.edit_pid);
    	
    	btnGetPidVid = (Button)view.findViewById(R.id.get_pidvid);
    	scrollView = (ScrollView) view.findViewById(R.id.ReadField);
    	readText = (TextView) view.findViewById(R.id.ReadValues);

        btnSetPidVid.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	if(null != ftdid2xx)
            	{
            	    ftdid2xx.createDeviceInfoList(DevicePIDVIDContext);
            	    Set_PIDVID_Click(v);
            	}
            	else
            	{
            		Toast.makeText(DevicePIDVIDContext, "NG: ftdid2xx == null !!", Toast.LENGTH_LONG).show();
            	}
				
            }
        });
        
        btnGetPidVid.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
            	if(null != ftdid2xx)
            	{
            	    ftdid2xx.createDeviceInfoList(DevicePIDVIDContext);
            	    Get_PIDVID_Click(v);
            	}
            	else
            	{
            		Toast.makeText(DevicePIDVIDContext, "NG: ftdid2xx == null !!", Toast.LENGTH_LONG).show();
            	}
				
            }
        });
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void Set_PIDVID_Click(View view) {
    	String sVid = Edit_VID.getText().toString();
    	String sPid = Edit_PID.getText().toString();

    	if( sVid.isEmpty() || sPid.isEmpty() ) {
			Toast.makeText(DevicePIDVIDContext, "Please enter Product ID and Vendor ID", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	if( sVid.length() != 4 || sPid.length() != 4 ) {
    		Toast.makeText(DevicePIDVIDContext, "Please enter correct length(4)", Toast.LENGTH_SHORT).show();
    		return;
    	}

    	try {
    		int iVid = Integer.parseInt(sVid,16);
    		int iPid = Integer.parseInt(sPid,16);

    		Log.e("PIDVID","vid  = " + Integer.toString(iVid));
    		Log.e("PIDVID","pid = " + Integer.toString(iPid));

			ftdid2xx.setVIDPID(iVid,iPid);
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		}
    }

    public void Get_PIDVID_Click(View view) {
    	int [][] arrayPIDVID = ftdid2xx.getVIDPID();
    	int len_2 = arrayPIDVID[0].length;
    	
    	readText.setText("");
    	
    	for(int i = 0; i < len_2; i++)
    	{
			String temp = "" + i +". VID:" + Integer.toHexString(arrayPIDVID[0][i]) 
	                             +"     PID:" + Integer.toHexString(arrayPIDVID[1][i]) + "\n";			
		    String tmp = temp.replace("\\n", "\n");
    		readText.append(tmp);	
    	}
    	readText.append("END");    	
    }
}
