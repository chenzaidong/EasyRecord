package cn.chenzd.easyrecord.mvp.data;

import java.io.File;
import java.util.List;

import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.db.InvasionRecordDao;
import cn.chenzd.easyrecord.entity.InvasionRecord;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 入侵记录数据操作类
 * Created by chenzaidong on 2017/9/12.
 */

public class InvasionRecordData {
    private InvasionRecordDao mInvasionRecordDao;

    public InvasionRecordData() {
        mInvasionRecordDao = EasyRecordApplication.getDaoSession().getInvasionRecordDao();
    }

    /**
     * 获取所有入侵记录
     * @return
     */
    public Observable<List<InvasionRecord>> getAllInvasionRecord() {
        return Observable.create(new ObservableOnSubscribe<List<InvasionRecord>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<InvasionRecord>> e) throws Exception {
                e.onNext(mInvasionRecordDao.queryBuilder().list());
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 添加入侵记录
     * @param invasionRecord
     */
    public Observable<Boolean> addInvasionRecord(final InvasionRecord invasionRecord) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mInvasionRecordDao.insertOrReplaceInTx(invasionRecord);
            }
        }).subscribeOn(Schedulers.io());

    }

    /**
     * 删除入侵记录
     * @param invasionRecord 要删除的对象
     */
    public Observable<Boolean> deleteInvasionRecord(final InvasionRecord invasionRecord) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mInvasionRecordDao.delete(invasionRecord);
                    File file = new File(invasionRecord.getImagePath());
                    if (file.exists()) {
                        file.delete();
                    }
                    e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }
}
