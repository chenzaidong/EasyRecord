package cn.chenzd.easyrecord.mvp.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.typeDetail.TypeDetailFragment;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 主页视图
 * Created by chenzaidong on 2017/8/25.
 */

public class HomeFragment extends Fragment implements HomeContract.View, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    Unbinder unbinder;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    private HomeContract.Presenter mPresenter;
    private QuickAdapter mQuickAdapter;

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onGetTypes(List<Type> types) {
        mQuickAdapter.setNewData(types);
        srlRefresh.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        mPresenter.getTypes();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Type type = mQuickAdapter.getData().get(position);
        ((HomeActivity) getActivity()).onSelectType(type);
        TypeDetailFragment fragment = new TypeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("type", type);
        fragment.setArguments(bundle);
        view.setTransitionName(((Type) adapter.getItem(position)).getIconResId() + "");
        getFragmentManager().beginTransaction()
                .addSharedElement(view, ((Type) adapter.getItem(position)).getIconResId() + "")
                .replace(R.id.fl_content, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).onSelectType(null);
        ((HomeActivity) getActivity()).onShowOrHide(true);
        View view = inflater.inflate(R.layout.fg_home_comtent, container, false);
        unbinder = ButterKnife.bind(this, view);
        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mQuickAdapter = new QuickAdapter();
        mQuickAdapter.bindToRecyclerView(mRvList);
        mQuickAdapter.setOnItemClickListener(this);
        srlRefresh.setOnRefreshListener(this);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class QuickAdapter extends BaseQuickAdapter<Type, BaseViewHolder> {
        public QuickAdapter() {
            super(R.layout.item_home_layout, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, Type item) {
            item.resetAccounts();
            List<Account> accounts = item.getAccounts();
            helper.setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_count, String.valueOf(accounts.size()))
                    .setTextColor(R.id.tv_count, accounts.size() == 0 ? ContextCompat.getColor(getContext(), R.color.textColorPrimary) : ContextCompat.getColor(getContext(), R.color.colorAccent))
                    .setImageResource(R.id.iv_icon, item.getIconResId());
        }
    }
}
