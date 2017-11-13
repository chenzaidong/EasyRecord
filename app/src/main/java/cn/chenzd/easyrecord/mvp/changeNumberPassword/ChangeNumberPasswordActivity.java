package cn.chenzd.easyrecord.mvp.changeNumberPassword;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;


/**
 * 修改数字密码activity
 * Created by chenzaidong on 2017/9/11.
 */

public class ChangeNumberPasswordActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_num_password);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, new InputOldNumPasswordFragment()).commit();
    }
}
