package com.dolphkon.scanlib.decode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.activity.ScanFragment;
import com.dolphkon.scanlib.camera.CameraManager;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.decode
 * ClassName: DecodeHandler
 * Author: kongdexi
 * Date: 2020/7/1 10:20
 * Description:TODO
 * *****************************************************
 */
public class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final ScanFragment fragment;
    private final MultiFormatReader multiFormatReader;

    DecodeHandler(ScanFragment fragment, Map<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.fragment = fragment;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        } else if (message.what == R.id.quit) {
            Looper.myLooper().quit();
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long start = System.nanoTime();
        Result rawResult = null;

        // 旋转摄像头扫描方向后 处理 可以扫描二维码 也可以扫描条形码
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;
        width = height;
        height = tmp;
        data = rotatedData;

        PlanarYUVLuminanceSource source = CameraManager.getCameraManager().buildLuminanceSource(data, width, height);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        Handler handler = fragment.getHandler();
        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.nanoTime();
            Log.d(TAG, "Found barcode in " + TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
                Bundle bundle = new Bundle();
                bundleThumbnail(source, bundle);
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }
        private static void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
            int[] pixels = source.renderThumbnail();
            int width = source.getThumbnailWidth();
            int height = source.getThumbnailHeight();
            Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
            bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width / source.getWidth());
        }
}
