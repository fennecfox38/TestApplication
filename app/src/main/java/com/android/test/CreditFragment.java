package com.android.test;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreditFragment extends Fragment {
    private View rootView;
    private ImageView imgView_credit1, imgView_credit2;
    private TextView txt_imageTitle;
    CreditFragment(){ }
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_credit,container,false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.Credit));

        txt_imageTitle=rootView.findViewById(R.id.txt_imageTitle);
        BitmapDrawable bitmap = (BitmapDrawable) (getResources().getDrawable(R.drawable.image_credit1));
        imgView_credit1=rootView.findViewById(R.id.imgView_credit1);
        imgView_credit1.setImageDrawable(bitmap);
        imgView_credit1.getLayoutParams().width=bitmap.getIntrinsicWidth();
        imgView_credit1.getLayoutParams().height=bitmap.getIntrinsicHeight();
        imgView_credit2=rootView.findViewById(R.id.imgView_credit2);
        imgView_credit2.setImageResource(R.drawable.image_credit2);

        Switch switch_image=rootView.findViewById(R.id.switch_image);
        switch_image.setChecked(false);
        switch_image.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                txt_imageTitle.setText((isChecked? R.string.credit1 : R.string.credit2));
                rootView.findViewById(R.id.ScrollView_Horizon).setVisibility((isChecked? View.VISIBLE : View.INVISIBLE));
                imgView_credit2.setVisibility((isChecked? View.INVISIBLE : View.VISIBLE));
            }
        });

        Button.OnClickListener onBtnClick = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt=null; Intent mIntent=null;
                switch(v.getId()){
                    case R.id.btn_dial: txt="Dial to 010-1234-5678"; mIntent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-1234-5678")); break;
                    case R.id.btn_webpage: txt="Direct to www.google.com"; mIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com")); break;
                    case R.id.btn_email: txt="Send Email to fennecfox38@gmail.com"; mIntent=new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:fennecfox38@gmail.com")); break;
                    case R.id.btn_locate: txt="Direct to geo:37.9239,127.0709"; mIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("geo:37.9239,127.0709")); break;
                }
                Toast.makeText(getContext(),txt,Toast.LENGTH_LONG).show();
                startActivity(mIntent);
            }
        };
        rootView.findViewById(R.id.btn_dial).setOnClickListener(onBtnClick);
        rootView.findViewById(R.id.btn_webpage).setOnClickListener(onBtnClick);
        rootView.findViewById(R.id.btn_email).setOnClickListener(onBtnClick);
        rootView.findViewById(R.id.btn_locate).setOnClickListener(onBtnClick);

        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
