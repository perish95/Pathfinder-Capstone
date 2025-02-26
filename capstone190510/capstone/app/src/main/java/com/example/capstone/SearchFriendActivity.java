package com.example.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchFriendActivity extends Activity implements View.OnClickListener {
    private Button addButton, searchButton, cancelButton;
    private EditText searchIdText;
    private boolean submit = false; // 값을 찾았는지 확인해주는 변수
    private User user;
    private User friend; //찾은 친구의 정보를 담는 객체
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_friend);

        Intent recv = getIntent();

        if ((User)recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            Log.d("CHECK","[FriendSearchActivity]catch : " + user);
        }

        setContent();
    }


    private void setContent(){
        addButton = (Button) findViewById(R.id.addButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        cancelButton = (Button) findViewById(R.id.cancelButtton);
        searchIdText = (EditText)findViewById(R.id.searchIdText);

        addButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String toSearch = searchIdText.getText().toString();
        renew();
        switch(v.getId()){
            case R.id.searchButton: //검색 이벤트
                //submit = search(toSearch);
                search(toSearch); // 검색은 닉네임으로 변경
                break;

            case R.id.addButton: //친구추가 버튼 이벤트
                Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                //Intent intent2 = new Intent(getApplicationContext(), RequestFriendActivity.class);

                if(submit){
                    if(friend != null){
                        int go = 0;
                        for(String Key : friend.friendsMap.keySet()){
                            if(Key.equals(user.get_nickname())){
                                go = 1;
                                Toast.makeText(SearchFriendActivity.this, "이미 친구목록에 있습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            go = 0;
                        }
                        if( go == 0 ) {
                            friend.friendRequest.add(user.get_nickname());
                            databaseReference.child(friend.get_nickname()).child("friendRequest").setValue(friend.friendRequest);
                        }
                    }
                    intent.putExtra("SentUser", user);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(SearchFriendActivity.this, "추가할 아이디가 없습니다.", Toast.LENGTH_SHORT).show();
                    intent.putExtra("SentUser", user);
                    startActivity(intent);
                }
                break;

            case R.id.cancelButtton:
                Intent it = new Intent(getApplicationContext(), FriendActivity.class);
                it.putExtra("SentUser", user);
                startActivity(it);
                break;
        }
    }

    //target은 아이디를 나타냄, boolean인 submit 값을 결정
    void search(final String target){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(target.equals(user.get_nickname())){
                        Toast.makeText(SearchFriendActivity.this, "자신의 아이디는 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (target.equals(snapshot.getValue(User.class).get_nickname())) {
                        friend = (User)snapshot.getValue(User.class);
                        submit = true;
                        if(submit)
                            Toast.makeText(SearchFriendActivity.this, "해당 아이디를 찾았습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("CHECK","[FriendSearchActivity] catch : submit = " + submit);
                        break;
                    }
                }if(!submit)
                    Toast.makeText(SearchFriendActivity.this, "해당 아이디를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("NOTICE","Don't search data");
            }
        });
        Log.d("CHECK","[FriendSearchActivity] catch : submit = " + submit);
        submit = false;
    }

    void renew() {
        databaseReference.
        addListenerForSingleValueEvent(new ValueEventListener() { //데이터를 한 번만 읽도록 바꾸어줌
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (user.get_nickname().equals(snapshot.getValue(User.class).get_nickname())) {
                        user = (User) snapshot.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
