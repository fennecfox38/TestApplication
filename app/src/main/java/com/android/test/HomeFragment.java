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
    View rootView;
    HomeFragment(){ }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_home,container,false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        (rootView.findViewById(R.id.btn_dataactivity)).setOnClickListener(btnOnClickListen);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
    Button.OnClickListener btnOnClickListen=new Button.OnClickListener(){
        @Override public void onClick(View view){
            Class<?> class_Activity;
            switch(view.getId()){
                case R.id.btn_dataactivity: class_Activity= PersonalDataActivity.class; break;
                default: class_Activity=null; break; }
            startActivity(new Intent(getContext(),class_Activity));
        }
    };
}
