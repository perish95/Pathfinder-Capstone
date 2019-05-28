package com.example.capstone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mref = firebaseDatabase.getReference("UserInfo");
    private FirebaseAuth firebaseAuth;

    private EditText email_login;
    private EditText pwd_login;
    private CheckBox autoCheck;
    private boolean saveLoginData;
    private SharedPreferences auto;
    private String id;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = firebaseAuth.getInstance();

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        final Button signupButton = (Button) findViewById(R.id.signupButton);
        final Button mapButton = (Button) findViewById(R.id.mapButton);
        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        load();
        email_login = (EditText) findViewById(R.id.idText);
        pwd_login = (EditText) findViewById(R.id.pwText);
        autoCheck = (CheckBox) findViewById(R.id.autoLogin);

        if(saveLoginData){
            email_login.setText(id);
            pwd_login.setText(pwd);
            autoCheck.setChecked(saveLoginData);
            signIn();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_login = (EditText) findViewById(R.id.idText);
                pwd_login = (EditText) findViewById(R.id.pwText);
                if (TextUtils.isEmpty(email_login.getText().toString().trim()) || TextUtils.isEmpty(pwd_login.getText().toString().trim()))
                    Toast.makeText(MainActivity.this, "ID, PASSWORD를 입력해주세요.", Toast.LENGTH_SHORT).show();
                else {
                    save();
                    signIn();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

    }

    // 설정값을 저장하는 함수
    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = auto.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", autoCheck.isChecked());
        editor.putString("ID", email_login.getText().toString().trim());
        editor.putString("PWD", pwd_login.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = auto.getBoolean("SAVE_LOGIN_DATA", false);
        id = auto.getString("ID", "");
        pwd = auto.getString("PWD", "");
    }

    public void signIn() {
        String email = email_login.getText().toString().trim();
        String pwd = pwd_login.getText().toString().trim();
        loginUser(email, pwd);
    }

    // 로그인
    private void loginUser(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            mref.addListenerForSingleValueEvent(new ValueEventListener() { //데이터를 한 번만 읽도록 바꾸어줌
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (email.equals(snapshot.getValue(User.class).get_id())) {
                                            User user = snapshot.getValue(User.class);
                                            Toast.makeText(MainActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                                            intent.putExtra("SentUser", user); //FriendActivity에 user값 전달
                                            startActivity(intent);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    //Overide
                                }
                            });


                        } else {
                            // 로그인 실패
                            Toast.makeText(MainActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
