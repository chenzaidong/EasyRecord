package cn.chenzd.easyrecord.mvp.initpassword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.ActivityCollector;


/**
 * 初始化数字密码activity
 * Created by chenzaidong on 2017/9/11.
 */

public class InitNumPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_init_num_password);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_content, new FirstPasswordFragment()).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
