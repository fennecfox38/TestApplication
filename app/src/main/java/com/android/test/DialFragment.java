package com.android.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DialFragment extends Fragment {
    private String strNum; //string which contains the number user put.
    private TextView txt_number;
    private ImageButton btn_dial,btn_sms, btn_erase;
    private Button[] btn= new Button[12]; //btn[10] for *, [11] for #
    //id array matched with each btn[]
    private int[] id_btn = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_star, R.id.btn_sharp};

    DialFragment(){ }
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_dial,container,false);

        txt_number=rootView.findViewById(R.id.txt_number);
        btn_dial=rootView.findViewById(R.id.btn_dial);
        btn_sms=rootView.findViewById(R.id.btn_sms);
        btn_erase=rootView.findViewById(R.id.btn_erase);


        txt_number.setText(strNum="");
        //EasterEgg TextWatcher
        txt_number.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if((s.toString()).equals("*123456#")){
                    ((LayoutInflater)(getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)))
                            .inflate(R.layout.layout_container, (ViewGroup) rootView.findViewById(R.id.layout_container),true);
                    Toast.makeText(getContext(),getString(R.string.EasterEgg),Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set OnClickListener & OnLongClickListener for btn.
        btn_dial.setOnClickListener(extActBtnListen);
        btn_dial.setOnLongClickListener(extActBtnLongListen);
        btn_sms.setOnClickListener(extActBtnListen);
        btn_sms.setOnLongClickListener(extActBtnLongListen);
        btn_erase.setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View v){
                if(strNum.length()==0) return;
                strNum=strNum.substring(0,strNum.length()-1);
                txt_number.setText(strNum);
            }
        });
        btn_erase.setOnLongClickListener(new Button.OnLongClickListener(){
            @Override public boolean onLongClick(View view){
                txt_number.setText(strNum="");
                return true; }
        });

//Declare Listener that defined as class
        NumBtnClickListener numbtnListener = new NumBtnClickListener();
        for(int i=0;i<12;++i){  //set Listener in loop
            btn[i]=rootView.findViewById(id_btn[i]);
            btn[i].setOnClickListener(numbtnListener);
        }
//set anonymous object Listener (designed for only '0' long pressed)
        btn[0].setOnLongClickListener(new Button.OnLongClickListener(){
            @Override public boolean onLongClick(View view){
                if(strNum.length()!=0) return false;
                txt_number.setText(strNum+="+");
                return true; }
        });

        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    //Anonymous Listener Class for External Action Button
    Button.OnClickListener extActBtnListen= new Button.OnClickListener(){
        @Override public void onClick(View view){
            String toast=null,parse=null,intentConst=null;
            if(view.getId()==R.id.btn_dial){
                toast="Dial to ";
                parse="tel:";
                intentConst= Intent.ACTION_DIAL; }
            else if(view.getId()==R.id.btn_sms){
                toast="SMS to ";
                parse="smsto:";
                intentConst=Intent.ACTION_SENDTO; }
            else return;
            toast+=strNum;
            parse+=strNum.replaceAll("#","%23"); //replace"#" with "%23" (XML Special Character Parsing)
            Toast.makeText(getContext(), toast, Toast.LENGTH_LONG).show();
            startActivity(new Intent(intentConst, Uri.parse(parse)));
        }
    };
    //Anonymous LongClickListener Class for External Action Button
    Button.OnLongClickListener extActBtnLongListen = new Button.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view){
            String toast,parse,intentConst;
            if(view.getId()==R.id.btn_dial){
                toast="Call to ";
                parse="tel:";
                intentConst=Intent.ACTION_CALL;
            }
            else if(view.getId()==R.id.btn_sms){
                toast="SMS to ";
                parse="smsto:";
                intentConst=Intent.ACTION_SENDTO;
            }
            else return false;
            toast+=strNum;
            parse+=strNum.replaceAll("#","%23");
            Toast.makeText(getContext(),toast,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(intentConst, Uri.parse(parse));
            if(intentConst==Intent.ACTION_SENDTO) intent.putExtra("sms_body","Test");
            startActivity(intent);
            return true;
        }
    };

    //explicit Listener Class for Number Button
    class NumBtnClickListener implements Button.OnClickListener{
        @Override
        public void onClick(View view){
            if(strNum.length()>15) return;
            for(int i=0;i<12;++i)
                if(view.getId()==id_btn[i]){
                    if(i<10) strNum+=Integer.toString(i);
                    else if(i==10) strNum+="*";
                    else strNum+="#";
                }
            txt_number.setText(strNum);
        }
    }
}
