package com.android.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.test.PersonalData.PersonalDataActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer_main;
    private NavigationView nav_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null; // Show title and 'HomeAsUp' on Action Bar
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);

        drawer_main=findViewById(R.id.drawer_main);
        nav_main=findViewById(R.id.nav_main);
        nav_main.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_dial: Toast.makeText(getApplicationContext(),"dial",Toast.LENGTH_SHORT).show(); break;
                    case R.id.menu_credit: Toast.makeText(getApplicationContext(),"credit",Toast.LENGTH_SHORT).show(); break;
                    case R.id.menu_setting: Toast.makeText(getApplicationContext(),"setting",Toast.LENGTH_SHORT).show(); break;
                }
                drawer_main.closeDrawer(GravityCompat.START);
                return false;
            }
        });

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
        if(drawer_main.isDrawerOpen(GravityCompat.START)) { drawer_main.closeDrawers(); return; }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.Warning));
        dialogBuilder.setMessage(getString(R.string.AskClose));
        dialogBuilder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) { MainActivity.super.onBackPressed(); } });
        dialogBuilder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) { return; }});
        dialogBuilder.show();
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            drawer_main.openDrawer(GravityCompat.START);
        /*switch(item.getItemId()){
            case android.R.id.home:  break;
        }*/
        return super.onOptionsItemSelected(item);
    }
}