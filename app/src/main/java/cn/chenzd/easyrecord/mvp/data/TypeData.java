package cn.chenzd.easyrecord.mvp.data;

import java.util.List;

import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.db.TypeDao;
import cn.chenzd.easyrecord.entity.Type;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 账户类型操作类
 * Created by chenzaidong on 2017/8/26.
 */

public class TypeData {

    public Observable<List<Type>> getTypes() {
        return Observable.create(new ObservableOnSubscribe<List<Type>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Type>> e) throws Exception {
                TypeDao typeDao = EasyRecordApplication.getDaoSession().getTypeDao();
                e.onNext(typeDao.queryBuilder().orderAsc(TypeDao.Properties.Order).build().list());
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 保存账户类型
     * @param types 保存的对象
     * @return
     */
    public Observable<Boolean> saveTypes(final List<Type> types) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                TypeDao typeDao = EasyRecordApplication.getDaoSession().getTypeDao();
                typeDao.insertOrReplaceInTx(types);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     * 删除数据
     * @param types
     * @return
     */
    public Observable<Void> deleteTypes(final List<Type> types){
        return Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Void> e) throws Exception {
                TypeDao typeDao = EasyRecordApplication.getDaoSession().getTypeDao();
                typeDao.deleteInTx(types);
            }
        }).subscribeOn(Schedulers.io());
    }

}
