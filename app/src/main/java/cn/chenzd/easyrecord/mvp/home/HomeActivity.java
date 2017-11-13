package cn.chenzd.easyrecord.mvp.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.about.AboutActivity;
import cn.chenzd.easyrecord.mvp.addAccount.AddAccountActivity;
import cn.chenzd.easyrecord.mvp.changeNumberPassword.ChangeNumberPasswordActivity;
import cn.chenzd.easyrecord.mvp.editType.EditTypeActivity;
import cn.chenzd.easyrecord.mvp.invasion.InvasionActivity;
import cn.chenzd.easyrecord.mvp.search.SearchActivity;
import cn.chenzd.easyrecord.utils.ActivityUtils;
import cn.chenzd.easyrecord.utils.FingerprintUtil;
import cn.chenzd.easyrecord.utils.GlideCacheUtil;

import static cn.chenzd.easyrecord.constant.Constant.SP_KEY_FINGER_PRINT;
import static cn.chenzd.easyrecord.constant.Constant.SP_NAME;


public class HomeActivity extends BaseActivity {
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_cache)
    TextView mTvCache;
    @BindView(R.id.sc_finger)
    SwitchCompat scFinger;
    @BindView(R.id.iv_search)
    ImageView mSearchView;
    private Type mSelectType;
    private DrawerLayout drawer;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.dl_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        HomeFragment homeFragment = new HomeFragment();
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), homeFragment, R.id.fl_content);
        new HomePresenter(homeFragment);
        mSharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        scFinger.setChecked(mSharedPreferences.getBoolean(SP_KEY_FINGER_PRINT, false));
    }

    @OnClick({R.id.rl_title, R.id.fab, R.id.ll_change_number_password, R.id.sc_finger, R.id.ll_invasion_record, R.id.ll_cache, R.id.ll_about,R.id.iv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title:
                startActivity(new Intent(this, EditTypeActivity.class));
                break;
            case R.id.fab:
                Intent intent = new Intent(this, AddAccountActivity.class);
                intent.putExtra("type", mSelectType);
                startActivity(intent);
                break;
            case R.id.ll_change_number_password:
                startActivity(new Intent(this, ChangeNumberPasswordActivity.class));
                break;
            case R.id.sc_finger:
                setFingerprint();
                break;
            case R.id.ll_invasion_record:
                startActivity(new Intent(this, InvasionActivity.class));
                break;
            case R.id.ll_cache:
                mTvCache.setText(R.string.cleaning_cache);
                GlideCacheUtil.getInstance().clearImageAllCache(this);
                mTvCache.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvCache.setText(GlideCacheUtil.getInstance().getCacheSize(HomeActivity.this));
                    }
                }, 1000);
                break;
            case R.id.ll_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.iv_search:
                startActivity(new Intent(this, SearchActivity.class), ActivityOptionsCompat.makeSceneTransitionAnimation(this,mSearchView,"search").toBundle());
                break;
            default:
                break;
        }
    }

    /**
     * 开启指纹解锁
     */
    private void setFingerprint() {

        boolean finger = mSharedPreferences.getBoolean(SP_KEY_FINGER_PRINT, false);
        if (!finger) {
            FingerprintUtil.registerSupportCallbackListener(mOnSupportCallbackListener);
            FingerprintUtil.supportFingerprint();
        } else {
            mSharedPreferences.edit().putBoolean(SP_KEY_FINGER_PRINT, false).apply();
        }
    }

    private FingerprintUtil.OnSupportCallbackListener mOnSupportCallbackListener = new FingerprintUtil.OnSupportCallbackListener() {
        @Override
        public void onSupport() {
            scFinger.setChecked(true);
            mSharedPreferences.edit().putBoolean(SP_KEY_FINGER_PRINT, true).apply();
        }

        @Override
        public void onSupportFailed() {
            scFinger.setChecked(false);
            Toast.makeText(HomeActivity.this, R.string.error_hardware_not_detected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInsecurity() {
            scFinger.setChecked(false);
            Toast.makeText(HomeActivity.this, R.string.error_keyguard_not_secured, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnrollFailed() {
            scFinger.setChecked(false);
            Toast.makeText(HomeActivity.this, R.string.error_has_not_enrolled_fingerprints, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVersionFailed() {
            scFinger.setChecked(false);
            Toast.makeText(HomeActivity.this, R.string.error_version_less_6, Toast.LENGTH_SHORT).show();
        }
    };

    public void onSelectType(Type type) {
        mSelectType = type;
    }

    /**
     * 浮动按钮 显示或者隐藏
     *
     * @param status true显示 false隐藏
     */
    public void onShowOrHide(boolean status) {
        if (status) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)) {
            drawer.closeDrawer(Gravity.START, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTvCache.setText(GlideCacheUtil.getInstance().getCacheSize(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOnSupportCallbackListener = null;
        FingerprintUtil.registerSupportCallbackListener(null);
    }
}
