package com.paku.mavlinkhub.fragments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.interfaces.IDataLoggedIn;
import com.paku.mavlinkhub.mavlink.MavLinkMsgItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RealTimeMavlinkFragment extends Fragment implements IDataLoggedIn {

	@SuppressWarnings("unused")
	private static final String TAG = "RealTimeMavlinkFragment";
	private AppGlobals globalVars;
	
	MavlinkMsgListViewAdapter mavlinkListAdapter;
	ListView mavlinkMsgListView;

	public RealTimeMavlinkFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		globalVars = (AppGlobals) getActivity().getApplication();        

	}
	
	//get data to fill the list view
	private ArrayList<MavLinkMsgItem> generateMavlinkListData() {		
		ArrayList<MavLinkMsgItem> tempArray = new ArrayList<MavLinkMsgItem>();
		MavLinkMsgItem tempMsg;
		
		ByteArrayInputStream msgByteInputStream = new ByteArrayInputStream(globalVars.logger.mInMemMsgBackgroundStream.toByteArray());
		

		
		try {
			ObjectInputStream msgObjectInputStream = new ObjectInputStream(msgByteInputStream);
			if (msgByteInputStream.available()>0)
			while ((tempMsg = (MavLinkMsgItem) msgObjectInputStream.readObject()) != null) {
				//tempArray.add(new MavLinkMsgItem(0,tempMsg.toString(), "description","3","4","5"));
				tempArray.add(tempMsg);
			}								
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return tempArray;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(
				R.layout.fragment_realtime_mavlink_msglist, container, false);

		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mavlinkListAdapter = new MavlinkMsgListViewAdapter (this.getActivity(), generateMavlinkListData());
		
        mavlinkMsgListView = (ListView) (getView().findViewById(R.id.listView_mavlinkMsgs));
        mavlinkMsgListView.setAdapter(mavlinkListAdapter);		
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
		
		//get last n kb of data
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

		mTextViewLogStats.setText(globalVars.mMavLinkCollector.getLastParserStats());
		
		//msgs listview
		//mavlinkListAdapter.notifyDataSetChanged();
		//mavlinkMsgListView.setVisibility(ListView.INVISIBLE);
		//mavlinkMsgListView.setVisibility(ListView.VISIBLE);
		//mavlinkMsgListView.
		/*
		mavlinkMsgListView.post(new Runnable() {
			@Override
			public void run() {				
				mavlinkListAdapter.notifyDataSetChanged();
				//mavlinkMsgListView.setSelection(1);
				mavlinkMsgListView.invalidateViews();
				//mavlinkMsgListView.setVisibility(ListView.INVISIBLE);
				//mavlinkMsgListView.setVisibility(ListView.VISIBLE);				
			}
		});
*/
		mavlinkListAdapter.clear();
		mavlinkListAdapter.addAll(generateMavlinkListData());
		mavlinkListAdapter.notifyDataSetChanged();
		mavlinkMsgListView.setSelection(mavlinkListAdapter.getCount());
		
		
	}

	@Override
	public void onDataLoggedIn() {
		// Log.d(TAG,
		// "[ByteLog]"+globalVars.logger.mInMemIncomingBytesStream.size());
		refreshUI();
	}

}