package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;
import com.paku.mavlinkhub.mavlink.MavLinkMsgItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MavlinkMsgListViewAdapter extends ArrayAdapter<MavLinkMsgItem> {

	
	private final Context context;
    private final ArrayList<MavLinkMsgItem> itemsArrayList;

    public MavlinkMsgListViewAdapter(Context context, ArrayList<MavLinkMsgItem> itemsArrayList) {

        super(context, R.layout.listviewitem_mavlinkmsg, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }
    
    
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listviewitem_mavlinkmsg, parent, false);
        
        TextView labelView = (TextView) rowView.findViewById(R.id.listViewItemTxt_mainText);
        TextView valueView1 = (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_1);
        TextView valueView2 = (TextView) rowView.findViewById(R.id.listViewItemTxt_desc_2);


        labelView.setText(itemsArrayList.get(position).getMainText());
        valueView1.setText(itemsArrayList.get(position).getDescription_1());
        valueView2.setText(itemsArrayList.get(position).getDescription_2());

        return rowView;
    }
    
    
	
	
}
