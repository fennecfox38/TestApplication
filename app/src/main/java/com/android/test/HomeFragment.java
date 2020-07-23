package com.android.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.test.PersonalData.PersonalDataActivity;

public class HomeFragment extends Fragment {
    HomeFragment(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        (rootView.findViewById(R.id.btn_dataactivity)).setOnClickListener(btnOnClickListen);
        (rootView.findViewById(R.id.btn_openPDFactivity)).setOnClickListener(btnOnClickListen);
        (rootView.findViewById(R.id.btn_creditactivity)).setOnClickListener(btnOnClickListen);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
    Button.OnClickListener btnOnClickListen=new Button.OnClickListener(){
        @Override public void onClick(View view){
            Class<?> class_Activity;
            switch(view.getId()){
                case R.id.btn_dataactivity: class_Activity= PersonalDataActivity.class; break;
                case R.id.btn_openPDFactivity: class_Activity= OpenPDFActivity.class; break;
                case R.id.btn_creditactivity: class_Activity= CreditActivity.class; break;
                default: class_Activity=null; break; }
            startActivity(new Intent(getContext(),class_Activity));
        }
    };
}
