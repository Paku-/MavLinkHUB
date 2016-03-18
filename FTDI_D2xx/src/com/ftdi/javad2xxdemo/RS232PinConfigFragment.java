package com.ftdi.javad2xxdemo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

public class RS232PinConfigFragment extends Fragment {
	Context RS232PinConfigFragmentContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;
	TextView ErrorInformation;
	Button btnDTR;
	Button btnRTS;
	static int iDtrFlag = 1;
	static int iRtsFlag = 1;

	// Empty Constructor
	public RS232PinConfigFragment()
	{
	}

	/* Constructor */
	public RS232PinConfigFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		RS232PinConfigFragmentContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 11);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_rs232pinconfig , container, false);

        ErrorInformation = (TextView)view.findViewById(R.id.ErrorInformation);
        btnDTR = (Button)view.findViewById(R.id.btn_dtr);
        btnRTS = (Button)view.findViewById(R.id.btn_rts);

        btnDTR.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				try {
					if(IsDeviceConnected() !=0 ) {
						btn_dtr_click(v);
					}
				} catch (InterruptedException e) {
					String s = e.getMessage();
					if (s != null) {
						ErrorInformation.setText(s);
					}
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        btnRTS.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
				try {
					if(IsDeviceConnected() !=0 ) {
						btn_rts_Click(v);
					}
				} catch (InterruptedException e) {
					String s = e.getMessage();
					if (s != null) {
						ErrorInformation.setText(s);
					}
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });


    	return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

	public void btn_dtr_click(View view) {
		// open our first device
		ftDevice = ftdid2xx.openByIndex(RS232PinConfigFragmentContext, 0);

		// reset to UART mode for 232 devices
		ftDevice.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		// set 230400 baud rate
		ftDevice.setBaudRate(230400);

		// set 8 data bits, 1 stop bit, no parity
		ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8,
				D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);

		// set RTS/CTS flow control
		ftDevice.setFlowControl(D2xxManager.FT_FLOW_RTS_CTS, (byte) 0x00, (byte) 0x00);

		if (iDtrFlag == 1) {
			iDtrFlag = 0;
			ftDevice.clrDtr();
			btnDTR.setText("Enable DTR");
		} else {
			iDtrFlag = 1;
			ftDevice.setDtr();
			btnDTR.setText("Disable DTR");
		}
		// close our device
		ftDevice.close();

	}

	public void btn_rts_Click(View view) {
		// open our first device
		ftDevice = ftdid2xx.openByIndex(RS232PinConfigFragmentContext, 0);

		if (iRtsFlag == 1) {
			iRtsFlag = 0;
			ftDevice.clrRts();
			// ftdid2xx.setRts();
			btnRTS.setText("Enable RTS");
		} else {
			iRtsFlag = 1;
			ftDevice.setRts();
			// ftdid2xx.clrRts();
			btnRTS.setText("Disable RTS");
		}
		// close our device
		ftDevice.close();
	}

    public int IsDeviceConnected() throws InterruptedException {
    	int ConnectCount = 0;
    	try {
			ConnectCount = ftdid2xx.createDeviceInfoList(RS232PinConfigFragmentContext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String s = e.getMessage();
			if (s != null) {
				ErrorInformation.setText(s);
			}
			e.printStackTrace();
		}

    	return ConnectCount;
    }
}
