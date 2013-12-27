package com.paku.mavlinkhub.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class RealTimeMavlinkFragment extends Fragment implements IBufferReady {

	private AppGlobals globalVars;

	public RealTimeMavlinkFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		globalVars = (AppGlobals) getActivity().getApplication();
		globalVars.mBtConnector.registerForIBufferReady(this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_realtime_mavlink,
				container, false);
		
		return rootView;
	}
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		//TextView textView = (TextView) (getView()
		//		.findViewById(R.id.textView_log));
		//textView.setMovementMethod(new ScrollingMovementMethod());
		
		
	}


	public void refreshUI() {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			globalVars.mBtConnector.copyandResetBuffer(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
/*		
		   getScrollView().post(new Runnable() {

		        @Override
		        public void run() {
		            getScrollView().fullScroll(ScrollView.FOCUS_DOWN);
		        }
		    });		
*/		

		TextView textView = (TextView) (getView()
				.findViewById(R.id.textView_log));
		textView.append(buffer.toString());				
		final ScrollView scrollview = ((ScrollView) getView().findViewById(R.id.scrollView1));
		scrollview.post(new Runnable() {
		    @Override
		    public void run() {
		        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
		    }
		});		
		

		/*
		 * String time = "Time Passed" + SystemClock.elapsedRealtime();
		 * 
		 * TextView textView = (TextView) (getView()
		 * .findViewById(R.id.textView_log)); textView.setTextColor(Color.BLUE);
		 * textView.setText(time);
		 */

	}

	@Override
	public void onBufferReady() {
		refreshUI();
	}

}