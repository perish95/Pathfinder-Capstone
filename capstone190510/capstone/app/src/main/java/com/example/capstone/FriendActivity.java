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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import static android.R.layout.simple_list_item_1;

public class FriendActivity extends AppCompatActivity {
    private User user;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");

    @Override
    protected void onCreate(Bundle bundle){
        Log.v("확인","FriendActivity 시작 확인 ***********************************");
        super.onCreate(bundle);
        setContentView(R.layout.activity_friend);

        Intent recv = getIntent();//SearchFriendActivity에서 데이터를 받는 intent 객체
        Button addButton = (Button)findViewById(R.id.addButton);
        ArrayList<String> items = new ArrayList<>(); //친구목록을 출력하기 위한 arraylist

        //MainActivity에서 user 객체 값 받는 과정
        if ((User)recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            //databaseReference.child(user.get_nickname()).child("_friendList").setValue(user._friendList);
            databaseReference.child(user.get_nickname()).child("friendsMap").setValue(user.friendsMap);
            Log.d("CHECK","[FriendActivity]catch : " + user.friendsMap);
        }

        //친구리스트 만드는 과정
        Set key = user.friendsMap.keySet();

        for(Iterator it = key.iterator();it.hasNext();){
            String keyName = (String)it.next();
            Log.d("CHECK","[FriendActivity]catch : keyName = " + keyName);
            if(keyName.equals(" "))
                continue;
            else
                items.add((String)user.friendsMap.get(keyName));
        }

        Log.d("CHECK","[FriendActivity]catch : keyName = " + items); //잘 만들어졌는지 확인

        ListAdapter adapter = new ArrayAdapter<String>(this, simple_list_item_1, items);
        final ListView listView = (ListView) findViewById(R.id.friendListView);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchFriendActivity.class);
                //Intent intent = new Intent(getApplicationContext() , FriendActivity.class);
                intent.putExtra("SentUser" ,user); //FriendActivity에 user값 전달
                startActivity(intent);
            }
        });


        if(recv.getStringExtra("deli") != null) {
            Log.d("확인", "객체확인" + recv);
            Log.d("test", "stinrg" + recv.getStringExtra("deli"));
            String input = recv.getStringExtra("deli");
            ((ArrayAdapter) adapter).notifyDataSetChanged();
        }


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

    /*
    @Override
    public void onBackPressed(){
        //Don't go back
    }*/
}
