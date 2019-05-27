package com.example.capstone;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    LayoutInflater infalter = null;
    private int listCount = 0;
    private ArrayList<ItemData> m_oData = null;
    // ListViewBtnAdapter 생성자. 마지막에 ListBtnClickListener 추가.
    /*
    public ListViewAdapter(ArrayList<String> temp){
        waitList = temp;
        listCount = waitList.size();
    }
    */
    public ListViewAdapter(ArrayList<ItemData> _oData)
    {
        m_oData = _oData;
        listCount = m_oData.size();
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

        if(!m_oData.isEmpty()) { //리스트뷰 갱신할때 리스트가 1개있을때 삭제하면 0개가 되서 오류가 나므로 조건
            TextView oTextTitle = (TextView) convertView.findViewById(R.id.FriendNameText);
            Button oBtn = (Button) convertView.findViewById(R.id.AddFriendButton);
            Button cBtn = (Button) convertView.findViewById(R.id.FriendCancelButton);
            oTextTitle.setText(m_oData.get(position).strTitle);
            oBtn.setTag("1");
            cBtn.setTag("2");
            oBtn.setOnClickListener(m_oData.get(position).onClickListener);
            cBtn.setOnClickListener(m_oData.get(position).onClickListener);
            convertView.setTag("" + position);
        }
        return convertView;
    }

}


