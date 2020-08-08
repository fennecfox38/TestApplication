package com.android.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class SMSDisplayActivity extends AppCompatActivity {
    TextView txt_sms_sender,txt_sms_body,txt_sms_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sms_display);
        setTitle(R.string.SMSReceived);

        txt_sms_sender = findViewById(R.id.txt_sms_sender);
        txt_sms_body = findViewById(R.id.txt_sms_body);
        txt_sms_date = findViewById(R.id.txt_sms_date);
        findViewById(R.id.btn_sms_ok).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }});

        Intent passedIntent = getIntent();
        processIntent(passedIntent);
    }
    @Override protected void onNewIntent(Intent intent){
        processIntent(intent);
        super.onNewIntent(intent);
    }
    private void processIntent(Intent intent){
        if(intent==null) return;
        txt_sms_sender.setText(intent.getStringExtra("sender"));
        txt_sms_body.setText(intent.getStringExtra("body"));
        txt_sms_date.setText(intent.getStringExtra("date"));
    }
}