package cn.chenzd.easyrecord.mvp.editType;


import java.util.ArrayList;
import java.util.List;

import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.data.AccountData;
import cn.chenzd.easyrecord.mvp.data.ImagesData;
import cn.chenzd.easyrecord.mvp.data.TypeData;
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
 * 编辑账户类型presenter实现类
 * Created by chenzaidong on 2017/8/26.
 */

public class EditTypePresenter implements EditConstract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private EditConstract.View mView;
    private TypeData mTypeData;
    private ImagesData mImagesData;
    private AccountData mAccountData;

    public EditTypePresenter(EditConstract.View view) {
        mView = checkNotNull(view);
        mView.setPresenter(this);
        mTypeData = new TypeData();
        mImagesData = new ImagesData();
        mAccountData = new AccountData();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    /**
     * 获取账户类型
     */
    @Override
    public void getTypes() {
        mCompositeDisposable.add(mTypeData.getTypes().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Type>>() {
                    @Override
                    public void accept(List<Type> types) throws Exception {
                        mView.onGetTypes(types);
                    }
                }));
    }

    /**
     * 保存账户类型
     *
     * @param types
     */
    @Override
    public void saveTypes(List<Type> types) {
        mCompositeDisposable.add(mTypeData.saveTypes(types).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aVoid) throws Exception {
                        mView.onSaveTypesSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onSaveTypesFailed(throwable.getMessage());
                    }
                }));
    }

    /**
     * 获取图标
     */
    @Override
    public void getIcons() {
        mCompositeDisposable.add(Observable.create(new ObservableOnSubscribe<ArrayList<Integer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Integer>> e) throws Exception {
                int[] iconResource = EasyRecordApplication.getIconResource();
                ArrayList<Integer> datas = new ArrayList<>();
                for (int i = 0; i < iconResource.length; i++) {
                    datas.add(iconResource[i]);
                }
                e.onNext(datas);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Integer>>() {
                    @Override
                    public void accept(ArrayList<Integer> o) throws Exception {
                        mView.onGetIcons(o);
                    }
                }));
    }

    /**
     * 删除账户类型
     *
     * @param types 删除的对象
     */
    @Override
    public void deleteTypes(List<Type> types) {
        mCompositeDisposable.add(mTypeData.deleteTypes(types).subscribe());
    }

    /**
     * 删除图片
     *
     * @param images
     */
    @Override
    public void deleteImages(List<Images> images) {
        mCompositeDisposable.add(mImagesData.deleteImages(images).subscribe());
    }

    /**
     * 删除账号
     * @param accounts
     */
    @Override
    public void deleteAccounts(List<Account> accounts) {
        mCompositeDisposable.add(mAccountData.deleteAccounts(accounts).subscribe());
    }
}
