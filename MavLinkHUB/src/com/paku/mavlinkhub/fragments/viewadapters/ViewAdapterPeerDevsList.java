package com.paku.mavlinkhub.fragments.viewadapters;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.communication.devicelist.ItemPeerDevice;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAdapterPeerDevsList extends ArrayAdapter<ItemPeerDevice> {

	private final Context context;
	private final ArrayList<ItemPeerDevice> itemsArrayList;

	public ViewAdapterPeerDevsList(Context context, ArrayList<ItemPeerDevice> itemsArrayList) {

		super(context, R.layout.listviewitem_mavlinkmsg, itemsArrayList);

		this.context = context;
		this.itemsArrayList = itemsArrayList;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.listviewitem_bt_devs, parent, false);

		TextView txtViewDevName = (TextView) rowView.findViewById(R.id.listViewItemTxt_dev_name);
		TextView txtViewDevAddress = (TextView) rowView.findViewById(R.id.listViewItemTxt_dev_address);

		// TextView devAddress = (TextView)
		// rowView.findViewById(android.R.id.text1);
		/*
		 * TextView mainText = (TextView)
		 * rowView.findViewById(R.id.listViewItemTxt_mainText); TextView desc1 =
		 * (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_1);
		 * TextView desc2 = (TextView)
		 * rowView.findViewById(R.id.listViewItemTxt_desc_2); TextView desc3 =
		 * (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_3);
		 */

		ItemPeerDevice dev = itemsArrayList.get(position);

		txtViewDevName.setText(dev.getName());
		txtViewDevAddress.setText(dev.getAddress());

		// desc1.setText(msgTxtItem.getDesc_1());
		// desc2.setText(msgTxtItem.getDesc_2());
		// desc3.setText(msgTxtItem.getDesc_3());

		if (dev.getState() == PEER_DEV_STATE.DEV_STATE_CONNECTED) {

			txtViewDevName.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
			txtViewDevName.setTextColor(Color.RED);
			// txtView.setBackgroundColor(0x3064FF);
			// 3064FF

		}
		else {

			txtViewDevName.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
			txtViewDevName.setTextColor(Color.BLACK);
			// txtView.setBackgroundColor(Color.WHITE);

		}

		return rowView;
	}

}
