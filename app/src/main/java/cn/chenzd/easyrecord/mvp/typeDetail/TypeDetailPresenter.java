package cn.chenzd.easyrecord.mvp.typeDetail;

import java.util.List;

import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.mvp.data.AccountData;
import cn.chenzd.easyrecord.mvp.data.ImagesData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 账户类型详情presenter实现类
 * Created by chenzaidong on 2017/8/31.
 */

public class TypeDetailPresenter implements TypeDetailConstract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private TypeDetailConstract.View mView;
    private AccountData mAccountData;
    private ImagesData mImagesData;

    public TypeDetailPresenter(TypeDetailConstract.View view) {
        mView = checkNotNull(view);
        mView.setPresenter(this);
        mAccountData = new AccountData();
        mImagesData = new ImagesData();
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
     * 获取指定账户类型下的所有账户
     * @param typeId 账户类型ID
     */
    @Override
    public void getAccountByTypeId(Long typeId) {
        mCompositeDisposable.add(mAccountData.getAccountsByTypeId(typeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Account>>() {
                    @Override
                    public void accept(List<Account> accounts) throws Exception {
                        mView.onGetAccountByTypeId(accounts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onGetAccountByTypeIdFailed(throwable.getMessage());
                    }
                }));
    }

    /**
     * 删除账号
     * @param account
     */
    @Override
    public void deleteAccount(final Account account) {
        mCompositeDisposable.add(mImagesData.deleteImages(account.getImages())
                .subscribe());
        mCompositeDisposable.add(mAccountData.deleteAccount(account)
                .subscribe());
    }
}
