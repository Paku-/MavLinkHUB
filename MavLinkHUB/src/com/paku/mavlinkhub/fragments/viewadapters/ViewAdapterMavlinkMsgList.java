package com.paku.mavlinkhub.fragments.viewadapters;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.mavlink.MavLinkClassExtractor;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsgTxt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAdapterMavlinkMsgList extends ArrayAdapter<ItemMavLinkMsg> {

	private final Context context;
	private final ArrayList<ItemMavLinkMsg> itemsArrayList;
	private HUBGlobals app;

	// mavlink classes' string names helper class
	private MavLinkClassExtractor mavClasses;

	public ViewAdapterMavlinkMsgList(Context context, ArrayList<ItemMavLinkMsg> itemsArrayList) {

		super(context, R.layout.listviewitem_mavlinkmsg, itemsArrayList);

		this.context = context;
		app = ((HUBGlobals) context.getApplicationContext());
		this.itemsArrayList = itemsArrayList;

		// mavlink msgs fields name reference object
		this.mavClasses = new MavLinkClassExtractor();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View msgItemView = inflater.inflate(R.layout.listviewitem_mavlinkmsg, parent, false);

		TextView msgName = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_Text1);
		TextView mainText = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_Text2);
		TextView desc1 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc1);
		TextView desc2 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc2);
		TextView desc3 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc3);

		ItemMavLinkMsgTxt msgTxtItem = new ItemMavLinkMsgTxt(itemsArrayList.get(position), mavClasses);

		msgName.setText(msgTxtItem.msgName);
		mainText.setText(msgTxtItem.mainTxt);
		desc1.setText(msgTxtItem.desc_1);
		desc2.setText(msgTxtItem.desc_2);
		desc3.setText(msgTxtItem.desc_3);

		if (msgTxtItem.direction == MSG_SOURCE.FROM_GS) {
			msgItemView.setBackgroundColor(Color.parseColor(app.colLight));
		}

		return msgItemView;
	}

}
