package cn.chenzd.easyrecord.mvp.invasion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;
import cn.chenzd.easyrecord.entity.InvasionRecord;
import cn.chenzd.easyrecord.utils.Utils;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.constant.Constant.BAIDU_AK;
import static cn.chenzd.easyrecord.constant.Constant.BAIDU_MAP_BASE;
import static cn.chenzd.easyrecord.utils.Utils.isOPenOfGPS;


/**
 * 入侵记录
 * Created by chenzaidong on 2017/9/12.
 */

public class InvasionActivity extends BaseActivity implements InvasionConstract.View, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.tv_camera)
    TextView tvCamera;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_settings)
    TextView tvSettings;
    @BindView(R.id.tv_gps)
    TextView tvGPS;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    private InvasionConstract.Presenter mPresenter;
    private QuickAdapter mQuickAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invasion_record_layout);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new InvasionPresenter(this);
        mPresenter.getAllInvasionRecord();
        mQuickAdapter = new QuickAdapter();
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mQuickAdapter.bindToRecyclerView(rvList);
        mQuickAdapter.setOnItemChildClickListener(this);
        mQuickAdapter.setEmptyView(R.layout.view_empty_layout);
        initListener();
    }

    private void initListener() {
        RxPermissions rxPermissions = new RxPermissions(this);
        RxView.clicks(tvCamera)
                .compose(rxPermissions.ensure(Manifest.permission.CAMERA))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        setAuthorized(tvCamera, aBoolean);
                    }
                });
        RxView.clicks(tvLocation)
                .compose(rxPermissions.ensure(Manifest.permission.ACCESS_FINE_LOCATION))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        setAuthorized(tvLocation, aBoolean);
                    }
                });
        RxView.clicks(tvGPS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthorization();
    }

    /**
     * 检查权限和功能
     */
    private void checkAuthorization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                setAuthorized(tvCamera, false);
            } else {
                setAuthorized(tvCamera, true);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                setAuthorized(tvLocation, false);
            } else {
                setAuthorized(tvLocation, true);
            }

        }

        if (isOPenOfGPS()) {
            tvGPS.setText(R.string.opened);
            tvGPS.setBackgroundResource(R.drawable.authorized_bg);
            tvGPS.setTextColor(ContextCompat.getColor(this, R.color.authorized));
        } else {
            tvGPS.setText(R.string.closed);
            tvGPS.setBackgroundResource(R.drawable.unauthorized_bg);
            tvGPS.setTextColor(ContextCompat.getColor(this, R.color.unauthorized));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isActive = true;
        Logger.i("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setAuthorized(TextView textView, boolean authorized) {
        if (authorized) {
            textView.setText(R.string.authorized);
            textView.setTextColor(ContextCompat.getColor(this, R.color.authorized));
            textView.setBackgroundResource(R.drawable.authorized_bg);
        } else {
            textView.setText(R.string.unauthorized);
            textView.setTextColor(ContextCompat.getColor(this, R.color.unauthorized));
            textView.setBackgroundResource(R.drawable.unauthorized_bg);
        }
    }

    @Override
    public void setPresenter(InvasionConstract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onGetAllInvasionRecord(List<InvasionRecord> invasionRecords) {
        mQuickAdapter.setNewData(invasionRecords);
    }

    @Override
    public void onDeleteSuccess() {
        Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.tv_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_settings:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_address:
                InvasionRecord invasionRecord = (InvasionRecord) adapter.getItem(position);
                ArrayList arrayList = Utils.getLocation(invasionRecord.getAddress());
                if (arrayList.size() < 2) {
                    Toast.makeText(this, R.string.error_location_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = BAIDU_MAP_BASE+"ak=%s&center=%s,%s&width=800&height=600&dpiType=ph&zoom=15&markers=%s,%s&markerStyles=-1,http://osgun3ytd.bkt.clouddn.com/location.png";
                String urlPath = String.format(url, BAIDU_AK, arrayList.get(1), arrayList.get(0), arrayList.get(1), arrayList.get(0));
                Logger.i(urlPath);
                Intent i = new Intent();
                i.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(urlPath);
                i.setData(content_url);
                startActivityForResult(i, 1000);
                break;
            case R.id.iv_image:
                Intent intent = new Intent(this, InvasionImageActivity.class);
                String path = ((InvasionRecord) adapter.getItem(position)).getImagePath();
                view.setTransitionName(path);
                intent.putExtra("path", path);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, path);
                ActivityCompat.startActivity(this, intent, options.toBundle());
                break;
            case R.id.right_menu_delete:
                mPresenter.deleteInvasionRecord(mQuickAdapter.getItem(position));
                mQuickAdapter.remove(position);
                break;
        }
    }


    private class QuickAdapter extends BaseQuickAdapter<InvasionRecord, BaseViewHolder> {
        QuickAdapter() {
            super(R.layout.item_invasion_record_layout, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, InvasionRecord item) {
            helper.setText(R.id.tv_address, item.getAddress())
                    .setText(R.id.tv_date, item.getDate())
                    .addOnClickListener(R.id.right_menu_delete)
                    .addOnClickListener(R.id.iv_image)
                    .addOnClickListener(R.id.tv_address);
            ImageView imageView = helper.getView(R.id.iv_image);
            TextView tvAddress = helper.getView(R.id.tv_address);
            ArrayList<String> location = Utils.getLocation(item.getAddress());
            if (location.size()==2){
                tvAddress.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
                tvAddress.getPaint().setAntiAlias(true);//抗锯齿
                tvAddress.setTextColor(Color.BLUE);
            }

            Glide.with(InvasionActivity.this).load(item.getImagePath()).error(R.drawable.error).into(imageView);
        }
    }
}
