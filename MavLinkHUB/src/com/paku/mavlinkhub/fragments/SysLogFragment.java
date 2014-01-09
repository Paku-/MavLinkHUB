package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.interfaces.IDataLoggedIn;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class SysLogFragment extends Fragment implements IDataLoggedIn {

	@SuppressWarnings("unused")
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

		final TextView txtView = (TextView) (getView()
				.findViewById(R.id.TextView_logSysLog));
		//Typeface externalFont = Typeface.createFromAsset(
		//		globalVars.getAssets(), "fonts/Roboto-Condensed.ttf");
		//txtView.setTypeface(externalFont);
		txtView.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);

		final TextView mTextViewLogStats = (TextView) (getView()
				.findViewById(R.id.textView_logSysLogStatsbar));		
		//Typeface externalFontStats = Typeface.createFromAsset(
				//globalVars.getAssets(), "fonts/Roboto-Regular.ttf");
		mTextViewLogStats.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
		

	}

	@Override
	public void onResume() {
		super.onResume();
		globalVars.logger.registerSysLogForIDataLoggedIn(this);
		refreshUI();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		globalVars.logger.unregisterSysLogForIDataLoggedIn();
		super.onPause();
	}

	public void refreshUI() {

		final TextView mTextViewBytesLog = (TextView) (getView()
				.findViewById(R.id.TextView_logSysLog));

		String buff;

		if (globalVars.logger.mInMemSysLogStream.size() > globalVars.visibleBuffersSize) {
			buff = new String(
					globalVars.logger.mInMemSysLogStream.toByteArray(),
					globalVars.logger.mInMemSysLogStream.size()
							- globalVars.visibleBuffersSize,
					globalVars.visibleBuffersSize);
		} else {
			buff = new String(
					globalVars.logger.mInMemSysLogStream.toByteArray());

		}

		mTextViewBytesLog.setText(buff);

		// scroll down
		final ScrollView mScrollView = (ScrollView) (getView()
				.findViewById(R.id.scrollView_logSys));

		mScrollView.post(new Runnable() {
			@Override
			public void run() {
				mScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});

		final TextView mTextViewLogStats = (TextView) (getView()
				.findViewById(R.id.textView_logSysLogStatsbar));

		mTextViewLogStats.setText(globalVars.mMavLinkCollector
				.getLastParserStats());

	}

	@Override
	public void onDataLoggedIn() {
		refreshUI();
	}

}