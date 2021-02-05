package com.dolphkon.scanlib;
import android.content.Context;
import com.dolphkon.scanlib.activity.ProcessScanActivity;
import com.dolphkon.scanlib.listener.OnError;
import com.dolphkon.scanlib.listener.OnSuccess;
import com.dolphkon.scanlib.open.CameraFacing;
import com.dolphkon.scanlib.utils.CodeUtils;
import com.dolphkon.scanlib.utils.ScanParamsBean;


/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scanlib
 * ClassName: Scan
 * Author: kongdexi
 * Date: 2020/7/1 17:09
 * Description:TODO
 * *****************************************************
 */
public class ScanManager {
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
    private int innerCornerWidth;
    // 是否展示小圆点
    private boolean isCircle;
    //扫码框距离顶部的距离
    private float frameMarginTop;
    //扫码框的高度
    private float frameHeight;
    //扫码框的宽度
    private float frameWidth;
    //扫描线的颜色
    private int scanColor;

    // 标题名称
    private String titleStr;
    //标题右边名称
    private String rightStr;

    //标题右边名称颜色
    private int rightColor;
    //标题背景色
    private int titleBackgroundColor;
    // 标题颜色
    private int titleColor;

    private boolean isCameraSwitch;

    // 设置默认的摄像头类型（前置/后置）
    private CameraFacing cameraFaceType;

    private Context context;
    public static OnSuccess onSuccess;
    public static OnError onError;
    private ScanParamsBean scanParamsBean;
    private long timeOut; //扫码超时时间

    private static ScanManager scanManager;


    private CodeUtils.CameraListener mListener;
    public ScanManager(Builder builder) {
        this.context = builder.context;
        onSuccess = builder.onSuccess;
        onError = builder.onError;
        this.scannerTipString = builder.scannerTipString;
        this.scannerTipColor = builder.scannerTipColor;
        this.scannerTipSize = builder.scannerTipSize;
        this.innerCornerColor = builder.innerCornerColor;
        this.innerCornerLength = builder.innerCornerLength;
        this.innerCornerWidth = builder.innerCornerWidth;
        this.isCircle = builder.isCircle;
        this.scanColor = builder.scanColor;
        this.scanParamsBean = builder.scanParamsBean;
        this.cameraFaceType=builder.cameraFaceType;
        this.timeOut=builder.timeOut;
         scanManager=builder.mScanManager;
         this.isCameraSwitch=builder.isCameraSwitch;
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {
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
        private int innerCornerWidth;
        //扫码框距离顶部的距离
        private float frameMarginTop;
        //扫码框的高度
        private float frameHeight;
        //扫码框的宽度
        private float frameWidth;
        private int scanColor;

        // 标题名称
        private String titleStr;
        //标题右边名称
        private String rightStr;

        //标题右边名称颜色
        private int rightColor;
        //标题背景色
        private int titleBackgroundColor;
        // 标题颜色
        private int titleColor;
      // 设置摄像头切换类型(前置/后置)
        private CameraFacing cameraFaceType;

        //超时时间
        private long timeOut;
        private ScanManager mScanManager;
        private boolean isCameraSwitch;


        public Builder setFrameMarginTop(float frameMarginTop) {
            this.frameMarginTop = frameMarginTop;
            scanParamsBean.setFrameMarginTop(frameMarginTop);
            return this;
        }

        public Builder setFrameHeight(float frameHeight) {
            this.frameHeight = frameHeight;
            scanParamsBean.setFrameHeight(frameHeight);
            return this;
        }

        public Builder setframeWidth(float frameWidth) {
            this.frameWidth = frameWidth;
            scanParamsBean.setFrameWidth(frameWidth);
            return this;
        }

        public Builder setTimeOut(long timeOut){
            this.timeOut=timeOut;
            scanParamsBean.setTimeOut(timeOut);
            return this;
        }

        // 是否展示小圆点
        private boolean isCircle;
        private Context context;
        public OnSuccess onSuccess;
        public OnError onError;
        private ScanParamsBean scanParamsBean;

        private Builder(Context context) {
            scanParamsBean = new ScanParamsBean();

            this.context = context;
        }

        public Builder setOnSuccess(OnSuccess onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }

        public Builder setOnError(OnError onError) {
            this.onError = onError;
            return this;
        }

        public Builder setScannerTipColor(int scannerTipColor) {
            this.scannerTipColor = scannerTipColor;
            scanParamsBean.setScannerTipColor(scannerTipColor);
            return this;
        }

        public Builder setScanColor(int scanColor) {
            this.scanColor = scanColor;
            scanParamsBean.setScanColor(scanColor);
            return this;
        }

        public Builder setScannerTipSize(int scannerTipSize) {
            this.scannerTipSize = scannerTipSize;
            scanParamsBean.setScannerTipSize(scannerTipSize);
            return this;
        }

        public Builder setScannerTipString(String scannerTipString) {
            this.scannerTipString = scannerTipString;
            scanParamsBean.setScannerTipString(scannerTipString);
            return this;
        }

        public Builder setInnerCornerColor(int innerCornerColor) {
            this.innerCornerColor = innerCornerColor;
            scanParamsBean.setInnerCornerColor(innerCornerColor);
            return this;
        }

        public Builder setInnerCornerLength(int innerCornerLength) {
            this.innerCornerLength = innerCornerLength;
            scanParamsBean.setInnerCornerLength(innerCornerLength);
            return this;
        }

        public Builder setInnerCornerWidth(int innerCornerWidth) {
            this.innerCornerWidth = innerCornerWidth;
            scanParamsBean.setInnerCornerWidth(innerCornerWidth);
            return this;
        }


        public Builder setCircle(boolean circle) {
            isCircle = circle;
            scanParamsBean.setCircle(circle);
            return this;
        }

        public Builder setTitleStr(String titleStr) {
            this.titleStr = titleStr;
            scanParamsBean.setTitleStr(titleStr);
            return this;
        }

        public Builder setRightStr(String rightStr) {
            this.rightStr = rightStr;
            scanParamsBean.setRightStr(rightStr);
            return this;
        }

        public Builder setRightColor(int rightColor) {
            this.rightColor = rightColor;
            scanParamsBean.setRightColor(rightColor);
            return this;
        }

        public Builder setTitleBackgroundColor(int titleBackgroundColor) {
            this.titleBackgroundColor = titleBackgroundColor;
            scanParamsBean.setTitleBackgroundColor(titleBackgroundColor);
            return this;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            scanParamsBean.setTitleColor(titleColor);
            return this;
        }

        public Builder setCameraFaceType(CameraFacing cameraFaceType){
            this.cameraFaceType=cameraFaceType;
            scanParamsBean.setCameraFaceType(cameraFaceType);
            return this;
        }

        public Builder setIsCameraSwitch(boolean isCameraSwitch){
            this.isCameraSwitch=isCameraSwitch;
            scanParamsBean.setCameraSwitch(isCameraSwitch);
            return this;
        }


        /**
         *  开始扫码
         * */
        public ScanManager startScan() {
            if (context != null) {
                ScannerConfig.init(context);
            }
            context.startActivity(ProcessScanActivity.newInsatance(context, scanParamsBean));
            scanManager=new ScanManager(this);
            return scanManager;
        }
    }



    public void addCameraListener(CodeUtils.CameraListener listener){
        mListener=listener;

    }

    public static ScanManager getScanManager(){
        return scanManager;
    }

    /**
     *  停止扫码
     * */
    public void StopScan(){
        mListener.stopScan();
    }
}
