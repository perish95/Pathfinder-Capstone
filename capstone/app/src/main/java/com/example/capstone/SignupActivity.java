package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    // 이메일과 비밀번호
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPhonNumber;
    private EditText editTextName;

    private String email = "";
    private String password = "";
    private boolean bool;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.idText);
        editTextPassword = findViewById(R.id.pwText);
        editTextName = findViewById(R.id.nameText);
        editTextPhonNumber= findViewById(R.id.numberText);

        final EditText numberText = (EditText) findViewById(R.id.numberText);
        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText pwText = (EditText) findViewById(R.id.pwText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        Button submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(editTextEmail.getText().toString().trim()) || TextUtils.isEmpty(editTextPassword.getText().toString().trim())
                  || TextUtils.isEmpty(editTextPhonNumber.getText().toString().trim()) || TextUtils.isEmpty(editTextName.getText().toString().trim()))
                    Toast.makeText(SignupActivity.this, "E-mail, PASSWORD , 이름, 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                else {
                    singUp();
                    Member temp = new Member(numberText.getText().toString(), idText.getText().toString(), pwText.getText().toString(), nameText.getText().toString());//수정요소가 있음, 임시로 temp로 지정해놓음
                    if(bool==true) {
                        //databaseReference.child("UserInfo").push().setValue(temp);
                        String id = nameText.getText().toString();
                        databaseReference.child("/UserInfo/" + id).setValue(temp);
                    }
                }


                //String name = nameText.getText().toString();
                //Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                //intent.putExtra("member", temp);
                Log.v("확인", "값넘기기");
               // startActivity(intent);
            }
        });

    }

    public void singUp() {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        bool = false;
        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
            bool = true;
        }
    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 회원가입
    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(SignupActivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            // 회원가입 실패
                            Toast.makeText(SignupActivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
