package com.paku.mavlinkhub.fragments;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.APP_STATE;
import com.paku.mavlinkhub.fragments.viewadapters.ViewAdapterMavlinkMsgList;
import com.paku.mavlinkhub.interfaces.IDataUpdateByteLog;
import com.paku.mavlinkhub.interfaces.IQueueMsgItemReady;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

		listAdapterMavLink = new ViewAdapterMavlinkMsgList(this.getActivity(), hub.queue.getListMsgItemsForUI());
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
		onQueueMsgItemReady();

		final TextView mTextViewBytesLog = (TextView) (getView().findViewById(R.id.textView_logByte));
		mTextViewBytesLog.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				;

				return false;
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

	@Override
	public void onQueueMsgItemReady() {

		listAdapterMavLink.clear();
		listAdapterMavLink.addAll(hub.queue.getListMsgItemsForUI());
		listViewMavLinkMsg.setSelection(listAdapterMavLink.getCount());

	}

}