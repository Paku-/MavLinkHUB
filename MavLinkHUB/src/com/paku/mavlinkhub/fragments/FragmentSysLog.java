package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.interfaces.ISysLogDataLoggedIn;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentSysLog extends HUBFragment implements ISysLogDataLoggedIn {

	@SuppressWarnings("unused")
	private static final String TAG = "FragmentSysLog";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_sys_log, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final TextView txtView = (TextView) (getView().findViewById(R.id.TextView_logSysLog));
		// Typeface externalFont = Typeface.createFromAsset(
		// globalVars.getAssets(), "fonts/Roboto-Condensed.ttf");
		// txtView.setTypeface(externalFont);
		txtView.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

		final TextView mTextViewLogStats = (TextView) (getView().findViewById(R.id.textView_logSysLogStatsbar));
		// Typeface externalFontStats = Typeface.createFromAsset(
		// globalVars.getAssets(), "fonts/Roboto-Regular.ttf");
		mTextViewLogStats.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

	}

	@Override
	public void onResume() {
		super.onResume();
		globalVars.messanger.registerForOnSysLogDataLoggedIn(this);
		refreshUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		globalVars.messanger.unregisterFromOnSysLogDataLoggedIn(this);
	}

	public void refreshUI() {

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.TextView_logSysLog));

		String buff;

		if (globalVars.logger.mInMemSysLogStream.size() > globalVars.visibleBuffersSize) {
			buff = new String(globalVars.logger.mInMemSysLogStream.toByteArray(),
					globalVars.logger.mInMemSysLogStream.size() - globalVars.visibleBuffersSize,
					globalVars.visibleBuffersSize);
		}
		else {
			buff = new String(globalVars.logger.mInMemSysLogStream.toByteArray());

		}

		mTextViewBytesLog.setText(buff);

		// scroll down
		final ScrollView mScrollView = (ScrollView) (getView().findViewById(R.id.scrollView_logSys));

		mScrollView.post(new Runnable() {
			@Override
			public void run() {
				mScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});

		final TextView mTextViewLogStats = (TextView) (getView().findViewById(R.id.textView_logSysLogStatsbar));

		mTextViewLogStats.setText(globalVars.mMavLinkCollector.getLastParserStats());

	}

	@Override
	public void onSysLogDataLoggedIn() {
		refreshUI();
	}

}