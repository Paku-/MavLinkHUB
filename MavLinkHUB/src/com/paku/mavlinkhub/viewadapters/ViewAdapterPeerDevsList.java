// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.PEER_DEV_STATE;
import com.paku.mavlinkhub.viewadapters.devicelist.ItemPeerDevice;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewAdapterPeerDevsList extends ArrayAdapter<ItemPeerDevice> {

	private final HUBGlobals hub;
	private final ArrayList<ItemPeerDevice> itemsArrayList;

	public ViewAdapterPeerDevsList(HUBGlobals hub, ArrayList<ItemPeerDevice> itemsArrayList) {

		super(hub, R.layout.listviewitem_select_device_row, itemsArrayList);

		this.hub = hub;
		this.itemsArrayList = itemsArrayList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final LayoutInflater inflater = (LayoutInflater) hub.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.listviewitem_select_device_row, parent, false);

		final TextView txtViewDevName = (TextView) rowView.findViewById(R.id.listViewItemTxt_dev_name);
		final TextView txtViewDevAddress = (TextView) rowView.findViewById(R.id.listViewItemTxt_dev_address);
		final ImageView imageConnected = (ImageView) rowView.findViewById(R.id.imageView_conn_endpoint_kind);

		final ItemPeerDevice dev = itemsArrayList.get(position);

		txtViewDevName.setText(dev.getName());
		txtViewDevAddress.setText(dev.getAddress());

		// desc1.setText(msgTxtItem.getDesc_1());
		// desc2.setText(msgTxtItem.getDesc_2());
		// desc3.setText(msgTxtItem.getDesc_3());

		if (dev.getState() == PEER_DEV_STATE.DEV_STATE_CONNECTED) {

			txtViewDevName.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
			txtViewDevName.setTextColor(hub.getResources().getColor(R.color.hubDark));
			txtViewDevName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
			imageConnected.setVisibility(View.VISIBLE);

		}
		else {

			txtViewDevName.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
			txtViewDevName.setTextColor(hub.getResources().getColor(R.color.system_icons));
			txtViewDevName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
			imageConnected.setVisibility(View.INVISIBLE);

		}

		return rowView;
	}

}
