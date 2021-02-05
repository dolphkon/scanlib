package com.dolphkon.scan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dolphkon.scanlib.ScanManager;
import com.dolphkon.scanlib.listener.OnError;
import com.dolphkon.scanlib.listener.OnSuccess;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button tvScan = findViewById(R.id.tv_scan);
        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanManager.with(MainActivity.this)
                        .setScannerTipString("请扫描二维码")
                        .setFrameMarginTop(4)
                        .setScanColor(ContextCompat.getColor(MainActivity.this, R.color.scannerview_laser))
                        .setScannerTipColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                        .setInnerCornerColor(ContextCompat.getColor(MainActivity.this,R.color.scannerview_laser))
                        .setRightStr("相册")
                        .setTitleBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.result_points))
                        .setTitleColor(ContextCompat.getColor(MainActivity.this,R.color.tv_whtie))
                        .setRightColor(ContextCompat.getColor(MainActivity.this,R.color.tv_whtie))
                        .setOnSuccess(new OnSuccess() {
                            @Override
                            public void onSuccess(String code, String result) {
                                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setOnError(new OnError() {
                            @Override
                            public void onError(String code, String errMsg) {
                                Toast.makeText(MainActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .startScan();
            }
        });
    }
}
