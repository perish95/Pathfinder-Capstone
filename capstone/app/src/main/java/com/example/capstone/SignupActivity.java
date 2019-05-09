package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_signup);

        final EditText numberText = (EditText) findViewById(R.id.numberText);
        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText pwText = (EditText) findViewById(R.id.pwText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        Button submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Member temp = new Member(numberText.getText().toString(), idText.getText().toString(), pwText.getText().toString(), nameText.getText().toString());//수정요소가 있음, 임시로 temp로 지정해놓음
                //String name = nameText.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("member", temp);
                Log.v("확인", "값넘기기");
                startActivity(intent);
            }
        });

    }
}
