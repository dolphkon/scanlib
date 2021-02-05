package com.dolphkon.scanlib.activity;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.dolphkon.scanlib.R;
import com.dolphkon.scanlib.ScanManager;
import com.dolphkon.scanlib.camera.CameraManager;
import com.dolphkon.scanlib.decode.CaptureActivityHandler;
import com.dolphkon.scanlib.decode.InactivityTimer;
import com.dolphkon.scanlib.open.CameraFacing;
import com.dolphkon.scanlib.utils.CodeUtils;
import com.dolphkon.scanlib.utils.ScanParamsBean;
import com.dolphkon.scanlib.view.ScannerView;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;


/**
 * ****************************************************
 * Project: android-common
 * PackageName: com.dolphkon.scannerlib.activity
 * ClassName: ScannerViewFragment
 * Author: kongdexi
 * Date: 2020/7/1 10:16
 * Description:默认的扫码界面fragment
 * *****************************************************
 */
public class ScanFragment extends Fragment implements SurfaceHolder.Callback, CodeUtils.CameraListener {
    private static final String TAG = "ScanFragment";
    private CaptureActivityHandler handler;
    private ScannerView mScanView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CodeUtils.AnalyzeCallback analyzeCallback;
    private ScanParamsBean scanParamsBean;
    private CameraFacing cfbf ;
    private Map<DecodeHintType, ?> decodeHints;
    private ImageView switchCamera;
    private ScanCountDown scanCountDown;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this.getActivity());
        CameraManager.init(getActivity());
        ((ScanActivity)getActivity()).setCameraListener(this);
        ScanManager.getScanManager().addCameraListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_scan, null);
        }
        mScanView = (ScannerView) view.findViewById(R.id.mScanview);
        surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
        switchCamera = (ImageView) view.findViewById(R.id.iv_camera);
        surfaceHolder = surfaceView.getHolder();
        Bundle bundle = getArguments();
        scanParamsBean = (ScanParamsBean)bundle.getSerializable("scanParamsBean");
        mScanView.setmScanParamsBean(scanParamsBean);
        if (scanParamsBean.isStop()){
            stopScan();
        }
        try {
            if (scanParamsBean.getTimeOut()==0){  //默认超时时间为20秒
                scanCountDown=new ScanCountDown(20000,1000);
            }else {
                scanCountDown=new ScanCountDown(scanParamsBean.getTimeOut(),1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (scanParamsBean.isCameraSwitch()){
            switchCamera.setVisibility(View.VISIBLE);
        }

        if (scanParamsBean.getCameraFaceType()==CameraFacing.BACK){
            cfbf=CameraFacing.BACK; //默认调用后置摄像头
        }else if (scanParamsBean.getCameraFaceType()==CameraFacing.FRONT){
            cfbf=CameraFacing.FRONT; //默认调用后置摄像头
        }else {
            if (scanParamsBean.isCameraSwitch()){
                cfbf=CameraFacing.FRONT; //默认调用后置摄像头
            }else {
                cfbf=CameraFacing.BACK; //默认调用后置摄像头
            }

        }
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"切换相机事件 点击监听");
                // 切换前后摄像头
                if(cfbf.equals(CameraFacing.FRONT)){
                    cfbf = CameraFacing.BACK;
                }else {
                    // 前置摄像头 不支持自动聚焦
                    cfbf = CameraFacing.FRONT;
                }
                initCamera(surfaceHolder);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inactivityTimer.shutdown();
    }

    public ScannerView getViewfinderView() {
        return mScanView;
    }

    /**
     * Handler scanlib result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {



        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(barcode, result.getText());
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provide");
        }
        if (CameraManager.getCameraManager().isOpen()) {

            // 如果相机已经打开 则关闭当前相机 重建一个 切换摄像头，，如果不需要切换前置摄像头 则这里直接return
            handler = null;
            CameraManager.getCameraManager().closeDriver();
        }
        try {
            CameraManager.getCameraManager().openDriver(surfaceHolder, cfbf);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, CameraManager.getCameraManager());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
            if (scanCountDown!=null){
                scanCountDown.start();
            }
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        mScanView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public void setAnalyzeCallback(CodeUtils.AnalyzeCallback analyzeCallback) {
        this.analyzeCallback = analyzeCallback;
    }

    @Nullable
    CameraInitCallBack callBack;

    /**
     * Set callback for Camera check whether Camera init success or not.
     */
    public void setCameraInitCallBack(CameraInitCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void stopScan() {
     if (handler==null){
         return;
     }
     if (!hasSurface){
         return;
     }
        handler.quitSynchronously();
        CameraManager.getCameraManager().closeDriver();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.removeCallback(this);
        handler = null;
        getActivity().finish();
    }

    interface CameraInitCallBack {
        /**
         * Callback for Camera init result.
         * @param e If is's null,means success.otherwise Camera init failed with the Exception.
         */
        void callBack(Exception e);
    }



    class ScanCountDown extends CountDownTimer{

        public ScanCountDown(long millisInFuture, long countDownInterval) throws Exception {
            super(millisInFuture, countDownInterval);   // 参数依次为总时长,和计时的时间间隔
            if (millisInFuture-countDownInterval<0){
                getActivity().finish();
                throw new Exception("总计时不能小于时间间隔1000毫秒");
            }
        }

        @Override
        public void onFinish() {    // 计时完毕时触发
            if (handler==null){
                return;
            }
            if (!hasSurface){
                return;
            }
            handler.quitSynchronously();
            CameraManager.getCameraManager().closeDriver();
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(ScanFragment.this);
            handler = null;
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeTimeOut();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {  // 计时过程显示

        }
    }


}
