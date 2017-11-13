package cn.chenzd.easyrecord.mvp.data;

import java.util.List;

import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.db.NumberPasswordDao;
import cn.chenzd.easyrecord.entity.NumberPassword;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 数字密码操作类
 * Created by chenzaidong on 2017/9/11.
 */

public class NumberPasswordData {
    private NumberPasswordDao mNumberPasswordDao;

    public NumberPasswordData() {
        mNumberPasswordDao = EasyRecordApplication.getDaoSession().getNumberPasswordDao();
    }

    /**
     * 添加数字密码对象
     * 只能为一 所以先删除 然后再添加
     *
     * @param numberPassword 添加的对象
     */
    public Observable<Boolean> addNumberPasword(final NumberPassword numberPassword) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mNumberPasswordDao.deleteAll();
                mNumberPasswordDao.insertOrReplaceInTx(numberPassword);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 查询数字密码
     */
    public Observable<List<NumberPassword>> queryNumberPasword() {
        return Observable.create(new ObservableOnSubscribe<List<NumberPassword>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NumberPassword>> e) throws Exception {
                e.onNext(mNumberPasswordDao.queryBuilder().list());
            }
        });
    }
}
