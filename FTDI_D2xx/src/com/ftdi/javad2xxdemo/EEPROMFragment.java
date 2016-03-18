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
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.FT_EEPROM;

public class EEPROMFragment extends Fragment {
	Context EEPROMFragmentContext;
	D2xxManager ftdid2xx;
	FT_Device ftDevice = null;

	EditText VendorIDText, ProductIDText, ProductDescriptionText, SerialNumberText;
	EditText data1, data2, data3, data4;

	Button btnSetEEPROMErase;
	Button btnSetEEPROMRead;
	Button btnSetEEPROMWrite;

	// Empty Constructor
	public EEPROMFragment()
	{
	}

	/* Constructor */
	public EEPROMFragment(Context parentContext , D2xxManager ftdid2xxContext)
	{
		EEPROMFragmentContext = parentContext;
		ftdid2xx = ftdid2xxContext;
	}

    public int getShownIndex() {
        return getArguments().getInt("index", 7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.device_eeprom, container, false);

        VendorIDText 			= (EditText)view.findViewById(R.id.VendorIDValue);
        ProductIDText 			= (EditText)view.findViewById(R.id.ProductIDValue);
        ProductDescriptionText 	= (EditText)view.findViewById(R.id.ProductDescriptionValue);
        SerialNumberText 		= (EditText)view.findViewById(R.id.SerialNumberValue);

        data1 = (EditText)view.findViewById(R.id.dataVal1);
        data2 = (EditText)view.findViewById(R.id.dataVal2);
        data3 = (EditText)view.findViewById(R.id.dataVal3);
        data4 = (EditText)view.findViewById(R.id.dataVal4);

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
    }

    @Override
	public void onDestroy() {
		super.onDestroy();
    }

	public void notifyUSBDeviceAttach()
	{
		ftdid2xx.createDeviceInfoList(EEPROMFragmentContext);
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
		VendorIDText.setText("");
		ProductIDText.setText("");
		ProductDescriptionText.setText("");
		SerialNumberText.setText("");

		FT_EEPROM ft_Data = null;
		if(ftdid2xx.createDeviceInfoList(EEPROMFragmentContext) <= 0)
			return;
		ftDevice = ftdid2xx.openByIndex(EEPROMFragmentContext, 0);

		
		ft_Data = ftDevice.eepromRead();

		if (ft_Data == null) {
			Toast.makeText(EEPROMFragmentContext, "Not supported device",
					Toast.LENGTH_SHORT).show();
		} else {
			VendorIDText.setText("0x" + Integer.toHexString(ft_Data.VendorId));
			ProductIDText.setText("0x" + Integer.toHexString(ft_Data.ProductId));
			ProductDescriptionText.setText(ft_Data.Product);
			SerialNumberText.setText(ft_Data.SerialNumber);
		}

		data1.setText(""+ftDevice.eepromReadWord((short)58));
		data2.setText(""+ftDevice.eepromReadWord((short)59));
		data3.setText(""+ftDevice.eepromReadWord((short)60));
		data4.setText(""+ftDevice.eepromReadWord((short)61));

		ftDevice.close();
	}

    public void StartEEpromWrite() {

    	FT_EEPROM ft_Data;
		if(ftdid2xx.createDeviceInfoList(EEPROMFragmentContext) <= 0)
			return;
		ftDevice = ftdid2xx.openByIndex(EEPROMFragmentContext, 0);		
		
		ft_Data = ftDevice.eepromRead();

		if (ft_Data == null) {
			Toast.makeText(EEPROMFragmentContext, "Not supported device", Toast.LENGTH_SHORT).show();
		} else {
			if (ProductDescriptionText.length() != 0)
				ft_Data.Product = ProductDescriptionText.getText().toString();

			if (SerialNumberText.length() != 0)
				ft_Data.SerialNumber = SerialNumberText.getText().toString();

		ftDevice.eepromWrite(ft_Data);
		}


		short bdata1, bdata2, bdata3, bdata4;

		if(data1.length() == 0x00){
			bdata1 = 0;
		}
		else if( Integer.parseInt(data1.getText().toString()) > 65535){
			bdata1 = (short)65535;
		}
		else{
			bdata1 = (short)Integer.parseInt(data1.getText().toString());
		}

		if(data2.length() == 0x00){
			bdata2 = 0;
		}
		else if( Integer.parseInt(data2.getText().toString()) > 65535){
			bdata2 = (short)65535;
		}
		else{
			bdata2 = (short)Integer.parseInt(data2.getText().toString());
		}

		if(data3.length() == 0x00){
			bdata3 = 0;
		}
		else if( Integer.parseInt(data3.getText().toString()) > 65535){
			bdata3 = (short)65535;
		}		
		else{
			bdata3 = (short)Integer.parseInt(data3.getText().toString());
		}

		if(data4.length() == 0x00){
			bdata4 = 0;
		}
		else if( Integer.parseInt(data4.getText().toString()) > 65535){
			bdata4 = (short)65535;
		}
		else{
			bdata4 = (short)Integer.parseInt(data4.getText().toString());
		}

		ftDevice.eepromWriteWord((short)58, bdata1);
		ftDevice.eepromWriteWord((short)59, bdata2);
		ftDevice.eepromWriteWord((short)60, bdata3);
		ftDevice.eepromWriteWord((short)61, bdata4);

		ftDevice.close();

    }

    /**
     *  This function should not be call
	 *  casually , so i market it first
	 *  in this sample project.
	 */
	public void StartEEpromErase() {
		
		if(ftdid2xx.createDeviceInfoList(EEPROMFragmentContext) <= 0)
			return;
		ftDevice = ftdid2xx.openByIndex(EEPROMFragmentContext, 0);		
		ftDevice.eepromErase();
		ftDevice.close();
	}
}
