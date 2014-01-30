// $codepro.audit.disable
// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.alwaysOverridetoString.alwaysOverrideToString
package com.paku.mavlinkhub.viewadapters;

import java.util.ArrayList;

import com.paku.mavlinkhub.HUBGlobals;
import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.enums.MSG_SOURCE;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsg;
import com.paku.mavlinkhub.queue.items.ItemMavLinkMsgTxt;
import com.paku.mavlinkhub.utils.MavLinkClassExtractor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAdapterMavlinkMsgList extends ArrayAdapter<ItemMavLinkMsg> {

	private final ArrayList<ItemMavLinkMsg> itemsArrayList;
	private final HUBGlobals hub;

	// mavlink classes' names helper class
	private final MavLinkClassExtractor mavClasses;

	//anty gc

	private final LayoutInflater inflater;
	private View msgItemView;

	private TextView msgName;
	private TextView mainText;
	private TextView desc1;
	private TextView desc2;
	private TextView desc3;
	private TextView desc4;

	private final ItemMavLinkMsgTxt msgTxtItem;

	public ViewAdapterMavlinkMsgList(HUBGlobals hub, ArrayList<ItemMavLinkMsg> itemsArrayList) {

		super(hub, R.layout.listviewitem_analyzer_msg_row_2, itemsArrayList);

		this.hub = hub;

		this.itemsArrayList = itemsArrayList;

		// mavlink msgs fields name reference object
		mavClasses = new MavLinkClassExtractor();

		inflater = (LayoutInflater) hub.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		msgTxtItem = new ItemMavLinkMsgTxt(null, null);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		msgItemView = inflater.inflate(R.layout.listviewitem_analyzer_msg_row_2, parent, false);

		msgName = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_Text1);
		mainText = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_Text2);
		desc1 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc1);
		desc2 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc2);
		desc3 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc3);
		desc4 = (TextView) msgItemView.findViewById(R.id.listViewMsgItemTxt_desc4);

		msgTxtItem.setMe(itemsArrayList.get(position), mavClasses);

		msgName.setText(msgTxtItem.msgName);
		mainText.setText(msgTxtItem.mainTxt);
		desc1.setText(msgTxtItem.desc_1);
		desc2.setText(msgTxtItem.desc_2);
		desc3.setText(msgTxtItem.desc_3);
		desc4.setText(msgTxtItem.desc_4);

		if (msgTxtItem.direction == MSG_SOURCE.FROM_GS) {
			msgItemView.setBackgroundColor(hub.getResources().getColor(R.color.hubLight));
		}

		return msgItemView;
	}
}
