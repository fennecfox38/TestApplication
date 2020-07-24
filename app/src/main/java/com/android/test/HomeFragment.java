package com.android.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.android.test.PersonalData.PersonalDataActivity;

public class HomeFragment extends Fragment {
    View rootView;
    HomeFragment(){ }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
        rootView = inflater.inflate(R.layout.fragment_home,container,false);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
