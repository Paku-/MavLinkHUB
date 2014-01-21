package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.fragments.viewadapters.ViewAdapterMavlinkMsgList;
import com.paku.mavlinkhub.interfaces.IDataUpdateByteLog;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemReady;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;

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

public class FragmentRealTimeMavlink extends HUBFragment implements IDataUpdateByteLog, IQueueMsgItemReady {

	@SuppressWarnings("unused")
	private static final String TAG = "FragmentRealTimeMavlink";

	ViewAdapterMavlinkMsgList listAdapterMavLink;
	ListView listViewMavLinkMsg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_realtime_mavlink_msglist, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listAdapterMavLink = new ViewAdapterMavlinkMsgList(this.getActivity(), new ArrayList<ItemMavLinkMsg>());
		listViewMavLinkMsg = (ListView) (getView().findViewById(R.id.listView_mavlinkMsgs));
		listViewMavLinkMsg.setAdapter(listAdapterMavLink);
	}

	@Override
	public void onResume() {
		super.onResume();

		hub.messenger.register(this, APP_STATE.MSG_DATA_UPDATE_BYTELOG);
		hub.messenger.register(this, APP_STATE.MSG_QUEUE_MSGITEM_READY);

		// GUI update
		onDataUpdateByteLog();
		onQueueMsgItemReady(null);

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));
		mTextViewBytesLog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// switch autoscroll state
				hub.prefs.edit().putBoolean("pref_byte_log_autoscroll", !hub.prefs.getBoolean("pref_byte_log_autoscroll", true)).commit();
			}
		});

		final ListView mListViewMsgItems = (ListView) (getView().findViewById(R.id.listView_mavlinkMsgs));
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
		hub.messenger.unregister(this, APP_STATE.MSG_DATA_UPDATE_BYTELOG);
		hub.messenger.unregister(this, APP_STATE.MSG_QUEUE_MSGITEM_READY);
	}

	@Override
	public void onDataUpdateByteLog() {

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));

		mTextViewBytesLog.setText(hub.logger.getByteLog());

		if (hub.prefs.getBoolean("pref_byte_log_autoscroll", true)) {
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

		}

	}

	@Override
	public void onQueueMsgItemReady(ItemMavLinkMsg msgItem) {

		if (msgItem != null) {
			listAdapterMavLink.add(msgItem);

			while (listAdapterMavLink.getCount() > hub.visibleMsgList) {
				listAdapterMavLink.remove(listAdapterMavLink.getItem(0));
			}
		}
		else
			Log.d(TAG, "Null msgItem");

		// scroll down on pref
		if (hub.prefs.getBoolean("pref_msg_items_autoscroll", true)) {
			listViewMavLinkMsg.setSelection(listAdapterMavLink.getCount());
		}

	}
}