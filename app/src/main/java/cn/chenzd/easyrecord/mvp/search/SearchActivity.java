package cn.chenzd.easyrecord.mvp.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.mvp.addAccount.AddAccountActivity;
import cn.chenzd.easyrecord.mvp.data.AccountData;
import cn.chenzd.easyrecord.mvp.data.ImagesData;
import cn.chenzd.easyrecord.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.app.EasyRecordApplication.getContext;

/**
 * 搜索界面
 * Created by chenzaidong on 2017/9/25.
 */

public class SearchActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R.id.sv_search)
    SearchView mSvSearch;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    private AccountData mAccountData;
    private QuickAdapter mQuickAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_layout);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mSvSearch.setTransitionName("search");
        mSvSearch.setIconifiedByDefault(false);
        mSvSearch.setSubmitButtonEnabled(true);
        mSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSvSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setSvSearch(newText);
                return false;
            }
        });
    }

    private void initData() {
        mAccountData = new AccountData();
        mQuickAdapter = new QuickAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mQuickAdapter.bindToRecyclerView(mRvList);
        mQuickAdapter.setOnItemChildClickListener(this);
    }

    /**
     * 搜索
     *
     * @param string
     */
    private void setSvSearch(String string) {
        if (TextUtils.isEmpty(string)) {
            mQuickAdapter.setNewData(null);
            return;
        }
        mAccountData.getAccountsByString(string).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Account>>() {
                    @Override
                    public void accept(List<Account> accounts) throws Exception {
                        Logger.i(accounts.toString());
                        mQuickAdapter.setNewData(accounts);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Logger.e(throwable, throwable.getMessage());
                    }
                });
    }

    @OnClick(R.id.tv_cancel)
    public void onViewClicked() {
        finishAfterTransition();
    }

    private void deleteItem(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setMessage(getString(R.string.is_delete) + mQuickAdapter.getItem(position).getTitle())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Account item = mQuickAdapter.getItem(position);
                        mQuickAdapter.remove(position);
                        new ImagesData().deleteImages(item.getImages())
                                .subscribe();
                        mAccountData.deleteAccount(item)
                                .subscribe();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.addNoteBG));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
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
        public void setOnItemClickListener(@Nullable BaseQuickAdapter.OnItemClickListener listener) {
            super.setOnItemClickListener(listener);
        }
    }
}
