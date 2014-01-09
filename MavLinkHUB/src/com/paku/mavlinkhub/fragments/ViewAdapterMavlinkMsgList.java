package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.mavlink.MavLinkClassExtractor;
import com.paku.mavlinkhub.mavlink.MavLinkMsgItem;
import com.paku.mavlinkhub.mavlink.MavLinkMsgTxtItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewAdapterMavlinkMsgList extends ArrayAdapter<MavLinkMsgItem> {

	
	private final Context context;
    private final ArrayList<MavLinkMsgItem> itemsArrayList;
    
	// mavlink classes' string names helper class
	public MavLinkClassExtractor mavClasses;


    public ViewAdapterMavlinkMsgList(Context context, ArrayList<MavLinkMsgItem> itemsArrayList) {

        super(context, R.layout.listviewitem_mavlinkmsg, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
		
        // mavlink msgs fields name reference object
		this.mavClasses = new MavLinkClassExtractor();

        
    }
    
    
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listviewitem_mavlinkmsg, parent, false);
        
        TextView msgName = (TextView) rowView.findViewById(R.id.listViewItemTxt_msgName);
        TextView mainText = (TextView) rowView.findViewById(R.id.listViewItemTxt_mainText);
        TextView desc1 = (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_1);
        TextView desc2 = (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_2);
        TextView desc3 = (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_3);
        
        MavLinkMsgTxtItem msgTxtItem = new MavLinkMsgTxtItem(itemsArrayList.get(position),mavClasses);

        msgName.setText(msgTxtItem.getName());
        mainText.setText(msgTxtItem.getMainTxt());
        desc1.setText(msgTxtItem.getDesc_1());
        desc2.setText(msgTxtItem.getDesc_2());
        desc3.setText(msgTxtItem.getDesc_3());

        return rowView;
    }
    
    
	
	
}
