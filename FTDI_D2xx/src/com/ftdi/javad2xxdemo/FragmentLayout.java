package com.ftdi.javad2xxdemo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ftdi.j2xx.D2xxManager;



public class FragmentLayout extends Activity {
	public static D2xxManager ftD2xx = null;
	public static int currect_index = 0;
	public static int old_index = -1;
	
	private static Fragment currentFragment = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
    		ftD2xx = D2xxManager.getInstance(this);
    	} catch (D2xxManager.D2xxException ex) {
    		ex.printStackTrace();
    	}
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        SetupD2xxLibrary();
        
		IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);   
    }

    @Override
	protected void onDestroy() {
    	this.unregisterReceiver(mUsbReceiver);
    	super.onDestroy();
	}

    public static class DetailsActivity extends Activity {

    	Map<Integer, Fragment> act_map = new HashMap<Integer, Fragment>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                Fragment f = act_map.get(currect_index);
                if (f == null)
                {
                	switch (currect_index)
                	{
                		case 0:
                			f = new DeviceInformationFragment(this , ftD2xx);
                			break;
                		case 1:
                			f = new DeviceStatusFragment(this , ftD2xx);
                			break;
                		case 2:
                			f = new DevicePIDVIDFragment(this , ftD2xx);
                			break;
                		case 3:
                			f = new MiscFragment(this , ftD2xx);
                			break;
                		case 4:
                			f = new OpenDeviceFragment(this , ftD2xx);
                			break;
                		case 5:
                			f = new DeviceUARTFragment(this , ftD2xx);
                			break;
                   		case 6:
                			f = new DeviceFileTransferFragment(this , ftD2xx);
                			break;
                   		case 7:
                			f = new EEPROMFragment(this , ftD2xx);
                			break;
                   		case 8:
                        	f = new EEPROMUserAreaFragment(this , ftD2xx);
                			break;
                   		case 9:
                			f = new FT4232HTestFragment(this , ftD2xx);
                			break;
                   		case 10:
                			f = new FT2232HTestFragment(this , ftD2xx);
                			break;
                		case 11:
                			f = new RS232PinConfigFragment(this , ftD2xx);
                			break;	
                		case 12:
                			f = new MPSEFragment(this , ftD2xx);
                			break;
                		case 13:
                			f = new FtdiEventFragment(this , ftD2xx);
                			break;  
                		default:
                			f = new DetailsFragment();
                			break;
                	}

                	act_map.put(currect_index, f);
                	f.setArguments(getIntent().getExtras());
                	getFragmentManager().beginTransaction().add(android.R.id.content, f).commit();
                }
                
                currentFragment = f;
            }
        }
    }

    private void SetupD2xxLibrary () {
    	/*
        PackageManager pm = getPackageManager();

        for (ApplicationInfo app : pm.getInstalledApplications(0)) {
          Log.d("PackageList", "package: " + app.packageName + ", sourceDir: " + app.nativeLibraryDir);
          if (app.packageName.equals(R.string.app_name)) {
        	  System.load(app.nativeLibraryDir + "/libj2xx-utils.so");
        	  Log.i("ftd2xx-java","Get PATH of FTDI JIN Library");
        	  break;
          }
        }
        */
    	// Specify a non-default VID and PID combination to match if required

    	if(!ftD2xx.setVIDPID(0x0403, 0xada1))
    		Log.i("ftd2xx-java","setVIDPID Error");

    }

    /**
     * This is the "top-level" fragment, showing a list of items that the
     * user can pick.  Upon picking an item, it takes care of displaying the
     * data to the user as appropriate based on the currrent UI layout.
     */

    public static class TitlesFragment extends ListFragment {
        boolean mDualPane;
        int mCurCheckPosition = 0;
       //  int mDualPaneIndex = -1;
        // public static D2xx ftD2xx;
        // Context TitlesFragmentContext = this.;
        Map<Integer, Fragment> map = new HashMap<Integer, Fragment>();

    	// public void setTitlesFragment(Context parentContext)
    	// {
    	//	TitlesFragmentContext = parentContext;
    		// ftD2xx = ftdid2xx;
    	// }

        public TitlesFragment() {

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Populate list with our static array of titles.
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_activated_1, FtdiModeListInfo.TITLES));

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.details);

            mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected item.
                getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                showDetails(mCurCheckPosition);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            showDetails(position);
        }

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a
         * whole new activity in which it is displayed.
         */
        void showDetails(int index) {
            mCurCheckPosition = index;
            currect_index = index;
            // Log.i("FragmentLayout","index = " + Integer.toString(index));
            // Log.i("FragmentLayout","mDualPaneIndex = " + Integer.toString(mDualPaneIndex));
            if (mDualPane) {
            	// We can display everything in-place with fragments, so update
            	// the list to highlight the selected item and show the data.
                getListView().setItemChecked(index, true);
                Fragment f = map.get(index);
                if (f == null) {
                	switch (index) {
                	case 0:
                		f = new DeviceInformationFragment(getActivity() , ftD2xx);
                		break;
                	case 1:
                		f = new DeviceStatusFragment(getActivity() , ftD2xx);
                		break;
                	case 2:
                		f = new DevicePIDVIDFragment(getActivity() , ftD2xx);
                		break;
                	case 3:
                		f = new MiscFragment(getActivity() , ftD2xx);
                		break;
                	case 4:
                		f = new OpenDeviceFragment(getActivity() , ftD2xx);
                		break;
            		case 5:
            			f = new DeviceUARTFragment(getActivity() , ftD2xx);
            			break;
               		case 6:
            			f = new DeviceFileTransferFragment(getActivity() , ftD2xx);
            			break;
               		case 7:
            			f = new EEPROMFragment(getActivity() , ftD2xx);
            			break;
               		case 8:
                    	f = new EEPROMUserAreaFragment(getActivity() , ftD2xx);
            			break;
               		case 9:
            			f = new FT4232HTestFragment(getActivity() , ftD2xx);
            			break;
               		case 10:
                    	f = new FT2232HTestFragment(getActivity() , ftD2xx);
        			break;	
                	case 11:
                		f = new RS232PinConfigFragment(getActivity() , ftD2xx);
                		break;
                	case 12:
                		f = new MPSEFragment(getActivity() , ftD2xx);
                		break;
            		case 13:
            			f = new FtdiEventFragment(getActivity() , ftD2xx);
            			break;  
                	default:
                		f = new DetailsFragment();
                		break;
                	}
                	
                	map.put(index, f);
                	Bundle args = new Bundle();
                	args.putInt("index", index);
                	f.setArguments(args);
                }
                
                currentFragment = f;
                
                if ( currect_index != old_index ) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, f);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                }
                old_index = currect_index;
            }
            else
            {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        }
    }

    /**
     * This is the secondary fragment, displaying the details of a particular
     * item.
     */
    public static class DetailsFragment extends Fragment {
    	/*
        public static DetailsFragment newInstance(int index) {
            DetailsFragment f = new DetailsFragment();
            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt("index", index);
            f.setArguments(args);

            return f;
        }
		*/
    	public DetailsFragment() {

    	}

        public int getShownIndex() {
            return getArguments().getInt("index", -1);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            if (container == null) {
                // We have different layouts, and in one of them this
                // fragment's containing frame doesn't exist.  The fragment
                // may still be created from its saved state, but there is
                // no reason to try to create its view hierarchy because it
                // won't be displayed.  Note this is not needed -- we could
                // just run the code below, where we would create and return
                // the view hierarchy; it would just never be used.
                return null;
            }

            ScrollView scroller = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());
            int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    4, getActivity().getResources().getDisplayMetrics());
            text.setPadding(padding, padding, padding, padding);
            scroller.addView(text);
            text.setText(FtdiModeListInfo.DIALOGUE[getShownIndex()]);
            return scroller;
        }
    }

	@Override
	protected void onNewIntent(Intent intent)
	{
		String action = intent.getAction();
		if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))		
		{
        	switch (currect_index) 
        	{
        	case 4:
        		((OpenDeviceFragment)currentFragment).notifyUSBDeviceAttach(intent);
        		break;
    		case 5:
    			((DeviceUARTFragment)currentFragment).notifyUSBDeviceAttach();
    			break;
       		case 7:
       			((EEPROMFragment)currentFragment).notifyUSBDeviceAttach();    			
    			break;
       		case 8:
       			((EEPROMUserAreaFragment)currentFragment).notifyUSBDeviceAttach();            	
    			break;    			
        	default:
        		break;
        	}
		}
	}
    
	/***********USB broadcast receiver*******************************************/
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String TAG = "FragL";			
			String action = intent.getAction();
			if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
			{
				Log.i(TAG,"DETACHED...");
				
	            if (currentFragment != null)
	            {
	            	switch (currect_index) 
	            	{

	        		case 5:
	        			((DeviceUARTFragment)currentFragment).notifyUSBDeviceDetach();
	        			break;
	            	default:
	            		//((DeviceInformationFragment)currentFragment).onStart();
	            		break;
	            	}
	            }         	
			}
		}	
	};
}
