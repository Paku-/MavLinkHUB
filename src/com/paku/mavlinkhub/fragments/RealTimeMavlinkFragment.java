package com.paku.mavlinkhub.fragments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.AppGlobals;
import com.paku.mavlinkhub.interfaces.IBufferReady;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class RealTimeMavlinkFragment extends Fragment implements IBufferReady {

	private AppGlobals globalVars;
	ByteArrayOutputStream mStream;

	//scrollView object used to force TextView object to keep scrolling down.
	ScrollView scrollview;

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
		View rootView = inflater.inflate(R.layout.fragment_realtime_mavlink,
				container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		scrollview = ((ScrollView) getView().findViewById(R.id.scrollView1));
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		globalVars.mBtConnector.registerFragmentForIBufferReady(this);
		refreshUI();
	}

	public void refreshUI() {

		//get data from the connector mConnectorStream	
		try {
			globalVars.mBtConnector.getResetConnStream(mStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		TextView textView = (TextView) (getView()
				.findViewById(R.id.textView_log));
		textView.append(mStream.toString());

		mStream.reset();
		
		scrollview.post(new Runnable() {
			@Override
			public void run() {
				scrollview.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

	}

	@Override
	public void onBufferReady() {
		refreshUI();
	}

}