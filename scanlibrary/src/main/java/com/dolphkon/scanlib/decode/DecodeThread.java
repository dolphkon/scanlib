package com.dolphkon.scanlib.decode;

import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.dolphkon.scanlib.activity.ScanFragment;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.decode
 * ClassName: DecodeThread
 * Author: kongdexi
 * Date: 2020/7/1 10:15
 * Description:TODO
 * *****************************************************
 */
public class DecodeThread extends Thread {

    public static final String BARCODE_BITMAP = "barcode_bitmap";
    public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

    private final ScanFragment fragment;
    private final Map<DecodeHintType,Object> hints;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    DecodeThread(ScanFragment activity,
                 Collection<BarcodeFormat> decodeFormats,
                 Map<DecodeHintType,?> baseHints,
                 String characterSet,
                 ResultPointCallback resultPointCallback) {

        this.fragment = activity;
        handlerInitLatch = new CountDownLatch(1);

        hints = new EnumMap<>(DecodeHintType.class);
        if (baseHints != null) {
            hints.putAll(baseHints);
        }

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        if (characterSet != null) {
            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
        }
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    }

    Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(fragment, hints);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
