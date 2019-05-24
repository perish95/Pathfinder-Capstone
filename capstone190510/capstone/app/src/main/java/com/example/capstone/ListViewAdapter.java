package com.example.capstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    LayoutInflater infalter = null;
    private ArrayList<String> waitList = null;
    private int listCount = 0;

    public ListViewAdapter(ArrayList<String> temp){
        waitList = temp;
        listCount = waitList.size();
    }

    @Override
    public int getCount() {
        return listCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            final Context context = parent.getContext();
            if(infalter == null){
                infalter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = infalter.inflate(R.layout.listview_item, parent, false);
        }
        TextView friendNameText = (TextView) convertView.findViewById(R.id.FriendNameText);
        friendNameText.setText(waitList.get(position));
        return convertView;
    }
}
