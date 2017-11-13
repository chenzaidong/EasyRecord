package cn.chenzd.easyrecord.mvp.addAccount;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.data.AccountData;
import cn.chenzd.easyrecord.mvp.data.ImagesData;
import cn.chenzd.easyrecord.mvp.data.TypeData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 添加账户 presenter实现类
 * Created by chenzaidong on 2017/8/28.
 */

public class AddAccountPresenter implements AddAccountConstract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private ImagesData mImagesData;
    private AddAccountConstract.View mView;
    private AccountData mAccountData;
    private TypeData mTypeData;

    public AddAccountPresenter(AddAccountConstract.View view) {
        mView = checkNotNull(view);
        mView.setPresenter(this);
        mAccountData = new AccountData();
        mImagesData = new ImagesData();
        mTypeData = new TypeData();
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
     * 添加账户
     * @param account 添加的账户对象
     * @param images 账户对应的图片
     */
    @Override
    public void addAccount(final Account account, final List<Images> images) {
        mCompositeDisposable.add(mAccountData.addAccount(account)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean b) throws Exception {
                        Long id = account.getId();
                        for (int i = 0; i < images.size(); i++) {
                            images.get(i).setAccountId(id);
                        }
                        addImages(id,images);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable,throwable.getMessage());
                        mView.onAddAccountFailed(throwable.getMessage());
                    }
                }));
    }

    /**
     * 添加图片
     * @param typeId
     * @param images
     */
    @Override
    public void addImages(Long typeId, List<Images> images) {
        mCompositeDisposable.add(mImagesData.addImages(typeId,images)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean a) throws Exception {
                        mView.onAddImagesSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onAddImagesFailed(throwable.getMessage());
                    }
                }));
    }

    /**
     * 获取所有类型
     */
    @Override
    public void getTypes() {
        mCompositeDisposable.add(mTypeData.getTypes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Type>>() {
                    @Override
                    public void accept(List<Type> types) throws Exception {
                        mView.onGetTypes(types);
                    }
                }));
    }
}
