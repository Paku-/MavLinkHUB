package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.interfaces.IDataLoggedIn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

		final TextView textView = (TextView) (getView()
				.findViewById(R.id.textView_logByte));
		textView.setMovementMethod(new ScrollingMovementMethod());

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		globalVars.mMavLinkCollector
				.registerRealTimeMavlinkForIDataLoggedIn(this);
		refreshUI();
	}

	@Override
	public void onPause() {
		globalVars.mMavLinkCollector
				.unregisterRealTimeMavlinkForIDataLoggedIn();
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

		// final TextView mTextViewMsgLog = (TextView) (getView()
		// .findViewById(R.id.TextView_logMavLinkMsg));
		// mTextViewMsgLog.setText(globalVars.mMavLinkCollector.mMsgSysWideLogStream)
		// mByteLogTempStream.reset();

		// scroll down
		final Layout layout = mTextViewBytesLog.getLayout();
		if (layout != null) {
			int scrollDelta = layout.getLineBottom(mTextViewBytesLog
					.getLineCount() - 1)
					- mTextViewBytesLog.getScrollY()
					- mTextViewBytesLog.getHeight();
			if (scrollDelta > 0)
				mTextViewBytesLog.scrollBy(0, scrollDelta);
		}

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