package cn.chenzd.easyrecord.mvp.addAccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.data.TypeData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.constant.Constant.DEFAULT_TRANSITION_NAME;


/**
 * 账户类型选择视图
 * Created by chenzaidong on 2017/8/29.
 */

public class SelectTypeActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rl_list)
    RecyclerView rlList;
    private Long seletedId;
    private QuickAdapter mQuickAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_layout);
        ButterKnife.bind(this);
        rlList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rlList.setLayoutManager(new LinearLayoutManager(this));
        mQuickAdapter = new QuickAdapter();
        mQuickAdapter.bindToRecyclerView(rlList);
        mQuickAdapter.setOnItemClickListener(this);
        seletedId = getIntent().getLongExtra("seletedId", -1);
        new TypeData().getTypes().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Type>>() {
                    @Override
                    public void accept(List<Type> types) throws Exception {
                        mQuickAdapter.setNewData(types);
                    }
                });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        view.findViewById(R.id.iv_type_icon).setTransitionName(DEFAULT_TRANSITION_NAME + "iv");
        view.findViewById(R.id.tv_type_name).setTransitionName(DEFAULT_TRANSITION_NAME + "tv");
        Intent intent = new Intent();
        intent.putExtra("typeId", mQuickAdapter.getData().get(position).getId());
        setResult(RESULT_OK, intent);
        finishAfterTransition();
    }

    class QuickAdapter extends BaseQuickAdapter<Type, BaseViewHolder> {
        QuickAdapter() {
            super(R.layout.item_select_type_layout, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, Type item) {
            helper.setText(R.id.tv_type_name, item.getName())
                    .setImageResource(R.id.iv_type_icon, item.getIconResId())
                    .setVisible(R.id.iv_checked, seletedId == item.getIconResId());
        }
    }
}
