package com.dolphkon.scanlib.listener;
/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scanlib.utils
 * ClassName: OnError
 * Author: kongdexi
 * Date: 2020/7/7 14:42
 * Description:失败的回调
 * *****************************************************
 */
public interface OnError {
    void   onError(String code,String errMsg);

}
