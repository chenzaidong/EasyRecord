package cn.chenzd.easyrecord.mvp.home;

import java.util.List;

import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.data.TypeData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 主页presetner层
 * Created by chenzaidong on 2017/8/26.
 */

public class HomePresenter implements HomeContract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private HomeContract.View mView;
    private TypeData mTypeData;

    public HomePresenter(HomeContract.View view) {
        mView = checkNotNull(view);
        mTypeData = new TypeData();
        mCompositeDisposable = new CompositeDisposable();
        mView.setPresenter(this);
    }



    @Override
    public void subscribe() {
        getTypes();
    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

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
