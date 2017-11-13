package cn.chenzd.easyrecord.mvp.initpassword;


import cn.chenzd.easyrecord.entity.NumberPassword;
import cn.chenzd.easyrecord.mvp.data.NumberPasswordData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 初始化数字密码presenter
 * Created by chenzaidong on 2017/9/11.
 */

public class InitNumPasswordPresenter implements initPasswordContract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private initPasswordContract.View mView;
    private NumberPasswordData mNumberPasswordData;

    public InitNumPasswordPresenter(initPasswordContract.View view) {
        mCompositeDisposable = new CompositeDisposable();
        mView = checkNotNull(view);
        mView.setPresenter(this);
        mNumberPasswordData =new NumberPasswordData();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    @Override
    public void savePassword(NumberPassword numberPassword) {
        mCompositeDisposable.add(mNumberPasswordData.addNumberPasword(numberPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aVoid) throws Exception {
                        mView.onSavePasswordSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onSavePasswordFailed(throwable.getMessage());
                    }
                }));
    }
}
