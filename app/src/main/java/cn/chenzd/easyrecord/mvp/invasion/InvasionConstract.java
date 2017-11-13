package cn.chenzd.easyrecord.mvp.invasion;

import java.util.List;

import cn.chenzd.easyrecord.base.BasePresenter;
import cn.chenzd.easyrecord.base.BaseView;
import cn.chenzd.easyrecord.entity.InvasionRecord;


/**
 * Created by chenzaidong on 2017/9/12.
 */

public interface InvasionConstract {
    interface  Presenter extends BasePresenter {
        void getAllInvasionRecord();
        void deleteInvasionRecord(InvasionRecord invasionRecord);
    }
    interface View extends BaseView<Presenter> {
        void onGetAllInvasionRecord(List<InvasionRecord> invasionRecords);
        void onDeleteSuccess();
    }
}
