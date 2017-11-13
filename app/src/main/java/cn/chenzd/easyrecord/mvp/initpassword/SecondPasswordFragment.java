package cn.chenzd.easyrecord.mvp.initpassword;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.entity.NumberPassword;

import static cn.chenzd.easyrecord.constant.Constant.VIBRATE_TIME;
import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 第二次密码输入界面
 * Created by chenzaidong on 2017/9/11.
 */

public class SecondPasswordFragment extends Fragment implements initPasswordContract.View {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_psw_1)
    ImageView ivPsw1;
    @BindView(R.id.iv_psw_2)
    ImageView ivPsw2;
    @BindView(R.id.iv_psw_3)
    ImageView ivPsw3;
    @BindView(R.id.iv_psw_4)
    ImageView ivPsw4;
    @BindView(R.id.tv_num_1)
    TextView tvNum1;
    @BindView(R.id.tv_num_2)
    TextView tvNum2;
    @BindView(R.id.tv_num_3)
    TextView tvNum3;
    @BindView(R.id.tv_num_4)
    TextView tvNum4;
    @BindView(R.id.tv_num_5)
    TextView tvNum5;
    @BindView(R.id.tv_num_6)
    TextView tvNum6;
    @BindView(R.id.tv_num_7)
    TextView tvNum7;
    @BindView(R.id.tv_num_8)
    TextView tvNum8;
    @BindView(R.id.tv_num_9)
    TextView tvNum9;
    @BindView(R.id.tv_num_0)
    TextView tvNum0;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    Unbinder unbinder;
    @BindView(R.id.ll_icon)
    LinearLayout llIcon;
    @BindView(R.id.gl_psw)
    GridLayout glPsw;
    private ArrayList<String> mFirstPassword;
    private ArrayList<String> mPasswords = new ArrayList<>();
    private ImageView[] mImageViews = null;
    private initPasswordContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new InitNumPasswordPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_second_password, null);
        unbinder = ButterKnife.bind(this, view);
        mFirstPassword = getArguments().getStringArrayList("password");
        mImageViews = new ImageView[]{ivPsw1, ivPsw2, ivPsw3, ivPsw4};

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_num_1, R.id.tv_num_2, R.id.tv_num_3, R.id.tv_num_4, R.id.tv_num_5, R.id.tv_num_6, R.id.tv_num_7, R.id.tv_num_8, R.id.tv_num_9, R.id.tv_num_0, R.id.tv_delete, R.id.tv_cancel})
    public void onClick(View view) {
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
            case R.id.tv_delete:
                if (mPasswords.size() > 0) {
                    mPasswords.remove(mPasswords.size() - 1);
                    setIvNum(mPasswords.size());
                }
                break;
            case R.id.tv_cancel:
                getFragmentManager().beginTransaction().replace(R.id.fl_content, new FirstPasswordFragment()).commit();
                break;
        }
    }

    private void setNum(String num) {
        mPasswords.add(num);
        setIvNum(mPasswords.size());
        if (mPasswords.size() == 4) {
            for (int i = 0; i < mPasswords.size(); i++) {
                if (!mPasswords.get(i).equals(mFirstPassword.get(i))) {
                    tvTitle.setText(R.string.error_second_password);
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
                    return;
                }
            }
            tvTitle.setText(R.string.thanks_for_using);
            savePassword();
        }
    }

    /**
     * 保存密码
     */
    private void savePassword() {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < mPasswords.size(); i++) {
            stringBuffer.append(mPasswords.get(i));
        }
        NumberPassword numberPassword = new NumberPassword();
        numberPassword.setPassword(stringBuffer.toString());
        mPresenter.savePassword(numberPassword);
    }

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
    public void setPresenter(initPasswordContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onSavePasswordSuccess() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }


    @Override
    public void onSavePasswordFailed(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }
}
