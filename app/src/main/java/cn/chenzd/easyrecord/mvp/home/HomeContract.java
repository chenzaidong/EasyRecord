package cn.chenzd.easyrecord.mvp.home;

import java.util.List;

import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.Type;


/**
 * Created by chenzaidong on 2017/8/25.
 */

public interface HomeContract {
    interface View extends BaseView<Presenter> {
       void onGetTypes(List<Type> types);

    }
    interface Presenter extends BasePresenter {
        void getTypes();
    }
}
