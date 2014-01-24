package com.ftdi.javad2xxdemo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

public class EEPROMUserAreaFragment extends Fragment {
	Context EEPROMFragmentContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;

	EditText UserDataSizeText;
	ScrollView scrollView;
	TextView readText;

	Button btnSetEEPROMErase;
	Button btnSetEEPROMRead;
	Button btnSetEEPROMWrite;

	int datasize = 0;

	// Empty Constructor
	public EEPROMUserAreaFragment()
	{
	}

	/* Constructor */
	public EEPROMUserAreaFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		EEPROMFragmentContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_eeprom_userarea, container, false);

        UserDataSizeText = (EditText)view.findViewById(R.id.UserDataSizeValue);
    	scrollView = (ScrollView) view.findViewById(R.id.ReadField);
    	readText = (TextView) view.findViewById(R.id.ReadValues);
    	readText.setTextSize(24);

    	btnSetEEPROMErase 	= (Button)view.findViewById(R.id.eeprom_erase_mode);
    	btnSetEEPROMRead 	= (Button)view.findViewById(R.id.eeprom_read_mode);
    	btnSetEEPROMWrite 	= (Button)view.findViewById(R.id.eeprom_write_mode);

    	btnSetEEPROMErase.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetEEPROMEraseClick(v);
            }
        });

    	btnSetEEPROMRead.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetEEPROMReadClick(v);
            }
        });

    	btnSetEEPROMWrite.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				btnSetEEPROMWriteClick(v);
            }
        });

    	return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ftdid2xx.createDeviceInfoList(EEPROMFragmentContext);
        UserDataSizeText.setText("User Area Size:");
        readText.setText("");
        datasize = 0;
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
        UserDataSizeText.setText("User Area Size:");
        readText.setText("");
        datasize = 0;
    }

	public void notifyUSBDeviceAttach()
	{
		ftdid2xx.createDeviceInfoList(EEPROMFragmentContext);
        UserDataSizeText.setText("User Area Size:");
        readText.setText("");
		datasize = 0;
	}

    public void btnSetEEPROMEraseClick(View view) {
    	StartEEpromErase();
    }

    public void btnSetEEPROMReadClick(View view) {
    	StartEEpromRead();
    }

    public void btnSetEEPROMWriteClick(View view) {
    	StartEEpromWrite();
    }

	public void StartEEpromRead() {
		readText.setText("");
		if(ftdid2xx.createDeviceInfoList(EEPROMFragmentContext) <= 0)
			return;
		ftDevice = ftdid2xx.openByIndex(EEPROMFragmentContext, 0);

		if (ftDevice == null) {
			Toast.makeText(EEPROMFragmentContext, "Not supported device",Toast.LENGTH_SHORT).show();
			UserDataSizeText.setText("User Area Size: null");
			readText.setText("null");
		} else {
			datasize = ftDevice.eepromGetUserAreaSize();
			UserDataSizeText.setText("User Area Size: "+ datasize);

			String temp = "[Read user area data from byte " + 0 + " to byte " + (datasize-1) + "]\n";
			String tmp = temp.replace("\\n", "\n");
			readText.append(tmp);
			
			byte [] userdata = ftDevice.eepromReadUserArea(datasize);
			for(int i = 0; i < datasize; i++)
			{
				readText.append(" " + i + ":" + Integer.toHexString(userdata[i]) + "   ");
			}
		}
		ftDevice.close();
	}

    public void StartEEpromWrite() {
    	readText.setText("");
		if(ftdid2xx.createDeviceInfoList(EEPROMFragmentContext) <= 0)
			return;
		ftDevice = ftdid2xx.openByIndex(EEPROMFragmentContext, 0);

		if (ftDevice == null) {
			Toast.makeText(EEPROMFragmentContext, "Not supported device",Toast.LENGTH_SHORT).show();
			UserDataSizeText.setText("User Area Size: null");
			readText.setText("null");
		} else {
			if( datasize <= 0)
			{
				String temp = "[Please click Read EEPROM button to get user area size before other test]";
				readText.setText(temp);
				ftDevice.close();
				return;
			}
			
			String temp = "[Write fixed data(hex):0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f,10,11... to user area from byte 0 to byte "+(datasize-1)+"]";
			readText.setText(temp);			

			byte [] wdata = new byte [datasize];
			for(int j = 0; j < (datasize/2); j++)
			{
				wdata[j*2] = (byte)(j*2+1);
				wdata[j*2+1] = (byte)(j*2);
			}
			
			ftDevice.eepromWriteUserArea(wdata);
		}
		ftDevice.close();
    }

	public void StartEEpromErase() {
    	readText.setText("");
		if(ftdid2xx.createDeviceInfoList(EEPROMFragmentContext) <= 0)
			return;
		ftDevice = ftdid2xx.openByIndex(EEPROMFragmentContext, 0);

		if (ftDevice == null) {
			Toast.makeText(EEPROMFragmentContext, "Not supported device",Toast.LENGTH_SHORT).show();
			UserDataSizeText.setText("User Area Size: null");
			readText.setText("null");
		} else {
			if( datasize <= 0)
			{
				String temp = "[Please click Read EEPROM button to get user area size before other test]";
				readText.setText(temp);
				ftDevice.close();
				return;
			}
			
			String temp = "[Erase user area data from byte 0 to byte " + (datasize-1) + "(set to 0)]";
			readText.setText(temp);

			byte [] wdata = new byte [datasize];
			ftDevice.eepromWriteUserArea(wdata);
		}
		ftDevice.close();
	}
}
