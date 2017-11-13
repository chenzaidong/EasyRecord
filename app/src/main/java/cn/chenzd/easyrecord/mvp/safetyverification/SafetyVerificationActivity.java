package cn.chenzd.easyrecord.mvp.safetyverification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.ActivityCollector;
import cn.chenzd.easyrecord.utils.FingerprintUtil;

import static cn.chenzd.easyrecord.constant.Constant.MAX_INPUT_NUM_MAX_COUNT;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_FINGER_PRINT;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_INPUT_COUNT;
import static cn.chenzd.easyrecord.constant.Constant.SP_NAME;

/**
 * 密码验证视图
 * Created by chenzaidong on 2017/9/10.
 */

public class SafetyVerificationActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.password_authentification_layout);
        final SharedPreferences sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        boolean finger = sharedPreferences.getBoolean(SP_KEY_FINGER_PRINT, false);
        int inputCount = sharedPreferences.getInt(SP_KEY_INPUT_COUNT,MAX_INPUT_NUM_MAX_COUNT);
        if (finger && inputCount >0) {
            FingerprintUtil.registerSupportCallbackListener(mOnSupportCallbackListener);
            FingerprintUtil.supportFingerprint();
        } else {
            numberPassword();
        }
    }

    private FingerprintUtil.OnSupportCallbackListener mOnSupportCallbackListener = new FingerprintUtil.OnSupportCallbackListener() {
        @Override
        public void onSupport() {
            fingerprint();
        }

        @Override
        public void onSupportFailed() {
            numberPassword();
        }

        @Override
        public void onInsecurity() {
            numberPassword();
        }

        @Override
        public void onEnrollFailed() {
            numberPassword();
        }

        @Override
        public void onVersionFailed() {
            numberPassword();
        }
    };

    private void fingerprint() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_content, new FingerprintVerificationFragment()).commit();
    }

    private void numberPassword() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_content, new InputNumFragment()).commit();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnSupportCallbackListener = null;
        FingerprintUtil.registerSupportCallbackListener(null);
        ActivityCollector.removeActivity(this);

    }
}
