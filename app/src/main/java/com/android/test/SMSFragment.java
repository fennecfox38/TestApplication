package com.android.test;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SMSFragment extends Fragment {
    private final static int REQUEST_CODE_PERMISSION_SMS = 104;
    private DangerousPermission permission_SMS;
    private AppCompatActivity activity;
    private Context context;
    private View rootView;
    private TextView txt_number_of_letter;
    private EditText edittxt_recipient, edittext_smsbody;
    private SmsManager smsManager;

    public SMSFragment() { }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) requireActivity();
        context = getContext();
        ActionBar actionBar = activity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(getString(R.string.SMS));
        Bundle bundle = getArguments();
        rootView=inflater.inflate(R.layout.fragment_sms, container, false);

        txt_number_of_letter = rootView.findViewById(R.id.txt_number_of_letter);
        edittxt_recipient=rootView.findViewById(R.id.edittxt_recipient);
        edittext_smsbody=rootView.findViewById(R.id.edittxt_smsbody);
        edittext_smsbody.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                txt_number_of_letter.setText(s.toString().length()+" / 70");
            }
        });
        rootView.findViewById(R.id.btn_send).setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View v) { permission_SMS.tryAction(); }
        });
        smsManager=SmsManager.getDefault();
        if(bundle!=null){
            edittxt_recipient.setText(bundle.getString("recipient"));
            edittext_smsbody.setText(bundle.getString("smsbody"));
        }

        permission_SMS = new DangerousPermission(this,context);
        permission_SMS.setPermissionList(new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS});
        permission_SMS.setAction(new Runnable() {
            @Override public void run() {
                String recipient=edittxt_recipient.getText().toString();
                String body=edittext_smsbody.getText().toString();
                edittext_smsbody.setText("");
                if(recipient.length()==0){
                    Toast.makeText(context, getString(R.string.WarnRecipientEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }else if(body.length()==0){
                    Toast.makeText(context, getString(R.string.WarnSMSBodyEmpty), Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<String> bodyList = smsManager.divideMessage(body);

                PendingIntent sentPI = PendingIntent.getBroadcast(context,0,new Intent("SMS_SENT"),0);
                PendingIntent recvPI = PendingIntent.getBroadcast(context,0,new Intent("SMS_SENT"),0);

                activity.registerReceiver(sentReceiver, new IntentFilter("SMS_SENT"));
                activity.registerReceiver(recvReceiver, new IntentFilter("SMS_DELIVERED"));

                if(bodyList.size() > 1){ //여러개의 SMS 메시지를 전송
                    ArrayList<PendingIntent> sentPIList = new ArrayList<>();
                    ArrayList<PendingIntent> recvPIList = new ArrayList<>();
                    for(int i=bodyList.size(); i>0; --i){
                        sentPIList.add(sentPI);
                        recvPIList.add(recvPI);
                    }
                    smsManager.sendMultipartTextMessage(recipient, null, bodyList, sentPIList, recvPIList);
                } //1개의 SMS 메시지를 전송
                else smsManager.sendTextMessage(recipient, null, body, sentPI, recvPI);
            }
        });
        permission_SMS.setDialog(getString(R.string.Warning),getString(R.string.WarnSMSPermissionDenied),
                getString(R.string.WarnSMSPermissionDenied),getString(R.string.GrantPermissionOnSetting));

        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission_SMS.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String txt="";
            switch (getResultCode()){
                case RESULT_OK: txt="SMS Send"; break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: txt="ERROR_GENERIC_FAILURE"; break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: txt="ERROR_NO_SERVICE"; break;
                case SmsManager.RESULT_ERROR_NULL_PDU: txt="ERROR_NULL_PDU"; break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: txt="ERROR_RADIO_OFF"; break;
            }Toast.makeText(context, txt, Toast.LENGTH_SHORT).show(); }
    };
    BroadcastReceiver recvReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String txt="";
            switch (getResultCode()){
                case RESULT_OK: txt="SMS Delivered"; break;
                case RESULT_CANCELED: txt="SMS Delivered Fail"; break;
            }Toast.makeText(context, txt, Toast.LENGTH_SHORT).show(); }
    };
}