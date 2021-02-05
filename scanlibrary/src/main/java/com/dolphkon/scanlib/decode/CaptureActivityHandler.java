package com.dolphkon.scanlib.decode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.activity.ScanFragment;
import com.dolphkon.scanlib.camera.CameraManager;
import com.dolphkon.scanlib.view.ViewfinderResultPointCallback;

import java.util.Collection;
import java.util.Map;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.decode
 * ClassName: CaptureActivityHandler
 * Author: kongdexi
 * Date: 2020/7/1 10:14
 * Description:TODO
 * *****************************************************
 */
public class CaptureActivityHandler extends Handler {


    private static final String TAG = CaptureActivityHandler.class.getSimpleName();

    private final ScanFragment fragment;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }


    public CaptureActivityHandler(ScanFragment fragment,
                                  Collection<BarcodeFormat> decodeFormats,
                                  Map<DecodeHintType, ?> baseHints,
                                  String characterSet,
                                  CameraManager cameraManager) {
        this.fragment = fragment;
        decodeThread = new DecodeThread(fragment, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(fragment.getViewfinderView()));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what==R.id.restart_preview){
            restartPreviewAndDecode();
        }else if (message.what==R.id.decode_succeeded){
            Log.d(TAG, "Got decode succeeded message");
            state = State.SUCCESS;
            Bundle bundle = message.getData();

            /***********************************************************************/
            Bitmap barcode = bundle == null ? null :
                    (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);

            fragment.handleDecode((Result) message.obj, barcode);
        }else if (message.what==R.id.decode_failed){
            // We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
        }else if (message.what==R.id.return_scan_result){
            Log.d(TAG, "Got return scanlib result message");
            fragment.getActivity().setResult(Activity.RESULT_OK, (Intent) message.obj);
            fragment.getActivity().finish();
        } else if (message.what == R.id.launch_product_query) {
            Log.d(TAG, "Got product query message");
            String url = (String) message.obj;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            fragment.getActivity().startActivity(intent);
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            fragment.drawViewfinder();
        }
    }

}
