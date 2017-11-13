package cn.chenzd.easyrecord.mvp.editType;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.jakewharton.rxbinding2.view.RxView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;
import cn.chenzd.easyrecord.entity.Account;
import cn.chenzd.easyrecord.entity.Images;
import cn.chenzd.easyrecord.entity.Type;
import cn.chenzd.easyrecord.utils.ActivityUtils;
import cn.chenzd.easyrecord.view.DeleteItemDialog;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.app.EasyRecordApplication.getContext;
import static cn.chenzd.easyrecord.constant.Constant.DEFAULT_ICON_ID;
import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;


/**
 * 编辑分类视图
 * Created by chenzaidong on 2017/8/26.
 */

public class EditTypeActivity extends BaseActivity implements EditConstract.View, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, DeleteItemDialog.OnDialogItemClickListener {
    @BindView(R.id.rl_list)
    RecyclerView rlList;
    @BindView(R.id.iv_add_type)
    ImageView ivAddType;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab_commit)
    FloatingActionButton mFabCommit;
    private EditConstract.Presenter mPresenter;
    private List<Type> mDatas;
    private ItemDragAdapter mItemDragAdapter;
    private PopupWindow mPopupWindow;
    private ImageView mEditIconView;
    private PopupWindowAdapter mPopupWindowAdapter;
    private List<Integer> mIcons;
    private List<Type> mDeleteTypes = new ArrayList<>();
    private List<Images> mDeleteImages = new ArrayList<>();
    private List<Account> mDeleteAccount = new ArrayList<>();

    private DeleteItemDialog mDeleteItemDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_type);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initListener() {
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mItemDragAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rlList);

        // 开启拖拽
        mItemDragAdapter.enableDragItem(itemTouchHelper, R.id.iv_drag, true);
        mItemDragAdapter.setOnItemDragListener(onItemDragListener);
        //添加分类
        RxView.clicks(ivAddType)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Type data = new Type();
                        data.setIconResId(DEFAULT_ICON_ID);
                        mItemDragAdapter.addData(0, data);
                        ivAddType.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showPopupWindow(mItemDragAdapter.getViewByPosition(0, R.id.iv_icon), 0);
                            }
                        }, 500);

                    }
                });
        //保存修改
        RxView.clicks(mFabCommit)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (checkTypeData()) {
                            List<Type> data = mItemDragAdapter.getData();
                            for (int i = 0; i < data.size(); i++) {
                                data.get(i).setOrder(i + 1);
                            }
                            mPresenter.deleteTypes(mDeleteTypes);
                            mPresenter.deleteImages(mDeleteImages);
                            mPresenter.deleteAccounts(mDeleteAccount);
                            mPresenter.saveTypes(mItemDragAdapter.getData());
                        }
                    }
                });
    }

    /**
     * 检查数据是否完整
     *
     * @return
     */
    private boolean checkTypeData() {
        List<Type> data = mItemDragAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            if (TextUtils.isEmpty(data.get(i).getName())) {
                Toast.makeText(this, R.string.name_not_null, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); //关键
        rlList.setLayoutManager(layoutManager);
        rlList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mItemDragAdapter = new ItemDragAdapter();
        mItemDragAdapter.bindToRecyclerView(rlList);
        mItemDragAdapter.setOnItemChildClickListener(this);
        mItemDragAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mItemDragAdapter.isFirstOnly(false);
        mPresenter = new EditTypePresenter(this);

        mPopupWindowAdapter = new PopupWindowAdapter();
        mPopupWindowAdapter.setOnItemClickListener(this);
        mPopupWindow = ActivityUtils.createPopupWindow(this, mPopupWindowAdapter);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

        mPresenter.getTypes();
        mPresenter.getIcons();
    }


    /**
     * 设备背景透明度
     *
     * @param bgAlpha 0-1
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(EditConstract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }



    @Override
    public void onGetTypes(List<Type> types) {
        mDatas = checkNotNull(types);
        mItemDragAdapter.replaceData(types);
    }

    @Override
    public void onSaveTypesSuccess() {
        finishAfterTransition();
    }

    @Override
    public void onSaveTypesFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetIcons(List<Integer> icons) {
        mIcons = icons;
        mPopupWindowAdapter.setNewData(icons);
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        switch (view.getId()) {
            case R.id.iv_delete:
                deleteItem(adapter, view, position);
                break;
            case R.id.iv_icon:
                showPopupWindow(view, position);
                break;
        }
    }

    private void deleteItem(final BaseQuickAdapter adapter, View view, final int position) {
        TextView tvCount = (TextView) adapter.getViewByPosition(position, R.id.tv_count);
        if ("0".equals(tvCount.getText())) {
            Snackbar.make(view, getResources().getString(R.string.confirm_delete, ((Type) adapter.getData().get(position)).getName()), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.delete, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDeleteTypes.add((Type) adapter.getItem(position));
                            adapter.remove(position);
                        }
                    }).show();
        } else {
            mDeleteItemDialog = new DeleteItemDialog(this);
            mDeleteItemDialog.setType((Type) adapter.getItem(position));
            mDeleteItemDialog.setPosition(position);
            mDeleteItemDialog.setOnDialogItemClickListener(this);
            mDeleteItemDialog.show();
            Window dialogWindow = mDeleteItemDialog.getWindow();
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.height = getResources().getDimensionPixelSize(R.dimen.y450); // 高度设置为屏幕的0.6，根据实际情况调整
            p.width = getResources().getDimensionPixelSize(R.dimen.x700); // 宽度设置为屏幕的0.65，根据实际情况调整
            dialogWindow.setAttributes(p);
        }
    }

    /**
     * 显示popupWindow
     *
     * @param view     显示的位置控件
     * @param position 在adatper中的位置
     */
    private void showPopupWindow(View view, int position) {
        mPopupWindow.showAsDropDown(view);
        mEditIconView = (ImageView) view;
        mEditIconView.setTag(position);
        setBackgroundAlpha(0.5f);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        mEditIconView.setImageResource(mIcons.get(position));
        mItemDragAdapter.getItem((Integer) mEditIconView.getTag()).setIconResId(mIcons.get(position));
    }

    @Override
    public void onDialogItemClick(View view) {
        switch (view.getId()) {
            //全部删除
            case R.id.tv_delete:
                Logger.i("tv_delete");
                Type type = mDeleteItemDialog.getType();
                if (type != null) {
                    mDeleteTypes.add(type);
                    List<Account> accounts = type.getAccounts();
                    if (accounts != null) {
                        for (int i = 0; i < accounts.size(); i++) {
                            Account account = accounts.get(i);
                            List<Images> images = account.getImages();
                            if (images != null) {
                                mDeleteImages.addAll(images);
                            }
                        }
                        mDeleteAccount.addAll(accounts);
                    }
                }
                mItemDragAdapter.remove(mDeleteItemDialog.getPosition());
                break;
        }
    }

    /**
     * 可拖拽列表adapter
     */
    private class ItemDragAdapter extends BaseItemDraggableAdapter<Type, BaseViewHolder> {
        ItemDragAdapter() {
            super(R.layout.item_edit_type_layout, mDatas);
        }

        @Override
        protected void convert(BaseViewHolder helper, final Type item) {
            List<Account> accounts = new ArrayList<>();
            if (item.getId() != null) {
                accounts = item.getAccounts();
            }
            helper.setText(R.id.tlet_name, item.getName())
                    .setImageResource(R.id.iv_icon, item.getIconResId())
                    .setText(R.id.tv_count, String.valueOf(accounts.size()))
                    .setTextColor(R.id.tv_count, accounts.size() == 0 ? ContextCompat.getColor(getContext(), R.color.textColorPrimary) : ContextCompat.getColor(getContext(), R.color.colorAccent))
                    .addOnClickListener(R.id.iv_delete)
                    .addOnClickListener(R.id.iv_icon)
                    .setVisible(R.id.iv_delete,!item.getIsDefaultType());
            final TextInputEditText inputEditText = helper.getView(R.id.tlet_name);
            final TextInputLayout textInputLayout = helper.getView(R.id.til_name);
            inputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    item.setName(s.toString());
                    if (TextUtils.isEmpty(s)) {
                        textInputLayout.setErrorEnabled(true);
                        textInputLayout.setError(getString(R.string.name_not_null));
                    } else {
                        textInputLayout.setErrorEnabled(false);
                        textInputLayout.setError(null);
                    }
                }
            });
        }
    }

    /**
     * 拖拽监听
     */
    private OnItemDragListener onItemDragListener = new OnItemDragListener() {
        private float mTempTranslationZ;
        private float mTempElevation;

        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            mTempTranslationZ = viewHolder.itemView.getTranslationZ();
            mTempElevation = viewHolder.itemView.getElevation();
            viewHolder.itemView.setElevation(5f);
            viewHolder.itemView.setTranslationZ(10f);
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
            viewHolder.itemView.setElevation(mTempElevation);
            viewHolder.itemView.setTranslationZ(mTempTranslationZ);
        }
    };

    /**
     * 弹窗中图标列表adapter
     */
    private class PopupWindowAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
        PopupWindowAdapter() {
            super(R.layout.item_icon_layout, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, Integer item) {
            helper.setImageResource(R.id.iv_icon, item);
        }
    }
}
