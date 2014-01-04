package com.paku.mavlinkhub.fragments;

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

public class SysLogFragment extends Fragment implements IBufferReady {

	//@SuppressWarnings("unused")
	private static final String TAG = "SysLogFragment";
	private AppGlobals globalVars;
	
	public SysLogFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		globalVars = (AppGlobals) getActivity().getApplication();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_sys_log,
				container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		final TextView textView = (TextView) (getView()
				.findViewById(R.id.TextView_logSysLog));	
		textView.setMovementMethod(new ScrollingMovementMethod());		
		

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		globalVars.mBtConnector.registerSysLogForIBufferReady(this);
		refreshUI();
	}

	public void refreshUI() {

		final TextView mTextViewBytesLog = (TextView) (getView()
				.findViewById(R.id.TextView_logSysLog));
		
		mTextViewBytesLog.setText(globalVars.logger.mInMemSysLogStream.toString());
		
		
		//final TextView mTextViewMsgLog = (TextView) (getView()
			//	.findViewById(R.id.TextView_logMavLinkMsg)); 
		//mTextViewMsgLog.setText(globalVars.mMavLinkCollector.mMsgSysWideLogStream)
		//mByteLogTempStream.reset();
		
		
		//scroll down
        final Layout layout = mTextViewBytesLog.getLayout();
        if(layout != null){
            int scrollDelta = layout.getLineBottom(mTextViewBytesLog.getLineCount() - 1) 
                - mTextViewBytesLog.getScrollY() - mTextViewBytesLog.getHeight();
            if(scrollDelta > 0)
            	mTextViewBytesLog.scrollBy(0, scrollDelta);
        }
        
        
		final TextView mTextViewLogStats = (TextView) (getView()
				.findViewById(R.id.textView_logSysLogStatsbar));
		
		mTextViewLogStats.setText("Bytes Count: "+globalVars.logger.statsReadByteCount);
		
        


	}

	@Override
	public void onBufferReady() {
		Log.d(TAG, "[SysLogLog]"+globalVars.logger.mInMemSysLogStream.size());
		refreshUI();				
	}

}