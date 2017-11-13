package cn.chenzd.easyrecord.mvp.initpassword;


import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.NumberPassword;

/**
 * Created by chenzaidong on 2017/9/11.
 */

public interface initPasswordContract {
    interface View extends BaseView<Presenter> {
        void onSavePasswordSuccess();
        void onSavePasswordFailed(String msg);
    }

    interface Presenter extends BasePresenter {
        void savePassword(NumberPassword numberPassword);
    }
}
