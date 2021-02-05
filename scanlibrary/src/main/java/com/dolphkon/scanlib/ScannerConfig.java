package com.dolphkon.scanlib;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import com.dolphkon.scanlib.utils.DisplayUtil;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib
 * ClassName: ScannerApplication
 * Author: kongdexi
 * Date: 2020/7/1 10:36
 * Description:TODO
 * *****************************************************
 */
public class ScannerConfig {
    private static Context mContext;
    private static final String TAG = "ScannerConfig";
    public static void init(Context context) {
        mContext = context.getApplicationContext();
        /**
         * 初始化尺寸工具类
         */
        initDisplayOpinion();
    }

    private static void initDisplayOpinion() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        DisplayUtil.density = dm.density;
        DisplayUtil.densityDPI = dm.densityDpi;
        DisplayUtil.screenWidthPx = dm.widthPixels;
        DisplayUtil.screenhightPx = dm.heightPixels;
        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(mContext.getApplicationContext(), dm.widthPixels);
        DisplayUtil.screenHightDip = DisplayUtil.px2dip(mContext.getApplicationContext(), dm.heightPixels);
    }
    public static Context getContext() {
        if (mContext==null){
            Log.e(TAG, "项目未初始化");
        }
        return mContext;
    }
}
