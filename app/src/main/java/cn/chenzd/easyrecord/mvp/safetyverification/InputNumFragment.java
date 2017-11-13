package cn.chenzd.easyrecord.mvp.safetyverification;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.base.ActivityCollector;
import cn.chenzd.easyrecord.entity.NumberPassword;
import cn.chenzd.easyrecord.utils.FingerprintUtil;
import cn.chenzd.easyrecord.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.content.Context.MODE_PRIVATE;
import static cn.chenzd.easyrecord.constant.Constant.MAX_INPUT_NUM_MAX_COUNT;
import static cn.chenzd.easyrecord.constant.Constant.PASSWORD_ERROR_WAIT_TIME;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_FINGER_PRINT;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_INPUT_COUNT;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_INPUT_LAST_TIME;
import static cn.chenzd.easyrecord.constant.Constant.SP_NAME;
import static cn.chenzd.easyrecord.constant.Constant.VIBRATE_TIME;
import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 数字密码 验证界面
 * Created by chenzaidong on 2017/9/11.
 */

public class InputNumFragment extends Fragment implements SafetyverificationContract.View {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_psw_1)
    ImageView mIvPsw1;
    @BindView(R.id.iv_psw_2)
    ImageView mIvPsw2;
    @BindView(R.id.iv_psw_3)
    ImageView mIvPsw3;
    @BindView(R.id.iv_psw_4)
    ImageView mIvPsw4;
    @BindView(R.id.tv_num_1)
    TextView mTvNum1;
    @BindView(R.id.tv_num_2)
    TextView mTvNum2;
    @BindView(R.id.tv_num_3)
    TextView mTvNum3;
    @BindView(R.id.tv_num_4)
    TextView mTvNum4;
    @BindView(R.id.tv_num_5)
    TextView mTvNum5;
    @BindView(R.id.tv_num_6)
    TextView mTvNum6;
    @BindView(R.id.tv_num_7)
    TextView mTvNum7;
    @BindView(R.id.tv_num_8)
    TextView mTvNum8;
    @BindView(R.id.tv_finger)
    TextView mTvFinger;
    @BindView(R.id.tv_num_0)
    TextView mTvNum0;
    @BindView(R.id.tv_delete)
    TextView mTvDelete;
    Unbinder unbinder;
    @BindView(R.id.ll_icon)
    LinearLayout llIcon;
    @BindView(R.id.tv_num_9)
    TextView tvNum9;
    @BindView(R.id.tv_input_count)
    TextView tvInputCount;
    @BindView(R.id.gl_psw)
    GridLayout glPsw;
    @BindView(R.id.textureView)
    TextureView mTextureView;

    private ArrayList<String> mPasswords = new ArrayList<>();
    private ImageView[] mImageViews = null;
    private SafetyverificationContract.Presenter mPresenter;
    private static final SparseIntArray ORIENTATION = new SparseIntArray();
    private String mCameraId;
    private Size mPreviewSize;
    private Size mCaptureSize;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;
    private CameraDevice mCameraDevice;
    private ImageReader mImageReader;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mCameraCaptureSession;
    private LocationManager mLocationManager;
    private SharedPreferences mSharedPreferences;
    private Disposable mDisposable;

    ///为了使照片竖直显示
    static {
        ORIENTATION.append(Surface.ROTATION_0, 270);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 0);
        ORIENTATION.append(Surface.ROTATION_270, 0);
    }

    private int mInputCount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new SafetyverificationPresenter(this);
        mSharedPreferences = getContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        mInputCount = mSharedPreferences.getInt(SP_KEY_INPUT_COUNT, MAX_INPUT_NUM_MAX_COUNT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_input_password, null);
        unbinder = ButterKnife.bind(this, view);
        mImageViews = new ImageView[]{mIvPsw1, mIvPsw2, mIvPsw3, mIvPsw4};
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean finger = sharedPreferences.getBoolean(SP_KEY_FINGER_PRINT, false);
        if (!finger) mTvFinger.setVisibility(View.INVISIBLE);
        if (mInputCount < MODE_PRIVATE && mInputCount >= 0) {
            tvInputCount.setText(getString(R.string.input_count, mInputCount));
        }
        if (mInputCount <= 0) {
            final long lastTime = sharedPreferences.getLong(SP_KEY_INPUT_LAST_TIME, System.currentTimeMillis());
            mDisposable = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            long time = System.currentTimeMillis();
                            if (time - lastTime <= PASSWORD_ERROR_WAIT_TIME * 1000) {
                                tvInputCount.setText(getString(R.string.please_wait, (PASSWORD_ERROR_WAIT_TIME * 1000 + lastTime - time) / 1000));
                            } else {
                                for (int i = 0; i < glPsw.getChildCount(); i++) {
                                    View childView = glPsw.getChildAt(i);
                                    childView.setClickable(true);
                                    childView.setAlpha(1.0f);
                                }
                                mInputCount = MAX_INPUT_NUM_MAX_COUNT;
                                sharedPreferences.edit().putInt(SP_KEY_INPUT_COUNT, MAX_INPUT_NUM_MAX_COUNT).apply();
                                tvInputCount.setText(null);
                                mDisposable.dispose();
                            }
                        }
                    });

            for (int i = 0; i < glPsw.getChildCount(); i++) {
                View childView = glPsw.getChildAt(i);
                childView.setClickable(false);
                childView.setAlpha(0.5f);
            }
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mImageViews = null;
        mSupportCallbackListener = null;
        FingerprintUtil.registerSupportCallbackListener(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @OnClick({R.id.tv_num_1, R.id.tv_num_2, R.id.tv_num_3, R.id.tv_num_4, R.id.tv_num_5, R.id.tv_num_6, R.id.tv_num_7, R.id.tv_num_8, R.id.tv_num_9, R.id.tv_finger, R.id.tv_num_0, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_num_1:
            case R.id.tv_num_2:
            case R.id.tv_num_3:
            case R.id.tv_num_4:
            case R.id.tv_num_5:
            case R.id.tv_num_6:
            case R.id.tv_num_7:
            case R.id.tv_num_8:
            case R.id.tv_num_9:
            case R.id.tv_num_0:
                setNum(((TextView) view).getText().toString());
                break;
            case R.id.tv_finger:
                fingerprint();
                break;
            case R.id.tv_delete:
                if (mPasswords.size() > 0) {
                    mPasswords.remove(mPasswords.size() - 1);
                    setIvNum(mPasswords.size());
                }
                break;
        }
    }

    /**
     * 验证当前设备是否支持指纹解锁
     */
    private void fingerprint() {
        FingerprintUtil.registerSupportCallbackListener(mSupportCallbackListener);
        FingerprintUtil.supportFingerprint();

    }

    private void setNum(String num) {
        mPasswords.add(num);
        setIvNum(mPasswords.size());
        if (mPasswords.size() == 4) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < mPasswords.size(); i++) {
                stringBuffer.append(mPasswords.get(i));
            }
            NumberPassword numberPassword = new NumberPassword();
            numberPassword.setPassword(stringBuffer.toString());
            mPresenter.checkNumberPassword(numberPassword);
        }
    }

    private FingerprintUtil.OnSupportCallbackListener mSupportCallbackListener = new FingerprintUtil.OnSupportCallbackListener() {
        @Override
        public void onSupport() {
            getFragmentManager().beginTransaction().replace(R.id.fl_content, new FingerprintVerificationFragment()).commit();
        }

        @Override
        public void onSupportFailed() {
            Toast.makeText(getContext(), R.string.error_hardware_not_detected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInsecurity() {
            Toast.makeText(getContext(), R.string.error_keyguard_not_secured, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnrollFailed() {
            Toast.makeText(getContext(), R.string.error_has_not_enrolled_fingerprints, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVersionFailed() {
            Toast.makeText(getContext(), R.string.error_version_less_6, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 设置圆点图标
     */
    private void setIvNum(int num) {
        for (int i = 0; i < mImageViews.length; i++) {
            if (i <= num - 1) {
                mImageViews[i].setImageResource(R.mipmap.ic_point_input);
            } else {
                mImageViews[i].setImageResource(R.mipmap.ic_point_normal);
            }
        }
    }

    @Override
    public void setPresenter(SafetyverificationContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraThread();
            if (!mTextureView.isAvailable()) {
                mTextureView.setSurfaceTextureListener(mTextureListener);
            } else {
                openCamera();
            }
        }
        startLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }

        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        stopLocation();
    }

    private void startCameraThread() {
        mCameraThread = new HandlerThread("CameraThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }

    private TextureView.SurfaceTextureListener mTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //当SurefaceTexture可用的时候，设置相机参数并打开相机
            setupCamera(width, height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void setupCamera(int width, int height) {
        //获取摄像头的管理者CameraManager
        CameraManager manager = (CameraManager) EasyRecordApplication.getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            //遍历所有摄像头
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                //此处默认打开前置摄像头
                if (facing == null || facing == CameraCharacteristics.LENS_FACING_BACK)
                    continue;
                //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                //根据TextureView的尺寸设置预览尺寸
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                //获取相机支持的最大拍照尺寸
                mCaptureSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new Comparator<Size>() {
                    @Override
                    public int compare(Size lhs, Size rhs) {
                        return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getHeight() * rhs.getWidth());
                    }
                });
                //此ImageReader用于拍照所需
                setupImageReader();
                mCameraId = cameraId;
                break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //选择sizeMap中大于并且最接近width和height的size
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizeMap[0];
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) EasyRecordApplication.getContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (!TextUtils.isEmpty(mCameraId))
                manager.openCamera(mCameraId, mStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private void startPreview() {
        SurfaceTexture mSurfaceTexture = mTextureView.getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(mSurfaceTexture);
        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        mCaptureRequest = mCaptureRequestBuilder.build();
                        mCameraCaptureSession = session;
                        mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void takePicture() {
        lockFocus();
    }

    private void lockFocus() {
        try {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mCameraCaptureSession.capture(mCaptureRequestBuilder.build(), mCaptureCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            capture();
        }
    };

    private void capture() {
        try {
            final CaptureRequest.Builder mCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            mCaptureBuilder.addTarget(mImageReader.getSurface());
            mCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));
            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    unLockFocus();
                }
            };
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.capture(mCaptureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void unLockFocus() {
        try {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void setupImageReader() {
        //2代表ImageReader中最多可以获取两帧图像流
        mImageReader = ImageReader.newInstance(mCaptureSize.getWidth(), mCaptureSize.getHeight(),
                ImageFormat.JPEG, MAX_INPUT_NUM_MAX_COUNT);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mPresenter.saveImage(reader.acquireNextImage(), System.currentTimeMillis() + ".jpg");
            }
        }, mCameraHandler);
    }


    /**
     * 密码验证回调
     *
     * @param pass
     */
    @Override
    public void onCheckNumberPassword(boolean pass) {
        if (pass) {
            mTvTitle.setText(R.string.thanks_for_using);
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else {
            mTvTitle.setText(R.string.error_password);
            mPasswords.clear();
            setIvNum(mPasswords.size());
            TranslateAnimation animation = new TranslateAnimation(0, 10, 0, 0);
            animation.setInterpolator(new OvershootInterpolator());
            animation.setDuration(100);
            animation.setRepeatCount(3);
            animation.setRepeatMode(Animation.REVERSE);
            llIcon.startAnimation(animation);
            glPsw.startAnimation(animation);
            Vibrator vib = (Vibrator) EasyRecordApplication.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(VIBRATE_TIME);

            mInputCount--;
            mSharedPreferences.edit().putInt(SP_KEY_INPUT_COUNT, mInputCount).apply();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
            if (mInputCount == 0) {
                mSharedPreferences.edit().putLong(SP_KEY_INPUT_LAST_TIME, System.currentTimeMillis()).apply();
                ActivityCollector.finishAll();
            } else {
                tvInputCount.setText(getString(R.string.input_count, mInputCount));
            }

        }
    }

    private void startLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && Utils.isOPenOfGPS()) {
            if (mLocationManager == null)
                mLocationManager = (LocationManager) EasyRecordApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            Logger.i("开启定位");
        }
    }

    private void stopLocation() {
        if (Utils.isOPenOfGPS()) {
            if (mLocationManager == null)
                mLocationManager = (LocationManager) EasyRecordApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.removeUpdates(locationListener);
            Logger.i("结束定位");
        }
    }

    private static LocationListener locationListener
            = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Logger.i("onLocationChanged");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Logger.i("onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Logger.i("onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Logger.i("onProviderDisabled");
        }
    };
}
