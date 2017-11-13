package cn.chenzd.easyrecord;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.entity.NumberPassword;
import cn.chenzd.easyrecord.mvp.home.HomeActivity;
import cn.chenzd.easyrecord.mvp.initpassword.InitNumPasswordActivity;
import cn.chenzd.easyrecord.mvp.safetyverification.SafetyVerificationActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static cn.chenzd.easyrecord.constant.Constant.REQUEST_CHECK_NUMBER_PASSWORD;
import static cn.chenzd.easyrecord.constant.Constant.REQUEST_INIT_NUMBER_PASSWORD;
import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_IS_FIRST;
import static cn.chenzd.easyrecord.constant.Constant.SP_NAME;
import static cn.chenzd.easyrecord.utils.Utils.isOPenOfGPS;


/**
 * Created by chenzaidong on 2017/9/11.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isFrist = getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(SP_KEY_IS_FIRST, true);
        if (isFrist) {
            setContentView(R.layout.activity_authorization_layout);
            ButterKnife.bind(this);
        } else {
            initPassword();
        }
    }

    /**
     * 判断是否有密码
     */
    private void initPassword() {
        Observable.create(new ObservableOnSubscribe<List<NumberPassword>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NumberPassword>> e) throws Exception {
                e.onNext(EasyRecordApplication.getDaoSession().getNumberPasswordDao().queryBuilder().list());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NumberPassword>>() {
                    @Override
                    public void accept(List<NumberPassword> numberPasswords) throws Exception {
                        if (numberPasswords == null || numberPasswords.size() < 1) {
                            startActivityForResult(new Intent(MainActivity.this, InitNumPasswordActivity.class), REQUEST_INIT_NUMBER_PASSWORD);
                        } else {
                            startActivityForResult(new Intent(MainActivity.this, SafetyVerificationActivity.class), REQUEST_CHECK_NUMBER_PASSWORD);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_INIT_NUMBER_PASSWORD || requestCode == REQUEST_CHECK_NUMBER_PASSWORD) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        } else {
            finish();
        }
    }

    @OnClick(R.id.btn_authorization)
    public void onViewClicked() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (!isOPenOfGPS()) {
                                Toast.makeText(MainActivity.this, R.string.gps_not_open, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.cancel_authorization, Toast.LENGTH_SHORT).show();
                        }
                        getSharedPreferences(SP_NAME, MODE_PRIVATE).edit().putBoolean(SP_KEY_IS_FIRST, false).apply();
                        initPassword();
                    }
                });
    }
}
