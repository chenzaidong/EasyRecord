package cn.chenzd.easyrecord.mvp.invasion;

import java.util.List;

import cn.chenzd.easyrecord.entity.InvasionRecord;
import cn.chenzd.easyrecord.mvp.data.InvasionRecordData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * 入侵记录presenter
 * Created by chenzaidong on 2017/9/12.
 */

public class InvasionPresenter implements InvasionConstract.Presenter {
    private CompositeDisposable mCompositeDisposable;
    private InvasionConstract.View mView;
    private InvasionRecordData mInvasionRecordData;

    public InvasionPresenter(InvasionConstract.View view) {
        mCompositeDisposable = new CompositeDisposable();
        mView = view;
        view.setPresenter(this);
        mInvasionRecordData = new InvasionRecordData();
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        mCompositeDisposable.clear();
    }

    /**
     * 获取所有入侵记录
     */
    @Override
    public void getAllInvasionRecord() {
        mCompositeDisposable.add(mInvasionRecordData.getAllInvasionRecord()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<InvasionRecord>>() {
                    @Override
                    public void accept(List<InvasionRecord> invasionRecords) throws Exception {
                        mView.onGetAllInvasionRecord(invasionRecords);
                    }
                }));
    }

    /**
     * 删除入侵记录
     * @param invasionRecord
     */
    @Override
    public void deleteInvasionRecord(InvasionRecord invasionRecord) {
        mCompositeDisposable.add(mInvasionRecordData.deleteInvasionRecord(invasionRecord)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.onDeleteSuccess();
                    }
                }));
    }

}
