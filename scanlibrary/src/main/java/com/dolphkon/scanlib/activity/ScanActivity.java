package com.dolphkon.scanlib.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gyf.barlibrary.ImmersionBar;
import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.utils.CodeUtils;
import com.dolphkon.scanlib.utils.ImageUtil;
import com.dolphkon.scanlib.utils.QRCodeAnalyzeUtils;
import com.dolphkon.scanlib.utils.ScanParamsBean;
import static com.dolphkon.scanlib.ScanManager.onError;
import static com.dolphkon.scanlib.ScanManager.onSuccess;
import static com.dolphkon.scanlib.utils.CodeUtils.REQUEST_IMAGE;

/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.activity
 * ClassName: ScannerViewActivity
 * Author: kongdexi
 * Date: 2020/7/1 10:43
 * Description:默认的扫描界面 Activity
 * *****************************************************
 */
public class ScanActivity extends BaseActivity implements View.OnClickListener{
    public final int PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0X11;
    public  static final String ONSUCCESS="00";  //成功
    public static final String  ONCANNER="01";    //取消
    public static final String ONTIMEOUT="02";   //超時
    public static final String OnError="03";  //未识别到二维码
    private ScanParamsBean scanParamsBean;
    private static final String TAG = "ScanActivity";
    private TextView mTvTitle;
    private TextView mTvRight;
    private ImageView mImBack;
    private RelativeLayout titlelayout;
    private CodeUtils.CameraListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scanParamsBean = (ScanParamsBean) getIntent().getSerializableExtra("scanParamsBean");
        initView();
        Bundle bundle = new Bundle();
        bundle.putSerializable("scanParamsBean", scanParamsBean);
        ScanFragment scannerFragment = new ScanFragment();
        scannerFragment.setArguments(bundle);
        scannerFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_zxing_container, scannerFragment).commit();
        scannerFragment.setCameraInitCallBack(e -> {
            if (e == null) {
                Log.e(TAG, "相机初始化成功: ");
            } else {
                Log.e(TAG, "相机初始化失败: ", e);
                Intent resultIntent = new Intent();
                Bundle bundle1 = new Bundle();
                bundle1.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
                bundle1.putString(CodeUtils.RESULT_STRING,getResources().getString(R.string.canmera_init_fail));
                resultIntent.putExtras(bundle1);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    public void setCameraListener(CodeUtils.CameraListener listener){
        mListener=listener;
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


    private void goPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }


    private void initView() {
        titlelayout = findViewById(R.id.titlelayout);
        mTvTitle = findViewById(R.id.title);
        mTvRight = findViewById(R.id.tv_right);
        mImBack = findViewById(R.id.im_back);
        RelativeLayout viewById2 = findViewById(R.id.ll_content);
        viewById2.setPadding(0,getStatusBarHeight(ScanActivity.this),0,0);
        if (scanParamsBean.getTitleBackgroundColor()!=0){
            ImmersionBar.with(this).statusBarColor(int2Hex(scanParamsBean.getTitleBackgroundColor())).init();
        }

        mTvTitle.setText(TextUtils.isEmpty(scanParamsBean.getTitleStr()) ? "扫码二维码" : scanParamsBean.getTitleStr());
        mImBack.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        if (TextUtils.isEmpty(scanParamsBean.getRightStr())) {
            mTvRight.setVisibility(View.GONE);
        }else {
            mTvRight.setVisibility(View.VISIBLE);
            mTvRight.setText(scanParamsBean.getRightStr());
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


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING,getResources().getString(R.string.scan_fail));
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onAnalyzeTimeOut() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING,getResources().getString(R.string.scan_time_out));
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        @Override
        public void onAnalyzeCanner() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING,getResources().getString(R.string.scan_canner));
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode==REQUEST_IMAGE){
                QRCodeAnalyzeUtils.analyzeQRCode(ImageUtil.getImageAbsolutePath(ScanActivity.this, data.getData()), new CodeUtils.AnalyzeCallback() {
                    @Override
                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                        if (onSuccess!=null){
                            onSuccess.onSuccess(ONSUCCESS,result);
                            onSuccess = null;
                        }
                    }
                    @Override
                    public void onAnalyzeFailed() {
                        if (onError != null) {
                            onError.onError(OnError,getResources().getString(R.string.scan_QRC_fail));
                            onError = null;
                        }
                    }

                    @Override
                    public void onAnalyzeTimeOut() {
                        if (onError != null) {
                            onError.onError(ONTIMEOUT,getResources().getString(R.string.scan_time_out));
                            onError = null;
                        }
                    }

                    @Override
                    public void onAnalyzeCanner() {
                        if (onError != null) {
                            onError.onError(ONCANNER,getResources().getString(R.string.scan_canner));
                            onError = null;
                        }
                    }

                });
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goPhoto();
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mListener!=null){
            mListener.stopScan();
        }
        if (onError != null) {
            onError.onError(ONCANNER,getResources().getString(R.string.scan_canner));
            onError = null;
        }
    }


    /**Color的Int整型转Color的16进制颜色值【方案一】
     * colorInt - -12590395
     * return Color的16进制颜色值——#3FE2C5
     * */
    public static String int2Hex(int colorInt){
        String hexCode = "";
        hexCode = String.format("#%06X", Integer.valueOf(16777215 & colorInt));
        return hexCode;
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.im_back) {
            onClickLeft();
        } else if (id == R.id.tv_right) {//先申请权限
            int checked = ContextCompat.checkSelfPermission(ScanActivity.this
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checked == PackageManager.PERMISSION_GRANTED) {
                goPhoto();
            } else {
                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        }
    }
}
