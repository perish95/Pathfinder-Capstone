package com.example.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    private User user;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("UserInfo");
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);
        Intent recv = getIntent();

        Button LogOutButton = (Button) findViewById(R.id.Logoutbutton);
        Button InforChange = (Button) findViewById(R.id.InforChangeButton);
        Button deleteInfor = (Button) findViewById(R.id.InforDeleteButton);

        if ((User) recv.getSerializableExtra("SentUser") != null) {
            user = (User) recv.getSerializableExtra("SentUser");
            databaseReference.child(user.get_nickname()).child("friendsMap").setValue(user.friendsMap);
            Log.d("CHECK", "[FriendActivity]catch : " + user.get_nickname());
        }

        LogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(it);
            }
        });

        InforChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InforChangeActivity.class);
                //Intent intent = new Intent(getApplicationContext() , FriendActivity.class);
                intent.putExtra("SentUser", user); //FriendActivity에 user값 전달
                startActivity(intent);
            }
        });

        deleteInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetting();
            }

        });
    }
    private void dialogSetting() {
        AlertDialog.Builder requestAlt = new AlertDialog.Builder(this);
        requestAlt.setMessage("정말로 회원탈퇴를 하시겠습니까???").setCancelable(false).setPositiveButton("회원탈퇴",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click "yes"
                        Toast.makeText(SettingActivity.this, "회원탈퇴 완료", Toast.LENGTH_SHORT).show();
                        databaseReference.child(user.get_nickname()).removeValue();
                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        FirebaseAuth.getInstance().signOut();


                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(it);
                        /*
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
                                        Intent it = new Intent(getApplicationContext(), MainActivity.class);
                                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(it);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.v("NOTICE","Don't search data");
                            }
                        });
                        */
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Click "No"
                dialog.cancel();
            }
        });

        AlertDialog alert = requestAlt.create();
        alert.setTitle("경 고");
        alert.setIcon(R.drawable.ic_launcher_background);
        alert.show();
    }
}
