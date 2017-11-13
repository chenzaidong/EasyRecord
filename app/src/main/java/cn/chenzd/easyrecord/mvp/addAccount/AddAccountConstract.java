package cn.chenzd.easyrecord.mvp.addAccount;

import java.util.List;

import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.entity.Type;


/**
 * 添加账户
 * Created by chenzaidong on 2017/8/28.
 */

public interface AddAccountConstract {
    interface Presenter extends BasePresenter {
        void addAccount(Account account, List<Images> images);
        void addImages(Long typeId, List<Images> images);
        void getTypes();
    }
    interface View extends BaseView<Presenter> {
        void onAddAccountFailed(String msg);
        void onAddImagesSuccess();
        void onAddImagesFailed(String msg);
        void onGetTypes(List<Type> types);
    }
}
