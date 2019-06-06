package com.example.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static android.R.layout.simple_list_item_1;

public class FriendActivity extends AppCompatActivity {
    private User user;
    private User friend;
    private String friendKey;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
    private DatabaseReference mRef = firebaseDatabase.getReference();
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle bundle) {
        Log.v("확인", "FriendActivity 시작 확인 ***********************************");
        super.onCreate(bundle);
        setContentView(R.layout.activity_friend);

        Intent recv = getIntent();//SearchFriendActivity에서 데이터를 받는 intent 객체
        Button addButton = (Button) findViewById(R.id.addButton);
        Button settingButton = (Button) findViewById(R.id.settingButton);
        Button waitButton = (Button) findViewById(R.id.waitListButton);
        ImageView redCircle = (ImageView) findViewById(R.id.redImage);
        items = new ArrayList<>(); //친구목록을 출력하기 위한 arraylist

        //MainActivity에서 user 객체 값 받는 과정
        if ((User) recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            databaseReference.child(user.get_nickname()).child("friendsMap").setValue(user.friendsMap);
            Log.d("CHECK", "[FriendActivity]catch : " + user.get_nickname());
        }

        //친구요청이 있을 시 빨간 동그라미 나오게함
        if(user.friendRequest.size() <= 1)
            redCircle.setVisibility(View.INVISIBLE);
        else
            redCircle.setVisibility(View.VISIBLE);

        // 친구리스트 만드는 과정 Start
        Set key = user.friendsMap.keySet();

        for (Iterator it = key.iterator(); it.hasNext(); ) {
            String keyName = (String) it.next();
            if (keyName.equals(" "))
                continue;
            else
                items.add((String) user.friendsMap.get(keyName));
        }

        Log.d("CHECK", "[FriendActivity]catch : keyName = " + items); //잘 만들어졌는지 확인

        adapter = new ArrayAdapter<String>(this, simple_list_item_1, items); //Listview에 적용
        final ListView listView = (ListView) findViewById(R.id.friendListView);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchFriendActivity.class);
                //Intent intent = new Intent(getApplicationContext() , FriendActivity.class);
                intent.putExtra("SentUser", user); //FriendActivity에 user값 전달
                startActivity(intent);
            }
        });

        //설정버튼 액션
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetting();
            }
        });

        waitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), RequestFriendActivity.class);
                it.putExtra("SentUser", user);
                startActivity(it);
            }
        });



        updatePromise(); // 약속신청을 받았을 때 구현

        //친구에게 약속신청을 보내는 버튼 액션
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String item = String.valueOf(parent.getItemAtPosition(i));
                dialogRequest(item);
            }
        });
    }

    private void dialogRequest(String partner) {
        AlertDialog.Builder requestAlt = new AlertDialog.Builder(this);
        //int currentPos = getPosition(); //currentPos는 현재 자신의 위치 뒤에 들어갈 예정
        requestAlt.setMessage(partner + "님에게 약속을 신청하시겠습니까?").setCancelable(false).setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click "yes"
                        requestPromise(partner); //서버로 약속 요청을 하는 메소드
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click "No"
                dialog.cancel();
            }
        }).setNeutralButton("친구 삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFriend(partner);
                Toast.makeText(FriendActivity.this, partner + "님을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = requestAlt.create();
        alert.setTitle("만남 요청");
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.show();
    }

    void deleteFriend(final String target){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (target.equals(snapshot.getValue(User.class)._name)) {
                        friend = (User)snapshot.getValue(User.class);
                        friend.friendsMap.remove(user.get_nickname());
                        user.friendsMap.remove(friend.get_nickname());
                        databaseReference.child(user.get_nickname()).child("friendsMap").setValue(user.friendsMap);
                        databaseReference.child(friend.get_nickname()).child("friendsMap").setValue(friend.friendsMap);
                        //Toast.makeText(FriendActivity.this, "친구삭제", Toast.LENGTH_SHORT).show();
                        items.remove(target);
                        adapter.notifyDataSetChanged();
                        break;
                        //Toast.makeText(FriendActivity.this, "찾찾찾", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("NOTICE","Don't search data");
            }
        });
    }
    private void dialogReceive(String partner, String id) { //내가 요청을 받을 때
        AlertDialog.Builder requestAlt = new AlertDialog.Builder(this);
        requestAlt.setMessage(partner + "님이 약속을 신청하였습니다. 수락하시겠습니까?").setCancelable(false).setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click "yes"
                        databaseReference.child(id).child("waitAccept").setValue(true);
                        Intent goMap = new Intent(getApplicationContext(), MapActivity.class);
                        //goMap.putExtra("FriendID", id);
                        user.myFriend = id;
                        Log.d("check", "dialogReceive" + user.myFriend);

                        goMap.putExtra("SentUser", user);
                        startActivity(goMap);
                    }
                }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click "No"
                mRef.child("MeetingInfo").child(user.get_nickname()).removeValue();
                dialog.cancel();
            }
        });

        AlertDialog alert = requestAlt.create();
        alert.setTitle("만남 요청");
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.show();
    }

    private void dialogSetting() {
        AlertDialog.Builder requestAlt = new AlertDialog.Builder(this);
        requestAlt.setMessage("제작 - PathFinder\n팀장 - 버너스리\n서버 - 안산피앙새\n기획 - 이경석주니어\nGPS - 장노이만").setCancelable(false).setPositiveButton("로그아웃",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click "yes"

                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(it);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click "No"
                dialog.cancel();
            }
        });

        AlertDialog alert = requestAlt.create();
        alert.setTitle("설 정");
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.show();
    }

    private void requestPromise(String target) {
        for (String key : user.friendsMap.keySet()) {
            if (user.friendsMap.get(key).equals(target)) {
                friendKey = key;
                mRef.child("/MeetingInfo/" + key).setValue(user.get_nickname());
            }
        }
    }

    private void updatePromise() {
        //나에게 요청이 있는지 확인
        mRef.child("MeetingInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("check","enter updatePromise1 : " );
                String partner = null;
                String partnerID = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(user.get_nickname()))
                        for (String key : user.friendsMap.keySet()) {
                            if(key.equals(snapshot.getValue().toString())) {
                                partner = user.friendsMap.get(key);
                                partnerID = key;
                                dialogReceive(partner, partnerID);
                            }
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //상대가 요청을 받았는지 확인, 내가 요청을 했을 때
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {Log.d("check", "updatePromise not enter " + user.myFriend); }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("check","enter updatePromise2");
                if(dataSnapshot.getValue(User.class).waitAccept){
                    Intent sent = new Intent(getApplicationContext(), MapActivity.class);
                    Log.d("check", "updatePromise not enter " + user.myFriend);
                    if(friendKey != null) {
                        user.myFriend = friendKey;
                        Log.d("check", "updatePromise enter" + user.myFriend);
                        sent.putExtra("SentUser", user);
                        startActivity(sent);
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    /*
    @Override
    public void onBackPressed(){
        //Don't go back
    }*/
}
