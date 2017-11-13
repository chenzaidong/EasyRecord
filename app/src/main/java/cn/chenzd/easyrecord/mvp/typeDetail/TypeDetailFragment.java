package cn.chenzd.easyrecord.mvp.typeDetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding2.view.RxView;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.mvp.addAccount.AddAccountActivity;
import cn.chenzd.easyrecord.mvp.home.HomeActivity;
import cn.chenzd.easyrecord.utils.Utils;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 账户类型详情页
 * Created by chenzaidong on 2017/8/31.
 */

public class TypeDetailFragment extends Fragment implements TypeDetailConstract.View, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.rv_type_detail_list)
    RecyclerView rvList;
    Unbinder unbinder;
    @BindView(R.id.ll_type)
    LinearLayout llType;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    private TypeDetailConstract.Presenter mPresenter;
    private QuickAdapter mQuickAdapter;
    private Type mType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new TypeDetailPresenter(this);
        setSharedElementEnterTransition(
                TransitionInflater.from(getContext())
                        .inflateTransition(android.R.transition.move));
        setSharedElementReturnTransition(TransitionInflater.from(getContext())
                .inflateTransition(android.R.transition.move));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_type_detail_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        mType = getArguments().getParcelable("type");
        llType.setTransitionName(mType.getIconResId() + "");
        ivIcon.setImageResource(mType.getIconResId());
        tvName.setText(mType.getName());
        mQuickAdapter = new QuickAdapter();
        mQuickAdapter.setOnItemChildClickListener(this);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mQuickAdapter.bindToRecyclerView(rvList);
        srlRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        initListener();
        return view;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    private void initListener() {
        mQuickAdapter.setOnItemClickListener(this);
        RxView.clicks(llType).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                getFragmentManager().popBackStack();
            }
        });
        RxRecyclerView.scrollEvents(rvList)
                .subscribe(new Consumer<RecyclerViewScrollEvent>() {
                    @Override
                    public void accept(RecyclerViewScrollEvent recyclerViewScrollEvent) throws Exception {
                        if (recyclerViewScrollEvent.dy() > 0 && Math.abs(recyclerViewScrollEvent.dy()) > 20) {
                            ((HomeActivity) getActivity()).onShowOrHide(false);
                        } else if (recyclerViewScrollEvent.dy() < 0 && Math.abs(recyclerViewScrollEvent.dy()) > 20) {
                            ((HomeActivity) getActivity()).onShowOrHide(true);
                        }
                    }
                });
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getAccountByTypeId(mType.getId());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
        mPresenter.getAccountByTypeId(mType.getId());
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(TypeDetailConstract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onGetAccountByTypeId(List<Account> accounts) {
        srlRefresh.setRefreshing(false);
        mQuickAdapter.setNewData(accounts);
        tvCount.setText(String.valueOf(accounts.size()));
        if (mQuickAdapter.getItemCount() == 0 && mQuickAdapter.getEmptyViewCount() == 0) {
            mQuickAdapter.setEmptyView(R.layout.view_empty_layout);
        }
    }

    @Override
    public void onGetAccountByTypeIdFailed(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.tv_notes:
            case R.id.tv_name:
            case R.id.et_password:
            case R.id.tv_title:
                if (Utils.copyText((TextView) view))
                    Snackbar.make(view, getString(R.string.hasCopy, ((TextView) view).getText()), Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.right_menu_delete:
                deleteItem(position);
                break;
            case R.id.right_menu_edit:
                Account item = mQuickAdapter.getItem(position);
                Intent intent = new Intent(getContext(), AddAccountActivity.class);
                intent.putExtra("account", item);
                startActivity(intent);
                break;
        }
    }

    private void deleteItem(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog alertDialog = builder.setMessage(getString(R.string.is_delete) + mQuickAdapter.getItem(position).getTitle())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Account item = mQuickAdapter.getItem(position);
                        mQuickAdapter.remove(position);
                        mPresenter.deleteAccount(item);
                        tvCount.setText(String.valueOf(mQuickAdapter.getData().size()));
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.addNoteBG));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Logger.i("onItemClick position=" + position);
    }

    private class QuickAdapter extends BaseQuickAdapter<Account, BaseViewHolder> {
        QuickAdapter() {
            super(R.layout.item_type_detail_layout, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, Account item) {
            item.resetImages();
            List<Images> images = item.getImages();
            helper.setText(R.id.tv_title, item.getTitle())
                    .setText(R.id.tv_name, item.getName())
                    .setText(R.id.tv_notes, item.getNotes())
                    .setText(R.id.et_password, item.getPassword())
                    .addOnClickListener(R.id.tv_notes)
                    .addOnClickListener(R.id.tv_name)
                    .addOnClickListener(R.id.et_password)
                    .addOnClickListener(R.id.tv_title)
                    .addOnClickListener(R.id.right_menu_delete)
                    .addOnClickListener(R.id.right_menu_edit);
            NineGridView recyclerView = helper.getView(R.id.ngv_images);
            if (images.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                for (Images imagesEntity : images) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(imagesEntity.getPath());
                    info.setBigImageUrl(imagesEntity.getPath());
                    imageInfo.add(info);
                }
                recyclerView.setAdapter(new NineGridViewClickAdapter(getContext(), imageInfo));
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }

        @Override
        public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
            super.setOnItemClickListener(listener);
        }
    }
}
