package cn.chenzd.easyrecord.mvp.data;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import cn.chenzd.easyrecord.app.EasyRecordApplication;
import cn.chenzd.easyrecord.db.AccountDao;
import cn.chenzd.easyrecord.entity.Account;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * 账户数据操作类
 * Created by chenzaidong on 2017/8/28.
 */

public class AccountData {
    private AccountDao mAccountDao = EasyRecordApplication.getDaoSession().getAccountDao();

    /**
     * 添加账户
     *
     * @param account 添加的账户对象
     */
    public Observable<Boolean> addAccount(final Account account) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mAccountDao.insertOrReplaceInTx(account);
                account.getId();
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 删除账户
     *
     * @param accounts 删除的账户数组对象
     */
    public Observable<Boolean> deleteAccounts(final List<Account> accounts) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mAccountDao.deleteInTx(accounts);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 删除指定账户
     *
     * @param account 账户的账户对象
     */
    public Observable<Boolean> deleteAccount(final Account account) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                mAccountDao.delete(account);
                e.onNext(true);
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 通过账户ID获取账户对象
     *
     * @param typeId 账户ID
     */
    public Observable<List<Account>> getAccountsByTypeId(final Long typeId) {
        return Observable.create(new ObservableOnSubscribe<List<Account>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Account>> e) throws Exception {
                e.onNext(mAccountDao._queryType_Accounts(typeId));
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * 根据匹配的字符串查询账号
     * @param string
     * @return
     */
    public Observable<List<Account>> getAccountsByString(final String string) {
        return Observable.create(new ObservableOnSubscribe<List<Account>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Account>> e) throws Exception {
                Query<Account> build = mAccountDao.queryBuilder().whereOr(
                        AccountDao.Properties.Title.like("%" + string + "%"),
                        AccountDao.Properties.Name.like("%" + string + "%"),
                        AccountDao.Properties.Password.like("%" + string + "%"),
                        AccountDao.Properties.Notes.like("%" + string + "%")
                ).build();
                e.onNext(build.list());
            }
        }).subscribeOn(Schedulers.io());
    }
}
