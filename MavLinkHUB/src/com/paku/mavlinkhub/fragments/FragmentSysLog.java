package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.interfaces.IDataUpdateStats;
import com.paku.mavlinkhub.interfaces.IDataUpdateSysLog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentSysLog extends HUBFragment implements IDataUpdateSysLog, IDataUpdateStats {

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

	}

	@Override
	public void onResume() {
		super.onResume();
		globalVars.messanger.registerForOnDataUpdateSysLog(this);
		refreshUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		globalVars.messanger.unregisterFromOnDataUpdateSysLog(this);
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

	}

	@Override
	public void onDataUpdateSysLog() {

		refreshUI();
	}

	@Override
	public void onDataUpdateStats() {
	}

}