package com.example.capstone;

import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//친구요청목록 보여주는 액티비티, ListViewAdapter에서 한줄 한줄 레이아웃 관리
public class RequestFriendActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView requestList = null;
    private ListViewAdapter adapter;
    private User user;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
    private User friend;
    private DatabaseReference mref = firebaseDatabase.getReference("UserInfo");
    private ArrayList<ItemData> temp = new ArrayList<ItemData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friend);
        Intent recv = getIntent();
        if ((User)recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            Log.d("CHECK","[RequestFriendActivity]catch : " + user);
        }

        renew();
        /*
        for(int i = 1; i< user.friendRequest.size() ; i++){
            //temp.add(user.friendRequest.get(i));
            ItemData oItem = new ItemData();
            oItem.strTitle = user.friendRequest.get(i);
            oItem.onClickListener = this;
            temp.add(oItem);
        }
        */
    }

    public void CheckFriends(String Friendsname, int tagCount ,String position){
        mref.addListenerForSingleValueEvent(new ValueEventListener() { //데이터를 한 번만 읽도록 바꾸어줌
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Friendsname.equals(snapshot.getValue(User.class).get_nickname())) {
                        friend = (User)snapshot.getValue(User.class);
                        if(tagCount == 1) {
                            AddFriends();
                            deleteFriends(Friendsname,position);
                        }

                        else if (tagCount == 2){
                            deleteFriends(Friendsname,position);
                        }
                        Log.d("찾차자","찾음______________________");
                       // user = snapshot.getValue(User.class);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Overide
            }
        });
    }

    @Override
    public void onClick(View v){
        int nViewTag = Integer.parseInt((String)v.getTag());
        String strViewName = "";
        View oParentView = (View)v.getParent();
        TextView oTextTitle = (TextView) oParentView.findViewById(R.id.FriendNameText);
        String position = (String) oParentView.getTag();
        String FN = oTextTitle.getText().toString().trim();

        switch (nViewTag)
        {
            case 1: // 수락버튼
                CheckFriends(FN,nViewTag,position);
                //AddFriends();
                //deleteFriends(FN);
                Toast.makeText(RequestFriendActivity.this, "OOOOOO "+FN , Toast.LENGTH_SHORT).show();
                break;
            case 2: // 거절버튼
                CheckFriends(FN,nViewTag,position);
                Toast.makeText(RequestFriendActivity.this, "XXXXXX "+FN, Toast.LENGTH_SHORT).show();
                break;
        }
        // 부모의 View를 가져온다. 즉, 아이템 View임.

    }
    public void renew(){
        mref.addListenerForSingleValueEvent(new ValueEventListener() { //데이터를 한 번만 읽도록 바꾸어줌
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (user.get_nickname().equals(snapshot.getValue(User.class).get_nickname())) {
                        user = (User)snapshot.getValue(User.class);
                        setList();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Overide
            }
        });
    }

    public void setList(){
        for(int i = 1; i< user.friendRequest.size() ; i++){
            //temp.add(user.friendRequest.get(i));
            ItemData oItem = new ItemData();
            oItem.strTitle = user.friendRequest.get(i);
            oItem.onClickListener = this;
            temp.add(oItem);
            requestList = (ListView) findViewById(R.id.RequestList);
            adapter = new ListViewAdapter(temp);
            requestList.setAdapter(adapter);
        }
    }
    public void AddFriends(){
        friend.friendsMap.put(user.get_nickname(), user._name);
        user.friendsMap.put(friend.get_nickname(),friend._name);
        databaseReference.child(friend.get_nickname()).child("friendsMap").setValue(friend.friendsMap);
        databaseReference.child(user.get_nickname()).child("friendsMap").setValue(user.friendsMap);
    }

    public void deleteFriends(String FN,String Position){
        user.friendRequest.remove(FN);
        int index = Integer.parseInt(Position);
        temp.remove(index);

        Log.d("Ddd","ddd ===" + index);
        databaseReference.child(user.get_nickname()).child("friendRequest").setValue(user.friendRequest);
        adapter.notifyDataSetChanged();
        requestList.setAdapter(adapter);
        //renew();
    }
}
