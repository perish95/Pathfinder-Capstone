package com.example.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import static android.R.layout.simple_list_item_1;

public class FriendActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_friend);

        Intent recv = getIntent();//SearchFriendActivity에서 데이터를 받는 intent 객체
        //user = (User) recv.getParcelableExtra("User");
        if ((User)recv.getSerializableExtra("sentUser") != null)
            Log.d("CHECK", "catch : " + (User)recv.getSerializableExtra("User"));

        Button addButton = (Button)findViewById(R.id.addButton);
        String[] temp = { "버너스리","장노이만", "박욘베", "킴선동", "안산피앙세", "우산피앙세","우산피앙세","우산피앙세","우산피앙세","우산피앙세","우산피앙세","우산피앙세","우산피앙세","우산피앙세"};
        ArrayList<String> items = new ArrayList<>();

        Collections.addAll(items, temp); //test용
        ListAdapter adapter = new ArrayAdapter<String>(this, simple_list_item_1, items);
        final ListView listView = (ListView) findViewById(R.id.friendListView);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchFriendActivity.class);
                startActivity(intent);
            }
        });


        if(recv.getStringExtra("deli") != null) {
            Log.d("확인", "객체확인" + recv);
            Log.d("test", "stinrg" + recv.getStringExtra("deli"));
            String input = recv.getStringExtra("deli");
            items.add(input); // 지금은 확인하는 용도라 로컬에만 추가해서 2개 이상 업데이트가 안됨.
            ((ArrayAdapter) adapter).notifyDataSetChanged();
        }


        /*listView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                friendScroll.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });*/


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String item = String.valueOf(parent.getItemAtPosition(i));
                DialogRequest(item);
                //Toast.makeText(FriendActivity.this, item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DialogRequest(String partner){
        AlertDialog.Builder requestAlt = new AlertDialog.Builder(this);
        //int currentPos = getPosition(); currentPos는 현재 자신의 위치 뒤에 들어갈 예정
        requestAlt.setMessage("현재 자신의 위치\n" + partner + "님에게 만남을 신청하시겠습니까?").setCancelable(false).setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click "yes"
                        //Intent sent = new Intent(getApplicationContext(), 클래스명);
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click "No"
                dialog.cancel();
            }
        }).setNeutralButton("위치 재검색", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click "Research"
                //getPosition();
            }
        });

        AlertDialog alert = requestAlt.create();
        alert.setTitle("Test");
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.show();
    }

    void getPosition(){
        //메소드 타입은 위치 정보의 타입에 따라 갈림
    }
}
