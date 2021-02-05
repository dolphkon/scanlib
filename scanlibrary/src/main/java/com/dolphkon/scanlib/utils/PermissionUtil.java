package com.dolphkon.scanlib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.view.CustomDialog;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.webview.utils
 * ClassName: PermissionUtil
 * Author: kongdexi
 * Date: 2020/7/9 19:55
 * Description:TODO
 * *****************************************************
 */
public class PermissionUtil {
    public static void showCameraAndStorageNoti(final Context context){
        CustomDialog dialog = new CustomDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.dialog_tip))
                .setMessage(context.getResources().getString(R.string.you_need_those_permissions_above)+getAppName(context)+context.getResources().getString(R.string.permission_manage))
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        getActivityByContext(context).finish(); //用户取消跳转至系统权限设置页面时finish当前activty(空白的activty)
                    }
                })
                .setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);
                        dialog.cancel();
                    }
                }).create(context, ContextCompat.getColor(context, R.color.common_tv_gold),ContextCompat.getColor(context, R.color.common_tv_gold));
        dialog.show();
    }

    /**
     * 获取应用名称
     */
    public static String getAppName(Context context) {
        String appName;
        PackageManager packManager = context.getPackageManager();
        ApplicationInfo appInfo = context.getApplicationInfo();
        appName = (String) packManager.getApplicationLabel(appInfo);
        return appName;
    }

    /**
     * 通过Context 获得所在Activity
     * */
    public static Activity getActivityByContext(Context context){
        while(context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
