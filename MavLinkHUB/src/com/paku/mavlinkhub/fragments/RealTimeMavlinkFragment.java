package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.interfaces.IDataLoggedIn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class RealTimeMavlinkFragment extends Fragment implements IDataLoggedIn {

	@SuppressWarnings("unused")
	private static final String TAG = "RealTimeMavlinkFragment";
	private AppGlobals globalVars;

	public RealTimeMavlinkFragment() {

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
		final View rootView = inflater.inflate(
				R.layout.fragment_realtime_mavlink, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// final TextView textView = (TextView) (getView()
		// .findViewById(R.id.textView_logByte));
		// textView.setMovementMethod(new ScrollingMovementMethod());

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		globalVars.logger.registerRealTimeMavlinkForIDataLoggedIn(this);
		refreshUI();
	}

	@Override
	public void onPause() {
		globalVars.logger.unregisterRealTimeMavlinkForIDataLoggedIn();
		super.onPause();
	}

	public void refreshUI() {

		final TextView mTextViewBytesLog = (TextView) (getView()
				.findViewById(R.id.textView_logByte));

		String buff;

		if (globalVars.logger.mInMemIncomingBytesStream.size() > globalVars.visibleBuffersSize) {
			buff = new String(
					globalVars.logger.mInMemIncomingBytesStream.toByteArray(),
					globalVars.logger.mInMemIncomingBytesStream.size()
							- globalVars.visibleBuffersSize,
					globalVars.visibleBuffersSize);
		} else {
			buff = new String(
					globalVars.logger.mInMemIncomingBytesStream.toByteArray());

		}

		mTextViewBytesLog.setText(buff);

		// scroll down
		final ScrollView mScrollView = (ScrollView) (getView()
				.findViewById(R.id.scrollView_logByte));

		if (mScrollView != null) {

			mScrollView.post(new Runnable() {
				@Override
				public void run() {
					mScrollView.fullScroll(View.FOCUS_DOWN);
				}
			});

		}

		// stats bar
		final TextView mTextViewLogStats = (TextView) (getView()
				.findViewById(R.id.textView_logStatsbar));

		mTextViewLogStats.setText("Bytes Count: "
				+ globalVars.logger.statsReadByteCount);

	}

	@Override
	public void onDataLoggedInReady() {
		// Log.d(TAG,
		// "[ByteLog]"+globalVars.logger.mInMemIncomingBytesStream.size());
		refreshUI();
	}

}