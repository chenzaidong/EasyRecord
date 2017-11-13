package cn.chenzd.easyrecord.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.chenzd.easyrecord.mvp.safetyverification.SafetyVerificationActivity;


/**
 * BaseActivity 用于判断当前activity是否处于后台
 * 然后恢复到前台， 如果在后台时间草果5秒 则弹出密码框
 * Created by chenzaidong on 2017/9/10.
 */

public class BaseActivity extends AppCompatActivity {
    protected boolean isActive = true;
    private long inBackgroundTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            isActive = false;
            inBackgroundTime = System.currentTimeMillis();
            Logger.i("进入后台");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActive) {
            //从后台唤醒
            Logger.i("从后台唤醒");
            long currentTime = System.currentTimeMillis();
            if (currentTime - inBackgroundTime >= 5000) {
                isActive = true;
                Intent intent = new Intent(this, SafetyVerificationActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * 是否在前台
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        String curPackageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> app = am.getRunningAppProcesses();
        if (app == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo a : app) {
            if (a.processName.equals(curPackageName) &&
                    a.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
