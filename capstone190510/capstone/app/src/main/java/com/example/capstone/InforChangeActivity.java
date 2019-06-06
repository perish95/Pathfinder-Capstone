package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InforChangeActivity extends AppCompatActivity {
    private User user;
    private User friend;
    private String friendKey;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth auth;
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
    private DatabaseReference mRef = firebaseDatabase.getReference();
    private boolean submit = false;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_inforchange);
        Intent recv = getIntent();

        if ((User) recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            databaseReference.child(user.get_nickname()).child("friendsMap").setValue(user.friendsMap);
            Log.d("CHECK", "[FriendActivity]catch : " + user.get_nickname());
        }

        Button EnterButton = (Button) findViewById(R.id.enterButton);
        Button NicknameChange = (Button) findViewById(R.id.nicknameChangeButton);
        Button SecretNumChange = (Button) findViewById(R.id.ChangeSecretButton);
        Button PhoneNumChange = (Button) findViewById(R.id.ChangePhoneButton);

        EditText searchIdText = (EditText)findViewById(R.id.nicknameChange);
        EditText changeSecretText = (EditText)findViewById(R.id.ChangeSecret);
        EditText changePhoneNumber= (EditText)findViewById(R.id.ChangePhoneNumber);

        EnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InforChangeActivity.this, "재 로그인 하세요.", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(it);
            }
        });

        NicknameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(searchIdText.getText().toString().trim())){
                    Log.e("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",searchIdText.getText().toString().trim());
                    Toast.makeText(InforChangeActivity.this, "입력한 값이 없습니다!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String toSearch = searchIdText.getText().toString();
                    search(toSearch);
                }
            }
        });

        SecretNumChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(changeSecretText.getText().toString().trim())) {
                    Toast.makeText(InforChangeActivity.this, "입력한 값이 없습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    String NewNumber = changeSecretText.getText().toString();
                    databaseReference.child(user.get_nickname()).child("_password").setValue(NewNumber);
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(NewNumber);
                    Toast.makeText(InforChangeActivity.this, "비밀번호가 재설정되었습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        PhoneNumChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(changePhoneNumber.getText().toString().trim())) {
                    Toast.makeText(InforChangeActivity.this, "입력한 값이 없습니다!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String NewNumber = changePhoneNumber.getText().toString();
                    databaseReference.child(user.get_nickname()).child("_number").setValue(Integer.parseInt(NewNumber));
                    Toast.makeText(InforChangeActivity.this, "전화번호가 재설정되었습니다!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    void search(final String target){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(target.equals(user.get_nickname())){
                        Toast.makeText(InforChangeActivity.this, "자신의 아이디는 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (target.equals(snapshot.getValue(User.class).get_nickname())) {
                        friend = (User)snapshot.getValue(User.class);
                        submit = true;
                        if(submit)
                            Toast.makeText(InforChangeActivity.this, "해당 아이디는 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                        Log.d("CHECK","[FriendSearchActivity] catch : submit = " + submit);
                        break;
                    }
                }if(!submit) {
                    Toast.makeText(InforChangeActivity.this, "닉네임 변경 완료!", Toast.LENGTH_SHORT).show();
                    databaseReference.child(target).setValue(user);
                    databaseReference.child(target).child("_nickname").setValue(target);
                    renew(target);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v("NOTICE","Don't search data");
            }
        });
        Log.d("CHECK","[FriendSearchActivity] catch : submit = " + submit);
        submit = false;
    }

    void renew(String target) {
        databaseReference.
                addListenerForSingleValueEvent(new ValueEventListener() { //데이터를 한 번만 읽도록 바꾸어줌
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (target.equals(snapshot.getValue(User.class).get_nickname())) {
                                databaseReference.child(user.get_nickname()).removeValue();
                                user = (User) snapshot.getValue(User.class);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }
}

