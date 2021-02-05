package com.dolphkon.scanlib.decode;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.decode
 * ClassName: FinishListener
 * Author: kongdexi
 * Date: 2020/7/1 10:18
 * Description:TODO
 * *****************************************************
 */
public class FinishListener implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

    private final Activity activityToFinish;

    public FinishListener(Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        run();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        run();
    }

    @Override
    public void run() {
        activityToFinish.finish();
    }
}
