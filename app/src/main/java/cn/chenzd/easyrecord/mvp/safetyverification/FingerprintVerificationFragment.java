package cn.chenzd.easyrecord.mvp.safetyverification;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.utils.FingerprintUtil;
import cn.chenzd.easyrecord.view.FingerprintDialog;


/**
 * 指纹验证
 * Created by chenzaidong on 2017/9/11.
 */

public class FingerprintVerificationFragment extends Fragment {
    @BindView(R.id.ll_finger)
    LinearLayout mLlFinger;
    @BindView(R.id.tv_other)
    TextView mTvOther;
    Unbinder unbinder;
    private FingerprintDialog mFingerprintDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startFingerprint();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_fingerprint_verification_layout, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        FingerprintUtil.registerCallBackListener(null);
        mOnCallBackListener = null;
    }

    @OnClick({R.id.ll_finger, R.id.tv_other})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_finger:
                startFingerprint();
                break;
            case R.id.tv_other:
                getFragmentManager().beginTransaction().replace(R.id.fl_content, new InputNumFragment()).commit();
                break;
        }
    }


    private void startFingerprint() {
        FingerprintUtil.registerCallBackListener(mOnCallBackListener);
        FingerprintUtil.callFingerPrint();
    }

    @Override
    public void onPause() {
        super.onPause();
        FingerprintUtil.cancel();
    }

    FingerprintUtil.OnCallBackListener mOnCallBackListener = new FingerprintUtil.OnCallBackListener() {
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
        public void onAuthenticationStart() {
            mFingerprintDialog = new FingerprintDialog(getContext());
            mFingerprintDialog.show();
            Window dialogWindow = mFingerprintDialog.getWindow();
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.height = getResources().getDimensionPixelSize(R.dimen.y450);
            p.width = getResources().getDimensionPixelSize(R.dimen.x700);
            dialogWindow.setAttributes(p);
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Toast.makeText(getContext(), errString, Toast.LENGTH_SHORT).show();
            if (mFingerprintDialog != null && mFingerprintDialog.isShowing()) {
                mFingerprintDialog.dismiss();
            }
            getFragmentManager().beginTransaction().replace(R.id.fl_content, new InputNumFragment()).commitAllowingStateLoss();
        }

        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(getContext(), R.string.unlock_the_failure, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(getContext(), helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            if (mFingerprintDialog != null && mFingerprintDialog.isShowing()) {
                mFingerprintDialog.dismiss();
            }
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }

        @Override
        public void onVersionFailed() {
            Toast.makeText(getContext(), R.string.error_version_less_6, Toast.LENGTH_SHORT).show();
        }
    };
}
