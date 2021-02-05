package com.dolphkon.scanlib.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;
import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.ScannerConfig;
import com.dolphkon.scanlib.camera.CameraManager;
import com.dolphkon.scanlib.utils.DisplayUtil;
import com.dolphkon.scanlib.utils.ScanParamsBean;

import java.util.Collection;
import java.util.HashSet;

import static android.content.ContentValues.TAG;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib
 * ClassName: ScannerView
 * Author: kongdexi
 * Date: 2020/7/1 10:00
 * Description:自定义扫描的view
 * *****************************************************
 */
public class ScannerView  extends View {
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 100L;
    private static final int OPAQUE = 0xFF;
    private int scannerAlpha;
    private Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private Paint mTipPaint;
    private final int resultColor;
    private final int resultPointColor;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private int mTipPaddingTop;
    //提示文字的颜色
    private int scannerTipColor;
    //提示文字的大小
    private int scannerTipSize;
    //提示文字的内容
    private String scannerTipString;

    // 扫描框边角颜色
    private int innerCornerColor;
    // 扫描框边角长度
    private int innerCornerLength;
    // 扫描框边角宽度
    private int innercornerWidth;

    //激光线
    private Paint mLaserPaint;


    // 扫描线移动的y
    private int scanLineTop;
    // 扫描线
    private int scanColor;
    // 是否展示小圆点
    private boolean isCircle;
    private ScanParamsBean mScanParamsBean;
    private int corLength;
    private int corWidth;

    //扫码框距离顶部的距离
    private float frameMarginTop;
    //扫码框的高度
    private float frameHeight;
    //扫码框的宽度
    private float frameWidth;


