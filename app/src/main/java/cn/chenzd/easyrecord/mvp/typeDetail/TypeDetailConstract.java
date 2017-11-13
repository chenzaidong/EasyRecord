package cn.chenzd.easyrecord.mvp.typeDetail;

import java.util.List;

import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.Account;


/**
 * Created by chenzaidong on 2017/8/31.
 */

public interface TypeDetailConstract {
    interface Presenter extends BasePresenter {
        void getAccountByTypeId(Long typeId);
        void deleteAccount(Account account);
    }
    interface View extends BaseView<Presenter> {
        void onGetAccountByTypeId(List<Account> accounts);
        void onGetAccountByTypeIdFailed(String msg);
    }
}
