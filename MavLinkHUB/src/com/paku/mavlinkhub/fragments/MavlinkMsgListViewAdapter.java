package com.paku.mavlinkhub.fragments;

import java.util.ArrayList;

import com.paku.mavlinkhub.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MavlinkMsgListViewAdapter extends ArrayAdapter<MavlinkMsgItem> {

	
	private final Context context;
    private final ArrayList<MavlinkMsgItem> itemsArrayList;

    public MavlinkMsgListViewAdapter(Context context, ArrayList<MavlinkMsgItem> itemsArrayList) {

        super(context, R.layout.listviewitem_mavlinkmsg, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }
    
    
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.listviewitem_mavlinkmsg, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.textView1);
        TextView valueView = (TextView) rowView.findViewById(R.id.textView2);

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getDescription());

        // 5. Return rowView
        return rowView;
    }
    
    
	
	
}
