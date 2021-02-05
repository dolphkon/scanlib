package com.dolphkon.scanlib.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.utils.CodeUtils;
import com.dolphkon.scanlib.utils.PermissionUtil;
import com.dolphkon.scanlib.utils.ScanParamsBean;
import static com.dolphkon.scanlib.ScanManager.onError;
import static com.dolphkon.scanlib.ScanManager.onSuccess;
import static com.dolphkon.scanlib.activity.ScanActivity.ONSUCCESS;
import static com.dolphkon.scanlib.utils.CodeUtils.REQUEST_CODE;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scanlib.activity
 * ClassName: ProcessScanActivity
 * Author: kongdexi
 * Date: 2020/7/6 16:12
 * Description:调起扫描的Activity
 * *****************************************************
 */
public class ProcessScanActivity extends BaseActivity implements View.OnClickListener {
    private ScanParamsBean scanParamsBean;
    private boolean isOpen;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ImageView mImBack;
    private RelativeLayout titlelayout;

    public static Intent newInsatance(Context context, ScanParamsBean scanParamsBean) {
        Intent intent = new Intent(context, ProcessScanActivity.class);
        intent.putExtra("scanParamsBean", scanParamsBean);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        scanParamsBean = (ScanParamsBean) getIntent().getSerializableExtra("scanParamsBean");
        initView();
    }

    @Override
    public void setRightColor(int rightColor) {
        mTvRight.setTextColor(rightColor);
    }


    @Override
    public void setTitleColor(int titleColor) {
        mTvTitle.setTextColor( titleColor);
    }

    @Override
    public void setTitlebarBackground(int backgroundColor) {
        titlelayout.setBackgroundColor(backgroundColor);
    }

    private void initView() {
        titlelayout = findViewById(R.id.titlelayout);
        mTvTitle = findViewById(R.id.title);
        mTvRight = findViewById(R.id.tv_right);
        mImBack = findViewById(R.id.im_back);

        mTvTitle.setText(TextUtils.isEmpty(scanParamsBean.getTitleStr()) ? "扫码二维码" : scanParamsBean.getTitleStr());
        mImBack.setOnClickListener(this);
        mTvRight.setOnClickListener(this);

        //动态权限申请
        if (ContextCompat.checkSelfPermission(ProcessScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProcessScanActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            //扫码
            goScan();
        }
        if (scanParamsBean.getRightColor()!=0){
            setRightColor(scanParamsBean.getRightColor());
        }

        if (scanParamsBean.getTitleColor()!=0){
            setTitleColor(scanParamsBean.getTitleColor());
        }

        if (scanParamsBean.getTitleBackgroundColor()!=0){
            setTitlebarBackground(scanParamsBean.getTitleBackgroundColor());
        }
    }



    private void goScan() {
        Intent intent = new Intent(ProcessScanActivity.this, ScanActivity.class);
        intent.putExtra("scanParamsBean", scanParamsBean);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOpen) {
            return;
        }
        isOpen = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString(CodeUtils.RESULT_STRING);
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    if (onSuccess != null) {
                        onSuccess.onSuccess(ONSUCCESS,result);
                        onSuccess = null;
                    }
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    String msg = bundle.getString(CodeUtils.RESULT_STRING);
                    String code=bundle.getString(CodeUtils.RESULT_CODE);
                    if (onError != null) {
                        onError.onError(code,msg);
                        onError = null;
                    }
                }
            }
        }
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //扫码
                    goScan();
                } else {
                    if (onError != null) {
                        PermissionUtil.showCameraAndStorageNoti(this);
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.im_back) {
            onClickLeft();
        } else if (id == R.id.tv_right) {
        }
    }




}
