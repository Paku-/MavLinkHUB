package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.interfaces.IDataUpdateSysLog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentSysLog extends HUBFragment implements IDataUpdateSysLog {

	@SuppressWarnings("unused")
	private static final String TAG = FragmentSysLog.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_sys_log, container, false);

		return rootView;

		// final TextView txtView = (TextView)
		// (getView().findViewById(R.id.TextView_logSysLog));
		// Typeface externalFont = Typeface.createFromAsset(
		// hub.getAssets(), "fonts/Roboto-Condensed.ttf");
		// txtView.setTypeface(externalFont);
		// txtView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
	}

	@Override
	public void onResume() {
		super.onResume();
		HUBGlobals.messenger.register(this, APP_STATE.MSG_DATA_UPDATE_SYSLOG);
		onDataUpdateSysLog();
	}

	@Override
	public void onPause() {
		super.onPause();
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DATA_UPDATE_SYSLOG);
	}

	@Override
	public void onDataUpdateSysLog() {
		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.TextView_logSysLog));

		mTextViewBytesLog.setText(HUBGlobals.logger.inMemSysLogBuffer);

		// scroll down
		final ScrollView mScrollView = (ScrollView) (getView().findViewById(R.id.scrollView_logSys));

		mScrollView.post(new Runnable() {
			@Override
			public void run() {
				mScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

}