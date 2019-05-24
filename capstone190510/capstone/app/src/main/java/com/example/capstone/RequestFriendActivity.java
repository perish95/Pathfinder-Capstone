package com.example.capstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//친구요청목록 보여주는 액티비티, ListViewAdapter에서 한줄 한줄 레이아웃 관리
public class RequestFriendActivity extends AppCompatActivity {
    private ListView requestList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friend);

        ArrayList<String> temp = new ArrayList<String>();
        temp.add("Yumi");
        temp.add("Sibal");
        temp.add("Nabi");
        temp.add("tang");

        requestList = (ListView)findViewById(R.id.RequestList);
        ListViewAdapter adapter = new ListViewAdapter(temp);
        requestList.setAdapter(adapter);

    }
}
