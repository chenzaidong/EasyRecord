package cn.chenzd.easyrecord.mvp.data;

import java.util.List;

import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.db.ImagesDao;
import cn.chenzd.easyrecord.entity.Images;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 图片数据操作类
 * Created by chenzaidong on 2017/8/28.
 */

public class ImagesData {
    ImagesDao mImagesDao = EasyRecordApplication.getDaoSession().getImagesDao();

    /**
     * 添加图片到数据库
     * 先删除账户ID下的所有图片 然后将图片添加
     *
     * @param typeId 账户ID
     * @param images 图片对象
     */
    public Observable<Boolean> addImages(final Long typeId, final List<Images> images) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mImagesDao.queryBuilder().where(ImagesDao.Properties.AccountId.eq(typeId)).buildDelete().executeDeleteWithoutDetachingEntities();
                mImagesDao.insertOrReplaceInTx(images);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 删除指定图片对象
     *
     * @param images 要删除的图片对象
     */
    public Observable<Boolean> deleteImages(final List<Images> images) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mImagesDao.deleteInTx(images);
            }
        }).subscribeOn(Schedulers.io());
    }
}
