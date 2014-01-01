package com.paku.mavlinkhub.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RealTimeMavlinkFragment extends Fragment implements IBufferReady {

	private static final String TAG = "RealTimeMavlinkFragment";
	private AppGlobals globalVars;
	ByteArrayOutputStream mByteLogTempStream;
	
	public RealTimeMavlinkFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		globalVars = (AppGlobals) getActivity().getApplication();

		mByteLogTempStream = new ByteArrayOutputStream();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_realtime_mavlink,
				container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final TextView textView = (TextView) (getView()
				.findViewById(R.id.textView_logByte));	
		textView.setMovementMethod(new ScrollingMovementMethod());		
		

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		globalVars.mBtConnector.registerForIBufferReady(this);
		refreshUI();
	}

	public void refreshUI() {

		final TextView mTextViewBytesLog = (TextView) (getView()
				.findViewById(R.id.textView_logByte));
		
		mTextViewBytesLog.append(mByteLogTempStream.toString());
		//mByteLogTempStream.reset();
		
		
		final TextView mTextViewMsgLog = (TextView) (getView()
				.findViewById(R.id.TextView_logMavLinkMsg));

		mTextViewMsgLog.append(mByteLogTempStream.toString());
		mByteLogTempStream.reset();
		
		
		//scroll down
        final Layout layout = mTextViewMsgLog.getLayout();
        if(layout != null){
            int scrollDelta = layout.getLineBottom(mTextViewMsgLog.getLineCount() - 1) 
                - mTextViewMsgLog.getScrollY() - mTextViewMsgLog.getHeight();
            if(scrollDelta > 0)
                mTextViewMsgLog.scrollBy(0, scrollDelta);
        }
        
        
		final TextView mTextViewLogStats = (TextView) (getView()
				.findViewById(R.id.textView_logStatsbar));
		
		mTextViewLogStats.setText("Bytes Count: "+globalVars.sysStatsHolder.statsByteCount);
		
        


	}

	@Override
	public void onBufferReady() {

		//get data from the connector mConnectorStream	
		try {
			globalVars.mBtConnector.copyConnectorStream(mByteLogTempStream,false);
		} catch (IOException e) {
			Log.d(TAG, "Stream copy: " + e.getMessage());
			e.printStackTrace();
		}

		refreshUI();
		
				
	}

}