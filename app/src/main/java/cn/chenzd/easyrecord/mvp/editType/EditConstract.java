package cn.chenzd.easyrecord.mvp.editType;

import java.util.List;

import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.entity.Type;

/**
 * Created by chenzaidong on 2017/8/26.
 */

public interface EditConstract {
    interface View extends BaseView<Presenter> {
        void onGetTypes(List<Type> types);

        void onSaveTypesSuccess();

        void onSaveTypesFailed(String msg);

        void onGetIcons(List<Integer> icons);
    }

    interface Presenter extends BasePresenter {
        void getTypes();

        void saveTypes(List<Type> types);

        void getIcons();

        void deleteTypes(List<Type> types);

        void deleteImages(List<Images> images);

        void deleteAccounts(List<Account> accounts);
    }
}
