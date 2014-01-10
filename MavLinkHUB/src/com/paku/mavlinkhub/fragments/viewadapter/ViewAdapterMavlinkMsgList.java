package com.paku.mavlinkhub.fragments.viewadapter;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.mavlink.MavLinkClassExtractor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAdapterMavlinkMsgList extends ArrayAdapter<ItemMavLinkMsg> {

	private final Context context;
	private final ArrayList<ItemMavLinkMsg> itemsArrayList;

	// mavlink classes' string names helper class
	public MavLinkClassExtractor mavClasses;

	public ViewAdapterMavlinkMsgList(Context context, ArrayList<ItemMavLinkMsg> itemsArrayList) {

		super(context, R.layout.listviewitem_mavlinkmsg, itemsArrayList);

		this.context = context;
		this.itemsArrayList = itemsArrayList;

		// mavlink msgs fields name reference object
		this.mavClasses = new MavLinkClassExtractor();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.listviewitem_mavlinkmsg, parent, false);

		TextView msgName = (TextView) rowView.findViewById(R.id.listViewMsgItemTxt_Text1);
		TextView mainText = (TextView) rowView.findViewById(R.id.listViewMsgItemTxt_Text2);
		TextView desc1 = (TextView) rowView.findViewById(R.id.listViewMsgItemTxt_desc1);
		TextView desc2 = (TextView) rowView.findViewById(R.id.listViewItemTxt_dev_desc_2);
		TextView desc3 = (TextView) rowView.findViewById(R.id.listViewMsgItemTxt_desc3);

		ItemMavLinkMsgTxt msgTxtItem = new ItemMavLinkMsgTxt(itemsArrayList.get(position), mavClasses);

		msgName.setText(msgTxtItem.getName());
		mainText.setText(msgTxtItem.getMainTxt());
		desc1.setText(msgTxtItem.getDesc_1());
		desc2.setText(msgTxtItem.getDesc_2());
		desc3.setText(msgTxtItem.getDesc_3());

		return rowView;
	}

}
