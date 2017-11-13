package cn.chenzd.easyrecord.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import cn.chenzd.easyrecord.app.EasyRecordApplication;

/**
 * Created by chenzaidong on 2017/9/11.
 */

public class FingerprintUtil {
    public static CancellationSignal cancellationSignal;
    private static OnSupportCallbackListener mListener;
    private static OnCallBackListener mOnCallBackListner;

    public static void registerSupportCallbackListener(OnSupportCallbackListener listener) {
        mListener = listener;
    }

    public static void registerCallBackListener(OnCallBackListener listner) {
        mOnCallBackListner = listner;
    }

    /**
     * 判断当前设备是否支持指纹解锁
     */
    public static void supportFingerprint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mListener != null)
                mListener.onVersionFailed();
            return;
        }
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(EasyRecordApplication.getContext());
        if (!managerCompat.isHardwareDetected()) { //判断设备是否支持
            if (mListener != null)
                mListener.onSupportFailed();
            return;
        }
        KeyguardManager keyguardManager = (KeyguardManager) EasyRecordApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中
            if (mListener != null)
                mListener.onInsecurity();
            return;
        }
        if (!managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
            if (mListener != null)
                mListener.onEnrollFailed(); //未注册
            return;
        }
        if (mListener != null) mListener.onSupport();
    }

    public static void callFingerPrint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mOnCallBackListner != null)
                mOnCallBackListner.onVersionFailed();
            return;
        }
        FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(EasyRecordApplication.getContext());
        if (!managerCompat.isHardwareDetected()) { //判断设备是否支持
            if (mOnCallBackListner != null)
                mOnCallBackListner.onSupportFailed();
            return;
        }
        KeyguardManager keyguardManager = (KeyguardManager) EasyRecordApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (!keyguardManager.isKeyguardSecure()) {//判断设备是否处于安全保护中
            if (mOnCallBackListner != null)
                mOnCallBackListner.onInsecurity();
            return;
        }
        if (!managerCompat.hasEnrolledFingerprints()) { //判断设备是否已经注册过指纹
            if (mOnCallBackListner != null)
                mOnCallBackListner.onEnrollFailed(); //未注册
            return;
        }
        if (mOnCallBackListner != null)
            mOnCallBackListner.onAuthenticationStart(); //开始指纹识别
        cancellationSignal = new CancellationSignal();
        managerCompat.authenticate(null, 0, cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback() {
            // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息，系统给出
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (mOnCallBackListner != null)
                    mOnCallBackListner.onAuthenticationError(errMsgId, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                if (mOnCallBackListner != null)
                    mOnCallBackListner.onAuthenticationHelp(helpMsgId, helpString);
            }

            // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                if (mOnCallBackListner != null)
                    mOnCallBackListner.onAuthenticationSucceeded(result);
            }

            // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，
            // 失败次数过多会停止响应一段时间然后再停止sensor的工作
            //多次失败会调用 onAuthenticationError() 方法
            @Override
            public void onAuthenticationFailed() {
                if (mOnCallBackListner != null)
                    mOnCallBackListner.onAuthenticationFailed();
            }
        }, null);
    }

    public static void cancel() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }

    }

    public interface OnCallBackListener {
        void onSupportFailed();

        void onInsecurity();

        void onEnrollFailed();

        void onAuthenticationStart();

        void onAuthenticationError(int errMsgId, CharSequence errString);

        void onAuthenticationFailed();

        void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

        void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);

        void onVersionFailed();
    }

    public interface OnSupportCallbackListener {
        void onSupport();//支持

        void onSupportFailed();//硬件不支持

        void onInsecurity();//设备未于安全保护中

        void onEnrollFailed(); //未注册过指纹

        void onVersionFailed();//版本过低
    }

}
