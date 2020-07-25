package com.android.test;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class DangerousPermission {
    public final static int REQUEST_CODE_PERMISSION=100;
    private AppCompatActivity activity;
    private Fragment fragment;
    private Context context;
    private ArrayList<String> permission_list;
    private Runnable action;
    private String deniedTitle, deniedMessage, gotoSettingTitle, gotoSettingMessage;
    DangerousPermission(AppCompatActivity activity, Context context){
        this.activity=activity;
        fragment=null;
        this.context=context;
    }
    DangerousPermission(Fragment fragment, Context context){
        activity=null;
        this.fragment=fragment;
        this.context=context;
    }
    public void setPermissionList(ArrayList<String> permission_list){ this.permission_list = permission_list; }
    public void setPermissionList(String[] permission_List){
        this.permission_list = new ArrayList<>();
        for(int i=0; i<permission_List.length; ++i)
            this.permission_list.add(permission_List[i]);
    }
    public void setAction(Runnable action){ this.action=action; }
    public void setDialog(String deniedTitle, String deniedMessage, String settingTitle, String settingMessage){
        this.deniedTitle=deniedTitle;
        this.deniedMessage=deniedMessage;
        this.gotoSettingTitle =settingTitle;
        this.gotoSettingMessage =settingMessage;
    }

    public void tryAction(){
        ArrayList<String> denied_list = new ArrayList<>();
        if(Build.VERSION.SDK_INT<23) { action.run(); return; }
        for(String perm : permission_list){
            if(context.checkSelfPermission(perm)== PackageManager.PERMISSION_DENIED)
                denied_list.add(perm);
        }
        if(denied_list.isEmpty()) action.run();
        else requestDangerousPermission(denied_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void requestDangerousPermission(ArrayList<String> requestList){
        boolean isRejected = false;
        if(activity!=null){
            for(String perm : requestList)
                if(!activity.shouldShowRequestPermissionRationale(perm)){ isRejected=true; break; }
            if(!isRejected)
                activity.requestPermissions(requestList.toArray(new String[0]),REQUEST_CODE_PERMISSION);
        }
        else if(fragment!=null){
            for(String perm : requestList)
                if(!fragment.shouldShowRequestPermissionRationale(perm)){ isRejected=true; break; }
            if(!isRejected)
                fragment.requestPermissions(requestList.toArray(new String[0]),REQUEST_CODE_PERMISSION);
        }
        if(isRejected) makeDialogToSetting();
    }

    private void makeDialogToSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(gotoSettingTitle);
        builder.setMessage(gotoSettingMessage);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener(){
            @Override public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + context.getPackageName())));
            }
        });
        builder.setNegativeButton(R.string.Cancel,new DialogInterface.OnClickListener(){
            @Override public void onClick(DialogInterface dialog, int which) { }
        });
        builder.show();
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode!= REQUEST_CODE_PERMISSION) return;
        boolean isAllGranted= true;
        final ArrayList<String> denied_list = new ArrayList<>();
        for(int i=0; i<permissions.length; ++i)
            if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                isAllGranted=false;
                denied_list.add(permissions[i]);
            }
        if(isAllGranted) action.run();
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(deniedTitle);
            builder.setMessage(deniedMessage);
            builder.setPositiveButton(R.string.Request, new DialogInterface.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override public void onClick(DialogInterface dialog, int which) { requestDangerousPermission(denied_list); }
            });
            builder.setNegativeButton(R.string.Cancel,new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface dialog, int which) { }
            });
            builder.show();
        }
    }
}
