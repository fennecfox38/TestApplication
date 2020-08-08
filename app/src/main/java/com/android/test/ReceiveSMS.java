package com.android.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveSMS extends BroadcastReceiver {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msg;
        String receive = "";

        if(bundle!=null){
            Object[] pdus = (Object[])bundle.get("pdus"); //get PDU(Protocol Data Unit)
            msg = new SmsMessage[pdus.length];
            for(int i=0; i<msg.length; i++){
                // Convert PDU to SmsMessage
                if(Build.VERSION.SDK_INT>=23) msg[i] = SmsMessage.createFromPdu((byte[])pdus[i], "3gpp");
                else msg[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                //PDU로부터 전화번호 , 메시지를 반환
                //receive += msg[i].getOriginatingAddress()+" : "+msg[i].getMessageBody();
                Intent intentDisplay = new Intent(context,SMSDisplayActivity.class);
                intentDisplay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentDisplay.putExtra("sender",msg[i].getOriginatingAddress());
                intentDisplay.putExtra("body",msg[i].getMessageBody());
                Date received = new Date(msg[i].getTimestampMillis());
                intentDisplay.putExtra("date",dateFormat.format(received));
                context.startActivity(intentDisplay);
                //PendingIntent pi = PendingIntent.getActivity(context,0,intentDisplay,PendingIntent.FLAG_ONE_SHOT);
                //try{ pi.send(); }
                //catch(PendingIntent.CanceledException e){ e.printStackTrace(); }
            }
            //Toast.makeText(context, receive, Toast.LENGTH_SHORT).show();
        }
    }
}
