package com.android.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.test.PersonalData.PersonalDataActivity;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer_main;
    private NavigationView nav_main;
    private Fragment frag_home, frag_dial, frag_sms, frag_credit, frag_open_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar_main = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main); // Set ToolBar as AppBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        assert actionBar != null; // Show title and 'HomeAsUp' on Action Bar
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);

        frag_home = new HomeFragment();
        frag_dial = new DialFragment();
        frag_sms = new SMSFragment();
        frag_credit = new CreditFragment();
        frag_open_pdf = new OpenPDFFragment();
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.add(R.id.FrameLayout_main, frag_home);
        fragTransaction.commit();

        drawer_main=findViewById(R.id.drawer_main);
        nav_main=findViewById(R.id.nav_main);
        nav_main.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home: switchFragmentTo(frag_home); break;
                    case R.id.menu_dial: switchFragmentTo(frag_dial); break;
                    case R.id.menu_sms: switchFragmentTo(frag_sms); break;
                    case R.id.menu_credit: switchFragmentTo(frag_credit); break;
                    case R.id.menu_pdf: switchFragmentTo(frag_open_pdf); break;
                    case R.id.menu_setting: Toast.makeText(getApplicationContext(),getString(R.string.Setting),Toast.LENGTH_SHORT).show(); break;
                    case R.id.menu_personal_data: startActivity(new Intent(getApplicationContext(), PersonalDataActivity.class)); break;
                }
                drawer_main.closeDrawer(GravityCompat.START);
                return false;
            }
        });

    }

    protected void switchFragmentTo(Fragment frag){
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.FrameLayout_main,frag);
        fragTransaction.commit();
    }

    @Override public void onBackPressed() {
        if(drawer_main.isDrawerOpen(GravityCompat.START)) { drawer_main.closeDrawers(); return; }
        if(getSupportFragmentManager().findFragmentById(R.id.FrameLayout_main)!=frag_home){
            switchFragmentTo(frag_home);
            return;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.Warning));
        dialogBuilder.setMessage(getString(R.string.AskClose));
        dialogBuilder.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                finish(); // finishAndRemoveTask();
                android.os.Process.killProcess(android.os.Process.myPid());
                //MainActivity.super.onBackPressed();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) { }});
        dialogBuilder.show();
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            drawer_main.openDrawer(GravityCompat.START);
        /*switch(item.getItemId()){
            case android.R.id.home: drawer_main.openDrawer(GravityCompat.START); break;
        }*/
        return super.onOptionsItemSelected(item);
    }
}