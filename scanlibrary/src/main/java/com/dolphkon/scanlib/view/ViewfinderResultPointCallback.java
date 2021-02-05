package com.dolphkon.scanlib.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.view
 * ClassName: ViewfinderResultPointCallback
 * Author: kongdexi
 * Date: 2020/7/1 10:24
 * Description:TODO
 * *****************************************************
 */
public class ViewfinderResultPointCallback  implements ResultPointCallback {

    private final ScannerView scannerView;

    public ViewfinderResultPointCallback(ScannerView scannerView) {
        this.scannerView = scannerView;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        scannerView.addPossibleResultPoint(point);
    }
}