    public void setmScanParamsBean(ScanParamsBean mScanParamsBean) {
        Log.d(TAG, "setmScanParamsBean: " + mScanParamsBean.toString());
        this.mScanParamsBean = mScanParamsBean;
        if (mScanParamsBean.getFrameMarginTop() != 0) {
            this.frameMarginTop = mScanParamsBean.getFrameMarginTop();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getFrameHeight()))) {
            this.frameHeight = mScanParamsBean.getFrameHeight();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getFrameWidth()))) {
            this.frameWidth = mScanParamsBean.getFrameWidth();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getInnerCornerLength()))) {
            this.innerCornerLength = mScanParamsBean.getInnerCornerLength();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getInnerCornerWidth()))) {
            this.innercornerWidth = mScanParamsBean.getInnerCornerWidth();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getInnerCornerColor()))) {
            this.innerCornerColor = mScanParamsBean.getInnerCornerColor();
        }
        this.isCircle = mScanParamsBean.isCircle();



        if (!TextUtils.isEmpty(mScanParamsBean.getScannerTipString())) {
            this.scannerTipString = mScanParamsBean.getScannerTipString();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getScannerTipColor()))) {
            this.scannerTipColor = mScanParamsBean.getScannerTipColor();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getScannerTipSize()))) {
            this.scannerTipSize = mScanParamsBean.getScannerTipSize();
        }

        if (!TextUtils.isEmpty(String.valueOf(mScanParamsBean.getScanColor()))) {
            this.scanColor=mScanParamsBean.getScanColor();
        }
        postInvalidate();
    }

    public ScannerView(Context context) {
        this(context, null);
    }

    public ScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }
    public ScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maskColor = ContextCompat.getColor(ScannerConfig.getContext(),R.color.scannerview_mask);
        resultColor = ContextCompat.getColor(ScannerConfig.getContext(),R.color.result_view);
        resultPointColor = ContextCompat.getColor(ScannerConfig.getContext(),R.color.possible_result_points);
        possibleResultPoints = new HashSet<>(5);
        initInnerRect();
    }

    /**
     * 初始化内部框的大小
     */
    @SuppressLint("ResourceType")
    private void initInnerRect() {
        paint = new Paint();
        mTipPaint = new Paint();
        mLaserPaint = new Paint();
//        扫描文字提示
        mTipPaint.setAntiAlias(true);
        // 扫描线
        mLaserPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //扫描框距离顶部的距离
        if (mScanParamsBean.getFrameMarginTop()==0){
            CameraManager.FRAME_MARGINTOP = (int) (DisplayUtil.screenhightPx / (int) 4);
        }else {
            CameraManager.FRAME_MARGINTOP = (int) (DisplayUtil.screenhightPx / (int) mScanParamsBean.getFrameMarginTop());
        }
        // 扫描框的宽度
        if (mScanParamsBean.getFrameWidth()==0){
            CameraManager.FRAME_WIDTH = (int) (DisplayUtil.screenWidthPx / (1.5f));
        }else {
            CameraManager.FRAME_WIDTH = (int) (DisplayUtil.screenWidthPx / frameWidth);
        }
        // 扫描框的高度
        if (mScanParamsBean.getFrameHeight()==0){
            CameraManager.FRAME_HEIGHT = (int) (DisplayUtil.screenWidthPx / (1.5f));
        }else {
            CameraManager.FRAME_HEIGHT = (int) (DisplayUtil.screenWidthPx / frameHeight);
        }

        Rect frame = CameraManager.getCameraManager().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        drawTipText(canvas, getResources().getDisplayMetrics().widthPixels, frame.bottom + mTipPaddingTop);
        drawLaser(canvas, frame);
        if (resultBitmap != null) {
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
            drawFrameBounds(canvas, frame);
            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);

                if (isCircle) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);

                if (isCircle) {
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                    }
                }
            }
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
        mTipPaddingTop = 88;
        if (mScanParamsBean.getScanColor()==0){
            mLaserPaint.setColor(getResources().getColor(R.color.tv_whtie));
        }else {
            mLaserPaint.setColor(scanColor);
        }
    }

    //绘制提示语
    private void drawTipText(Canvas canvas, int w, int h) {
        if (!TextUtils.isEmpty(scannerTipString)) {
            float l = (w - scannerTipString.length() * mTipPaint.getTextSize()) / 2;
            if (mScanParamsBean.getScannerTipColor() == 0) {
                mTipPaint.setColor(ContextCompat.getColor(ScannerConfig.getContext(), R.color.tv_whtie));
            } else {
                mTipPaint.setColor(scannerTipColor);
            }
            if (mScanParamsBean.getScannerTipSize() == 0) {
                mTipPaint.setTextSize(45);
            } else {
                mTipPaint.setTextSize(scannerTipSize);
            }
            canvas.drawText(scannerTipString, l, h, mTipPaint);
        }
    }


    /**
     * 绘制取景框边框
     *
     * @param canvas
     * @param frame
     */
    private void drawFrameBounds(Canvas canvas, Rect frame) {
        if (mScanParamsBean.getInnerCornerColor() == 0) {
            paint.setColor(ContextCompat.getColor(ScannerConfig.getContext(), R.color.tv_whtie));
        } else {
            paint.setColor(innerCornerColor);
        }

        paint.setStyle(Paint.Style.FILL);

        if (mScanParamsBean.getInnerCornerWidth() == 0) {
            corWidth = 10;
        } else {
            corWidth = innercornerWidth;
        }

        if (mScanParamsBean.getInnerCornerLength() == 0) {
            corLength = 86;
        } else {
            corLength = innerCornerLength;
        }

        // 左上角
        canvas.drawRect(frame.left, frame.top, frame.left + corWidth, frame.top
                + corLength, paint);
        canvas.drawRect(frame.left, frame.top, frame.left
                + corLength, frame.top + corWidth, paint);
        // 右上角
        canvas.drawRect(frame.right - corWidth, frame.top, frame.right,
                frame.top + corLength, paint);
        canvas.drawRect(frame.right - corLength, frame.top,
                frame.right, frame.top + corWidth, paint);
        // 左下角
        canvas.drawRect(frame.left, frame.bottom - corLength,
                frame.left + corWidth, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - corWidth, frame.left
                + corLength, frame.bottom, paint);
        // 右下角
        canvas.drawRect(frame.right - corWidth, frame.bottom - corLength,
                frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - corLength, frame.bottom - corWidth,
                frame.right, frame.bottom, paint);
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    /**
     * 绘制激光线
     */
    private void drawLaser(Canvas canvas, Rect rect) {
        mLaserPaint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = rect.height() / 2 + rect.top;
        canvas.drawRect(rect.left + 2, middle - 1, rect.right - 1, middle + 2, mLaserPaint);
    }
}

