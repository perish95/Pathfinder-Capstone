package com.example.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class SearchFriendActivity extends Activity implements View.OnClickListener {
    private Button addButton, searchButton;
    private EditText searchIdText;
    private boolean submit = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_friend);

        setContent();
    }


    private void setContent(){
        addButton = (Button) findViewById(R.id.addButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        searchIdText = (EditText)findViewById(R.id.searchIdText);


        addButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String toSearch = searchIdText.getText().toString();
        switch(v.getId()){
            case R.id.searchButton:
                submit = search(toSearch);
                //클릭시 DB에서 맞는 아이디를 찾아서 boolean(?)으로 리턴
                break;

            case R.id.addButton:
                if(submit){
                    Intent intent = new Intent(getApplicationContext(), FriendActivity.class);
                    Log.d("확인", "문자열확인 " + toSearch);
                    intent.putExtra("deli", toSearch);
                    startActivity(intent);
                }
                else{
                    Log.v("확인", "submit == False");
                }
                break;
        }
    }

    boolean search(String target){ //DB에서 찾는 메소드
        //implements
        return true;
    }
}
