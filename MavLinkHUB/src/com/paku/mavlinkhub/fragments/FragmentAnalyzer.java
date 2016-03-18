package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.interfaces.IDataUpdateByteLog;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemReady;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;
import com.paku.mavlinkhub.viewadapters.ViewAdapterAnalyzerList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class FragmentAnalyzer extends HUBFragment implements IDataUpdateByteLog, IQueueMsgItemReady {

	private static final String TAG = FragmentAnalyzer.class.getSimpleName();

	private ViewAdapterAnalyzerList listAdapterAnalyzer;
	private ListView listViewAnalyzer;

	private TextView textViewNoData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_analyzer, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		textViewNoData = (TextView) (getView().findViewById(R.id.textView_analyzer_no_data));

		listAdapterAnalyzer = new ViewAdapterAnalyzerList(hub, new ArrayList<ItemMavLinkMsg>());
		listViewAnalyzer = (ListView) (getView().findViewById(R.id.listView_analyzer_msg_list));
		listViewAnalyzer.setAdapter(listAdapterAnalyzer);
	}

	@Override
	public void onResume() {
		super.onResume();

		HUBGlobals.messenger.register(this, APP_STATE.MSG_DATA_UPDATE_BYTELOG);
		HUBGlobals.messenger.register(this, APP_STATE.MSG_QUEUE_MSGITEM_READY);

		// GUI update
		onDataUpdateByteLog();
		onQueueMsgItemReady(null);

		// byte log display clicks
		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));
		mTextViewBytesLog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// switch autoscroll state
				hub.prefs.edit().putBoolean("pref_byte_log_autoscroll", !hub.prefs.getBoolean("pref_byte_log_autoscroll", true)).commit();
			}
		});

		// analyzer msgs display clicks
		final ListView mListViewMsgItems = (ListView) (getView().findViewById(R.id.listView_analyzer_msg_list));
		mListViewMsgItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				// switch autoscroll state
				hub.prefs.edit().putBoolean("pref_msg_items_autoscroll", !hub.prefs.getBoolean("pref_msg_items_autoscroll", true)).commit();
				// Object listItem = list.getItemAtPosition(position);
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_DATA_UPDATE_BYTELOG);
		HUBGlobals.messenger.unregister(this, APP_STATE.MSG_QUEUE_MSGITEM_READY);
	}

	@Override
	public void onDataUpdateByteLog() {

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));

		mTextViewBytesLog.setText(HUBGlobals.logger.getByteLog());

		if (hub.prefs.getBoolean("pref_byte_log_autoscroll", true)) {
			// scroll down
			final ScrollView mScrollView = (ScrollView) (getView().findViewById(R.id.scrollView_logByte));
			if (null != mScrollView) {
				mScrollView.post(new Runnable() {
					@Override
					public void run() {
						mScrollView.fullScroll(View.FOCUS_DOWN);
					}
				});
			}

		}

	}

	@Override
	public void onQueueMsgItemReady(ItemMavLinkMsg msgItem) {

		if (null != msgItem) {
			listAdapterAnalyzer.add(msgItem);

			textViewNoData.setVisibility(View.GONE);

		}
		else {
			Log.d(TAG, "Null msgItem");
		}

		// scroll down on pref
		if (hub.prefs.getBoolean("pref_msg_items_autoscroll", true)) {
			// trim only if autoscroll enabled
			while (listAdapterAnalyzer.getCount() > HUBGlobals.visibleMsgList) {
				listAdapterAnalyzer.remove(listAdapterAnalyzer.getItem(0));
			}

			listViewAnalyzer.setSelection(listAdapterAnalyzer.getCount());
		}

	}
}