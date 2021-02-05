package com.dolphkon.scanlib.utils;

import com.dolphkon.scanlib.open.CameraFacing;
import java.io.Serializable;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scanlib.utils
 * ClassName: ScanParamsBean
 * Author: kongdexi
 * Date: 2020/7/6 16:11
 * Description:TODO
 * *****************************************************
 */
public class ScanParamsBean implements Serializable {
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
    // 标题名称
    private String titleStr;
    //标题右边名称
    private String rightStr;

    public boolean isCameraSwitch() {
        return isCameraSwitch;
    }

    public void setCameraSwitch(boolean cameraSwitch) {
        isCameraSwitch = cameraSwitch;
    }

    private boolean isCameraSwitch;

    public boolean isStop() {
        return isStop;
    }

    public void stopScan(boolean stop) {
        isStop = stop;
    }

    private boolean isStop;


    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    private long timeOut;

    public CameraFacing getCameraFaceType() {
        return cameraFaceType;
    }

    public void setCameraFaceType(CameraFacing cameraFaceType) {
        this.cameraFaceType = cameraFaceType;
    }

    private CameraFacing cameraFaceType;

    public String getTitleStr() {
        return titleStr;
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public String getRightStr() {
        return rightStr;
    }

    public void setRightStr(String rightStr) {
        this.rightStr = rightStr;
    }

    public int getRightColor() {
        return rightColor;
    }

    public void setRightColor(int rightColor) {
        this.rightColor = rightColor;
    }

    public int getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public void setTitleBackgroundColor(int titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    //标题右边名称颜色
    private int rightColor;
    //标题背景色
    private int titleBackgroundColor;
    // 标题颜色
    private int titleColor;


    public int getScannerTipColor() {
        return scannerTipColor;
    }

    public void setScannerTipColor(int scannerTipColor) {
        this.scannerTipColor = scannerTipColor;
    }

    public int getScannerTipSize() {
        return scannerTipSize;
    }

    public void setScannerTipSize(int scannerTipSize) {
        this.scannerTipSize = scannerTipSize;
    }

    public String getScannerTipString() {
        return scannerTipString;
    }

    public void setScannerTipString(String scannerTipString) {
        this.scannerTipString = scannerTipString;
    }

    public int getInnerCornerColor() {
        return innerCornerColor;
    }

    public void setInnerCornerColor(int innerCornerColor) {
        this.innerCornerColor = innerCornerColor;
    }

    public int getInnerCornerLength() {
        return innerCornerLength;
    }

    public void setInnerCornerLength(int innerCornerLength) {
        this.innerCornerLength = innerCornerLength;
    }

    public int getInnerCornerWidth() {
        return innerCornerWidth;
    }

    public void setInnerCornerWidth(int innerCornerWidth) {
        this.innerCornerWidth = innerCornerWidth;
    }

 
    public float getFrameMarginTop() {
        return frameMarginTop;
    }

    public void setFrameMarginTop(float frameMarginTop) {
        this.frameMarginTop = frameMarginTop;
    }

    public float getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(float frameHeight) {
        this.frameHeight = frameHeight;
    }

    public float getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(float frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getScanColor() {
        return scanColor;
    }

    public void setScanColor(int scanColor) {
        this.scanColor = scanColor;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }


    @Override
    public String toString() {
        return "ScanParamsBean{" +
                "scannerTipColor=" + scannerTipColor +
                ", scannerTipSize=" + scannerTipSize +
                ", scannerTipString='" + scannerTipString + '\'' +
                ", innerCornerColor=" + innerCornerColor +
                ", innerCornerLength=" + innerCornerLength +
                ", innerCornerWidth=" + innerCornerWidth +
                ", frameMarginTop=" + frameMarginTop +
                ", frameHeight=" + frameHeight +
                ", frameWidth=" + frameWidth +
                ", titleStr='" + titleStr + '\'' +
                ", rightStr='" + rightStr + '\'' +
                ", rightColor=" + rightColor +
                ", titleBackgroundColor=" + titleBackgroundColor +
                ", titleColor=" + titleColor +
                ", scanColor=" + scanColor +
                ", isCircle=" + isCircle +
                '}';
    }

    //扫描线的颜色
    private int scanColor;

    // 是否展示小圆点
    private boolean isCircle;


}
