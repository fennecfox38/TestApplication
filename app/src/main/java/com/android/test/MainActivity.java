package com.android.test;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.test.PersonalData.PersonalDataActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.ic_launcher_round);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_SHOW_TITLE);

        findViewById(R.id.btn_dataactivity).setOnClickListener(btnOnClickListen);
        findViewById(R.id.btn_dialactivity).setOnClickListener(btnOnClickListen);
        findViewById(R.id.btn_openPDFactivity).setOnClickListener(btnOnClickListen);
        findViewById(R.id.btn_creditactivity).setOnClickListener(btnOnClickListen);
    }

    Button.OnClickListener btnOnClickListen=new Button.OnClickListener(){
        @Override public void onClick(View view){
            Class<?> class_Activity;
            switch(view.getId()){
                case R.id.btn_dataactivity: class_Activity=PersonalDataActivity.class; break;
                case R.id.btn_dialactivity: class_Activity=DialActivity.class; break;
                case R.id.btn_openPDFactivity: class_Activity=OpenPDFActivity.class; break;
                case R.id.btn_creditactivity: class_Activity=CreditActivity.class; break;
                default: class_Activity=null; break; }
            startActivity(new Intent(getApplicationContext(),class_Activity));
        }
    };

    @Override public void onBackPressed() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.Warning));
        dialogBuilder.setMessage(getString(R.string.AskClose));
        dialogBuilder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) { finish(); } });
        dialogBuilder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) { return; }});
        dialogBuilder.show();
        //super.onBackPressed();
    }
}