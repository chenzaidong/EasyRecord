package cn.chenzd.easyrecord.mvp.safetyverification;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.support.v4.app.ActivityCompat;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.entity.InvasionRecord;
import cn.chenzd.easyrecord.entity.NumberPassword;
import cn.chenzd.easyrecord.mvp.data.InvasionRecordData;
import cn.chenzd.easyrecord.mvp.data.NumberPasswordData;
import cn.chenzd.easyrecord.utils.Utils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 安全检查presenter实现类
 * Created by chenzaidong on 2017/9/11.
 */

public class SafetyverificationPresenter implements SafetyverificationContract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private SafetyverificationContract.View mView;
    private NumberPasswordData mNumberPasswordData;
    private InvasionRecordData mInvasionRecordData;
    private Location mLocation = null;

    public SafetyverificationPresenter(SafetyverificationContract.View view) {
        mCompositeDisposable = new CompositeDisposable();
        mView = checkNotNull(view);
        mView.setPresenter(this);
        mNumberPasswordData = new NumberPasswordData();
        mInvasionRecordData = new InvasionRecordData();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    /**
     * 检查输入的密码是否正确
     * @param numberPassword
     */
    @Override
    public void checkNumberPassword(final NumberPassword numberPassword) {
        mCompositeDisposable.add(mNumberPasswordData.queryNumberPasword().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NumberPassword>>() {
                    @Override
                    public void accept(List<NumberPassword> numberPasswords) throws Exception {
                        if (numberPasswords != null && numberPasswords.size() > 0) {
                            mView.onCheckNumberPassword(numberPassword.getPassword().equals(numberPasswords.get(0).getPassword()));
                        }
                    }
                }));
    }

    /**
     * 保存图片
     * @param image 保存的对象
     * @param name 图片名称
     */
    @Override
    public void saveImage(final Image image, final String name) {
        mCompositeDisposable.add(Observable.create(new ObservableOnSubscribe<InvasionRecord>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<InvasionRecord> e) throws Exception {
                InvasionRecord invasionRecord = new InvasionRecord();
                invasionRecord.setDate(Utils.getCurrentDate());
                invasionRecord.setImagePath(EasyRecordApplication.getContext().getFilesDir().getAbsolutePath() + File.separator + name);
                invasionRecord.setAddress(getLocation());
                Logger.i(invasionRecord.toString());
                e.onNext(invasionRecord);
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<InvasionRecord>() {
                    @Override
                    public void accept(InvasionRecord invasionRecord) throws Exception {
                        mCompositeDisposable.add(mInvasionRecordData.addInvasionRecord(invasionRecord).subscribe());
                        Utils.savaImageToFile(name, image);
                        Logger.i("save invasionRecord="+invasionRecord);
                    }
                }));
    }

    /**
     * 获取定位信息
     */
    public String getLocation() {
        LocationManager locationManager = (LocationManager) EasyRecordApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        Context context = EasyRecordApplication.getContext();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Utils.isOPenOfGPS()) {
                mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (mLocation != null) {
                    return context.getString(R.string.location_longitude_latitude, mLocation.getLatitude(), mLocation.getLongitude());
                } else {
                    return context.getString(R.string.error_location_null);
                }
            } else {
                return context.getString(R.string.error_location_not_open);
            }
        }
        return context.getString(R.string.error_location_unauthorized);
    }

}
