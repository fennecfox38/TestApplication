package com.android.test;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.File;

public class OpenPDFFragment extends Fragment {
    private DangerousPermission permission_Storage;
    private AppCompatActivity activity;
    private Context context;
    private View rootView;
    private String filepath;
    private TextView txt_path;
    OpenPDFFragment(){ }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) requireActivity();
        context = getContext();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(getString(R.string.openPDF));
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);

        rootView = inflater.inflate(R.layout.fragment_open_pdf,container,false);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build()); // Unlimit restriction of access through file path directly.

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        txt_path=rootView.findViewById(R.id.txt_path);
        txt_path.setText(filepath);
        rootView.findViewById(R.id.btn_open).setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View v) {
                permission_Storage.tryAction();
            }
        });

        permission_Storage = new DangerousPermission(this,context);
        permission_Storage.setPermissionList(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE});
        permission_Storage.setAction(new Runnable() {
            @Override public void run() {
                String filename = ((EditText)rootView.findViewById(R.id.edittxt_PDFname)).getText().toString();
                if(filename.length()>0) openPDF(filename.trim());
                else Toast.makeText(getContext(), getString(R.string.PDFHint), Toast.LENGTH_SHORT).show();
            }
        });
        permission_Storage.setDialog(getString(R.string.Warning),getString(R.string.WarnPermissionDenied),
                getString(R.string.WarnPermissionDenied),getString(R.string.GrantPermissionOnSetting));

        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void openPDF(String filename){
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filepath += File.separator + filename;
        txt_path.setText(filepath);

        File file = new File(filepath);
        if(file.exists()){
            Uri uri = Uri.fromFile(file);//FileProvider.getUriForFile(getApplicationContext(),BuildConfig.APPLICATION_ID+".fileProvider",file);
            Intent intentPDF = new Intent(Intent.ACTION_VIEW);
            intentPDF.setDataAndType(uri,"application/pdf");
            try{
                startActivity(intentPDF);
            } catch (ActivityNotFoundException ex){
                Toast.makeText(getContext(),getString(R.string.NoPDFViewer),Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getContext(),getString(R.string.FileNotExist),Toast.LENGTH_SHORT).show();
        }
    }
}
