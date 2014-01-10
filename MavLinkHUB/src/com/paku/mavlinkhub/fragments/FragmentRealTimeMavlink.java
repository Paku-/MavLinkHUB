package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.fragments.viewadapters.ViewAdapterMavlinkMsgList;
import com.paku.mavlinkhub.fragments.viewadapters.items.ItemMavLinkMsg;
import com.paku.mavlinkhub.interfaces.IByteLogDataLoggedIn;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentRealTimeMavlink extends HUBFragment implements IByteLogDataLoggedIn {

	@SuppressWarnings("unused")
	private static final String TAG = "FragmentRealTimeMavlink";

	ViewAdapterMavlinkMsgList mavlinkListAdapter;
	ListView mavlinkMsgListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_realtime_mavlink_msglist, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final TextView mTextViewLogStats = (TextView) (getView().findViewById(R.id.textView_logStatsbar));
		mTextViewLogStats.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));
		mTextViewBytesLog.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);

		mavlinkListAdapter = new ViewAdapterMavlinkMsgList(this.getActivity(), generateMavlinkListData());

		mavlinkMsgListView = (ListView) (getView().findViewById(R.id.listView_mavlinkMsgs));
		mavlinkMsgListView.setAdapter(mavlinkListAdapter);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		globalVars.messanger.registerForOnByteLogDataLoggedIn(this);
		refreshUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		globalVars.messanger.unregisterFromOnByteLogDataLoggedIn(this);
	}

	public void refreshUI() {

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));

		String buff;

		// get last n kb of data
		if (globalVars.logger.mInMemIncomingBytesStream.size() > globalVars.visibleBuffersSize) {
			buff = new String(globalVars.logger.mInMemIncomingBytesStream.toByteArray(),
					globalVars.logger.mInMemIncomingBytesStream.size() - globalVars.visibleBuffersSize,
					globalVars.visibleBuffersSize);
		}
		else {
			buff = new String(globalVars.logger.mInMemIncomingBytesStream.toByteArray());

		}

		mTextViewBytesLog.setText(buff);

		// scroll down
		final ScrollView mScrollView = (ScrollView) (getView().findViewById(R.id.scrollView_logByte));

		if (mScrollView != null) {

			mScrollView.post(new Runnable() {
				@Override
				public void run() {
					mScrollView.fullScroll(View.FOCUS_DOWN);
				}
			});

		}

		// stats bar
		final TextView mTextViewLogStats = (TextView) (getView().findViewById(R.id.textView_logStatsbar));

		mTextViewLogStats.setText(globalVars.mMavLinkCollector.getLastParserStats());

		mavlinkListAdapter.clear();
		mavlinkListAdapter.addAll(generateMavlinkListData());
		mavlinkMsgListView.setSelection(mavlinkListAdapter.getCount());

	}

	// get data to fill the list view
	private ArrayList<ItemMavLinkMsg> generateMavlinkListData() {

		// limit size
		// / failing pku
		while (globalVars.logger.mavlinkMsgItemsArray.size() > globalVars.visibleMsgList)
			globalVars.logger.mavlinkMsgItemsArray.remove(0);

		// flush mem
		globalVars.logger.mavlinkMsgItemsArray.trimToSize();

		// we need a clone for adapter.
		ArrayList<ItemMavLinkMsg> clone = new ArrayList<ItemMavLinkMsg>();
		clone.addAll(globalVars.logger.mavlinkMsgItemsArray);

		return clone;
	}

	@Override
	public void onByteLogDataLoggedIn() {
		refreshUI();
	}

}