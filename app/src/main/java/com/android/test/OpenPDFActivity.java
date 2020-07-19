package com.android.test;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class OpenPDFActivity extends AppCompatActivity {
    private String filepath;
    private TextView txt_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdf);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build()); // Unlimit restriction of access through file path directly.

        filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        txt_path=findViewById(R.id.txt_path);
        txt_path.setText(filepath);
        findViewById(R.id.btn_open).setOnClickListener(new Button.OnClickListener(){
            @Override public void onClick(View v) {
                String filename =
                ((EditText)findViewById(R.id.edittxt_PDFname)).getText().toString();
                if(filename.length()>0)
                    openPDF(filename.trim());
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.PDFHint), Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(getApplicationContext(),getString(R.string.NoPDFViewer),Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getApplicationContext(),getString(R.string.FileNotExist),Toast.LENGTH_SHORT).show();
        }
    }
}