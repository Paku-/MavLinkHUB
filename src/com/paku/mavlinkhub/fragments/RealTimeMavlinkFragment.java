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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RealTimeMavlinkFragment extends Fragment implements IBufferReady {

	private AppGlobals globalVars;
	ByteArrayOutputStream mStream;
	
	int logCount = 0 ;

	public RealTimeMavlinkFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		globalVars = (AppGlobals) getActivity().getApplication();

		mStream = new ByteArrayOutputStream();

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
				.findViewById(R.id.textView_log));	
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

		final TextView mTextView = (TextView) (getView()
				.findViewById(R.id.textView_log));
		
		mTextView.append(mStream.toString());

		//scroll down
        final Layout layout = mTextView.getLayout();
        if(layout != null){
            int scrollDelta = layout.getLineBottom(mTextView.getLineCount() - 1) 
                - mTextView.getScrollY() - mTextView.getHeight();
            if(scrollDelta > 0)
                mTextView.scrollBy(0, scrollDelta);
        }
        
        
		final TextView mTextViewLogStats = (TextView) (getView()
				.findViewById(R.id.textView1));
		
		mTextViewLogStats.setText("Bytes Count: "+logCount);
		
        


	}

	@Override
	public void onBufferReady() {

		//get data from the connector mConnectorStream	
		try {
			globalVars.mBtConnector.copyConnectorStream(mStream,false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logCount+=mStream.size();
		
		refreshUI();
		
		mStream.reset();		
	}

}