package com.dolphkon.scanlib.activity;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.dolphkon.scanlib.R;
import com.gyf.barlibrary.ImmersionBar;


/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scanlib.activity
 * ClassName: BaseActivity
 * Author: kongdexi
 * Date: 2020/7/15 11:42
 * Description:TODO
 * *****************************************************
 */
public abstract  class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ImmersionBar.with(this)
                .statusBarDarkFont(true, 0.2f)//顶部状态栏，如果顶部颜色不可变时，将显示灰色阴影，顶部状态栏全白
                .statusBarColor(R.color.tv_whtie)
                .init();
    }
    void onClickLeft() {
        onBackPressed();
    }

    public abstract void setRightColor(int rightColor);

    public abstract void setTitleColor(int titleColor);

    public abstract void setTitlebarBackground(int backgroundColor);
}
