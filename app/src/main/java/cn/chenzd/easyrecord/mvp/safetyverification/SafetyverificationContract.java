package cn.chenzd.easyrecord.mvp.safetyverification;

import android.media.Image;

import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.NumberPassword;


/**
 * Created by chenzaidong on 2017/9/11.
 */

public interface SafetyverificationContract {
    interface Presenter extends BasePresenter {
        void checkNumberPassword(NumberPassword numberPassword);
        void saveImage(Image image, String name);
    }
    interface View extends BaseView<Presenter> {
        void onCheckNumberPassword(boolean pass);
    }
}
