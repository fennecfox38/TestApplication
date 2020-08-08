package com.android.test;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

public class DialFragment extends Fragment {
    private DangerousPermission permission_read_state, permission_Call;
    private MainActivity activity;
    private Context context;
    private View rootView;
    private String strNum; //string which contains the number user put.
    private TextView txt_number;
    private Button[] btn= new Button[12]; //btn[10] for *, [11] for #
    //id array matched with each btn[]
    private int[] id_btn = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_star, R.id.btn_sharp};

    DialFragment(){ }
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity)getActivity();
        context = getContext();
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.Dial));
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);

        rootView = inflater.inflate(R.layout.fragment_dial,container,false);

        txt_number=rootView.findViewById(R.id.txt_number);

        txt_number.setText(strNum="");
        //EasterEgg TextWatcher
        txt_number.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if((s.toString()).equals("*123456#")){
                    ((LayoutInflater)(activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
                            .inflate(R.layout.layout_container, (ViewGroup) rootView.findViewById(R.id.layout_container),true);
                    Toast.makeText(context,getString(R.string.EasterEgg),Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set OnClickListener & OnLongClickListener for btn.

        Button btn_dial=rootView.findViewById(R.id.btn_dial);
        Button btn_sms=rootView.findViewById(R.id.btn_sms);
        Button btn_erase=rootView.findViewById(R.id.btn_erase);
        btn_dial.setOnClickListener(funcBtnListen);
        btn_sms.setOnClickListener(funcBtnListen);
        btn_erase.setOnClickListener(funcBtnListen);
        btn_dial.setOnLongClickListener(funcBtnLongListen);
        btn_sms.setOnLongClickListener(funcBtnLongListen);
        btn_erase.setOnLongClickListener(funcBtnLongListen);

//set OnClickListener for Number Button
        for(int i=0;i<12;++i){  //set Listener in loop
            btn[i]=rootView.findViewById(id_btn[i]);
            btn[i].setOnClickListener(numBtnListen);
        }
//set anonymous object Listener (designed for only '0' long pressed)
        btn[0].setOnLongClickListener(new Button.OnLongClickListener(){
            @Override public boolean onLongClick(View view){
                if(strNum.length()!=0) return false;
                txt_number.setText(strNum+="+");
                return true; }
        });

        permission_read_state = new DangerousPermission(this,context);
        permission_read_state.setPermissionList(new String[]{Manifest.permission.READ_PHONE_STATE});
        permission_read_state.setAction(new Runnable() {
            @Override public void run() {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                strNum = telephonyManager.getLine1Number();
                if(strNum.startsWith("+82")) strNum=strNum.replace("+82","0");
            }
        });
        permission_read_state.setDialog(getString(R.string.Warning),getString(R.string.WarnPermissionDenied),
                getString(R.string.WarnPermissionDenied),getString(R.string.GrantPermissionOnSetting));
        permission_read_state.setRequestCode(103);

        permission_Call = new DangerousPermission(this,context);
        permission_Call.setPermissionList(new String[]{Manifest.permission.CALL_PHONE});
        permission_Call.setAction(new Runnable() {
            @Override public void run() {
                String parse="tel:"+getPhoneNumber().replaceAll("#","%23");
                Toast.makeText(context,"Call to "+getPhoneNumber(),Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(parse)));
            }
        });
        permission_Call.setDialog(getString(R.string.Warning),getString(R.string.WarnCallPermissionDenied),
                getString(R.string.WarnCallPermissionDenied),getString(R.string.GrantPermissionOnSetting));
        permission_Call.setRequestCode(104);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Anonymous Listener Class for Function Button
    private Button.OnClickListener funcBtnListen = new Button.OnClickListener(){
        @Override public void onClick(View view){
            switch (view.getId()){
                case R.id.btn_dial: permission_Call.tryAction(); break;//call(); break;
                case R.id.btn_sms: redirectSMS(); break;
                case R.id.btn_erase:
                    if(strNum.length()==0) return;
                    strNum=strNum.substring(0,strNum.length()-1);
                    txt_number.setText(strNum); break;
                default: break; } }
    };
    //Anonymous LongClickListener Class for Function Button
    private Button.OnLongClickListener funcBtnLongListen = new Button.OnLongClickListener(){
        @Override public boolean onLongClick(View view){
            switch (view.getId()){
                case R.id.btn_dial: permission_Call.tryAction(); break;//call(); break;
                case R.id.btn_sms: redirectSMS(); break;
                case R.id.btn_erase: txt_number.setText(strNum=""); break;
                default: return false;
            }return true; }
    };

    private Button.OnClickListener numBtnListen = new Button.OnClickListener(){
        @Override public void onClick(View view){
            if(strNum.length()>15) return;
            for(int i=0;i<12;++i)
                if(view.getId()==id_btn[i]){
                    if(i<10) strNum+=Integer.toString(i);
                    else if(i==10) strNum+="*";
                    else strNum+="#";
                }
            txt_number.setText(strNum);
        }
    };

    private void redirectSMS(){
        Fragment frag_sms = new SMSFragment();
        Bundle bundle = new Bundle();
        bundle.putString("recipient",getPhoneNumber().replaceAll("#","%23"));
        bundle.putString("smsbody",getString(R.string.SentFromTestApplication));
        frag_sms.setArguments(bundle);
        activity.switchFragmentTo(frag_sms);
    }

    private String getPhoneNumber(){
        if(strNum.length()!=0) return strNum;
        permission_read_state.tryAction();
        txt_number.setText(strNum);
        return strNum;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==103) permission_read_state.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==104) permission_Call.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
