package com.example.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class reload2 extends AppCompatActivity {
    private String receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload2);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            receiver = bundle.getString("receiver");
        }
        goback();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goback();
    }

    public void goback() {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra("tag",receiver);
        startActivity(intent);
        finish();
    }
}