package cn.chenzd.easyrecord.mvp.initpassword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.chenzd.easyrecord.R;


/**
 * 初始化数字密码视图
 * Created by chenzaidong on 2017/9/11.
 */

public class FirstPasswordFragment extends Fragment {
    @BindView(R.id.iv_psw_1)
    ImageView ivPsw1;
    @BindView(R.id.iv_psw_2)
    ImageView ivPsw2;
    @BindView(R.id.iv_psw_3)
    ImageView ivPsw3;
    @BindView(R.id.iv_psw_4)
    ImageView ivPsw4;
    Unbinder unbinder;
    private ArrayList<String> mPasswords = new ArrayList<>();
    private ImageView[] mImageViews = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_first_password, null);
        unbinder = ButterKnife.bind(this, view);
        mImageViews = new ImageView[]{ivPsw1, ivPsw2, ivPsw3, ivPsw4};
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_num_1, R.id.tv_num_2, R.id.tv_num_3, R.id.tv_num_4, R.id.tv_num_5, R.id.tv_num_6, R.id.tv_num_7, R.id.tv_num_8, R.id.tv_num_9, R.id.tv_num_0, R.id.tv_delete})
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
        }
    }

    private void setNum(String num) {
        mPasswords.add(num);
        setIvNum(mPasswords.size());
        if (mPasswords.size() == 4) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("password", mPasswords);
            SecondPasswordFragment secondPasswordFragment = new SecondPasswordFragment();
            secondPasswordFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fl_content, secondPasswordFragment).commit();
        }
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
}
