package cn.chenzd.easyrecord.mvp.addAccount;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.preview.ImagePreviewActivity;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
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
import cn.chenzd.easyrecord.utils.ShoterHelper;
import cn.chenzd.easyrecord.view.SpaceItemDecoration;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.constant.Constant.DEFAULT_TRANSITION_NAME;
import static cn.chenzd.easyrecord.constant.Constant.MAX_PAGES;
import static cn.chenzd.easyrecord.constant.Constant.REQUEST_MEDIA_PROJECTION;
import static cn.chenzd.easyrecord.utils.ActivityUtils.checkNotNull;
import static cn.chenzd.easyrecord.utils.Utils.doBlur;
import static cn.chenzd.easyrecord.utils.Utils.getCurrentDate;


/**
 * 添加账户视图
 * Created by chenzaidong on 2017/8/28.
 */

public class AddAccountActivity extends BaseActivity implements AddAccountConstract.View, TakePhoto.TakeResultListener, InvokeListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemChildLongClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tiet_title)
    TextInputEditText tietTitle;
    @BindView(R.id.til_title)
    TextInputLayout tilTitle;
    @BindView(R.id.tiet_name)
    TextInputEditText tietName;
    @BindView(R.id.til_name)
    TextInputLayout tilName;
    @BindView(R.id.tiet_password)
    TextInputEditText tietPassword;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.iv_add_account)
    ImageView ivAddAccount;
    @BindView(R.id.ll_type)
    LinearLayout llType;
    @BindView(R.id.iv_type_icon)
    ImageView ivTypeIcon;
    @BindView(R.id.tv_type_name)
    TextView tvTypeName;
    @BindView(R.id.tv_notes)
    TextView tvNotes;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.rootView)
    CoordinatorLayout mRootView;
    private AddAccountConstract.Presenter mPresenter;
    private List<Type> mTypes;
    private Type mSelectType;
    private static final int SELECT_TYPE = 100;
    private static final int ADD_NOTES = 101;
    private SelectImagePopupWindow mPopupWindow;
    private InvokeParam invokeParam;
    private TakePhoto takePhoto;
    QuickAdapter mQuickAdapter;
    private List<Images> mTImageList = new ArrayList<>();
    private Account mEditAccount = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_add_account_layout);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    private void initData() {
        mPresenter = new AddAccountPresenter(this);
        mSelectType = getIntent().getParcelableExtra("type");
        mPresenter.getTypes();
        mQuickAdapter = new QuickAdapter();
        mRvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvList.addItemDecoration(new SpaceItemDecoration(10));
        mQuickAdapter.bindToRecyclerView(mRvList);

        mEditAccount = getIntent().getParcelableExtra("account");
        if (mEditAccount != null) {
            mTvTitle.setText(R.string.edit_account);
            tietTitle.setText(mEditAccount.getTitle());
            tietName.setText(mEditAccount.getName());
            tietPassword.setText(mEditAccount.getPassword());
            tvNotes.setText(mEditAccount.getNotes());
            mQuickAdapter.addData(mEditAccount.getImages());
        }
        getTakePhoto();
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        RxView.clicks(llType) //标题点击事件
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent(AddAccountActivity.this, SelectTypeActivity.class);
                        intent.putExtra("seletedId", (Long) ivTypeIcon.getTag());
                        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(AddAccountActivity.this,
                                Pair.create((View) ivTypeIcon, DEFAULT_TRANSITION_NAME + "iv"),
                                Pair.create((View) tvTypeName, DEFAULT_TRANSITION_NAME + "tv"));
                        startActivityForResult(intent, SELECT_TYPE, activityOptions.toBundle());
                    }
                });
        //添加 点击事件
        RxView.clicks(ivAddAccount).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (checkInputInfo()) {
                            Account account;
                            if (mEditAccount != null) {
                                account = mEditAccount;
                            } else {
                                account = new Account();
                                account.setCreateDate(getCurrentDate());
                            }
                            account.setTypeId((Long) ivTypeIcon.getTag());
                            account.setNotes(tvNotes.getText().toString());
                            account.setLastModifyTime(getCurrentDate());
                            account.setPassword(tietPassword.getText().toString());
                            account.setTitle(tietTitle.getText().toString());
                            account.setName(tietName.getText().toString());
                            mPresenter.addAccount(account, mQuickAdapter.getData());
                        }
                    }
                });
        //相机图标点击事件
        RxView.clicks(ivCamera).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        showSelectImagesView(ivCamera);
                    }
                });
        mQuickAdapter.setOnItemChildClickListener(this);
        mQuickAdapter.setOnItemChildLongClickListener(this);

        //备注 点击事件
        RxView.clicks(tvNotes).throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //请求权限
                        startActivityForResult(
                                ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE)).createScreenCaptureIntent(),
                                REQUEST_MEDIA_PROJECTION);
                    }
                });
        RxTextView.textChanges(tietTitle).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                if (charSequence.length() > 0) tietTitle.setError(null);
            }
        });
    }

    /**
     * 检查输入信息是否完整
     */
    private boolean checkInputInfo() {
        if (TextUtils.isEmpty(tietTitle.getText())) {
            tietTitle.setError(getResources().getString(R.string.error_input_title_is_null));
            return false;
        }
        return true;
    }

    /**
     * 显示相机或者相册弹窗
     *
     * @param view
     */
    private void showSelectImagesView(View view) {
        mPopupWindow = new SelectImagePopupWindow(this, view, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        // 在底部显示
        mPopupWindow.showAtLocation(ivCamera,
                Gravity.BOTTOM, 0, 0);
        setBackgroundAlpha(0.5f);//设置屏幕透明度
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
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

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setPresenter(AddAccountConstract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void onAddAccountFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddImagesSuccess() {
        finishAfterTransition();
    }

    /**
     * 快捷方式获取图片
     *
     * @param path
     */
    public void onGetImagesFromFast(String path) {
        if (mQuickAdapter.getItemCount() < MAX_PAGES) {
            Images images = new Images();
            images.setPath(path);
            mQuickAdapter.addData(images);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isActive = true;
        if (resultCode == RESULT_OK) {
            Logger.i("requestCode=" + requestCode);
            if (requestCode == SELECT_TYPE) {
                Long typeId = data.getLongExtra("typeId", -1L);
                Type type = getTypeById(typeId);
                if (type != null) {
                    mSelectType = null;
                    ivTypeIcon.setImageResource(type.getIconResId());
                    ivTypeIcon.setTag(type.getId());
                    tvTypeName.setText(type.getName());
                }
            } else if (requestCode == ADD_NOTES) {
                String notes = data.getStringExtra("notes");
                tvNotes.setText(notes);
            } else if (requestCode == REQUEST_MEDIA_PROJECTION) {
                //截屏回调
                ShoterHelper shotter = new ShoterHelper(data);
                shotter.startScreenShot(onShotListener);
            } else {
                takePhoto.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            if (requestCode == REQUEST_MEDIA_PROJECTION) {
                Intent intent = new Intent(AddAccountActivity.this, AddNoteActivity.class);
                intent.putExtra("notes", tvNotes.getText());
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(AddAccountActivity.this, tvNote, DEFAULT_TRANSITION_NAME);
                startActivityForResult(intent, ADD_NOTES, activityOptions.toBundle());
            }
            takePhoto.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onAddImagesFailed(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onShotListener = null;
    }

    private ShoterHelper.OnShotListener onShotListener = new ShoterHelper.OnShotListener() {
        @Override
        public void onFinish(Bitmap bitmap) {
            Logger.i("截屏图片大小=" + bitmap.getByteCount());
            Bitmap bluBitmap = doBlur(AddAccountActivity.this, bitmap, 10, 0.1f);
            Logger.i("模糊后图片大小=" + bluBitmap.getByteCount());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bluBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bitmapByte = baos.toByteArray();
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(AddAccountActivity.this, AddNoteActivity.class);
            intent.putExtra("notes", tvNotes.getText());
            intent.putExtra("bitmap", bitmapByte);
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(AddAccountActivity.this, tvNote, DEFAULT_TRANSITION_NAME);
            startActivityForResult(intent, ADD_NOTES, activityOptions.toBundle());
        }
    };

    /**
     * 获取账户类型回调
     *
     * @param types
     */
    @Override
    public void onGetTypes(List<Type> types) {
        Logger.i("onGetFirstType type");
        mTypes = types;
        if (mSelectType != null) {
            ivTypeIcon.setImageResource(mSelectType.getIconResId());
            ivTypeIcon.setTag(mSelectType.getId());
            tvTypeName.setText(mSelectType.getName());
        } else if (mEditAccount != null) {
            for (int i = 0; i < types.size(); i++) {
                if (mEditAccount.getTypeId().equals(types.get(i).getId())) {
                    ivTypeIcon.setImageResource(types.get(i).getIconResId());
                    ivTypeIcon.setTag(types.get(i).getId());
                    tvTypeName.setText(types.get(i).getName());
                    break;
                }
            }
        } else {
            ivTypeIcon.setImageResource(types.get(0).getIconResId());
            ivTypeIcon.setTag(types.get(0).getId());
            tvTypeName.setText(types.get(0).getName());
        }
        llType.setTransitionName(DEFAULT_TRANSITION_NAME);
    }

    /**
     * 通过类型Id获取账户类型对象
     *
     * @param id
     * @return
     */

    private Type getTypeById(Long id) {
        for (Type type : mTypes) {
            if (type.getId() == id) return type;
        }
        return null;
    }

    @Override
    public void takeCancel() {
        Logger.i("takeCancel");
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Logger.i("takeFail result=" + result + ",msg=" + msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        mPopupWindow.dismiss();
        List<Images> tempList = new ArrayList<>();
        ArrayList<TImage> images = result.getImages();
        for (TImage tImage : images) {
            Images image = new Images();
            image.setPath(tImage.getOriginalPath());
            tempList.add(image);
        }
        Logger.i(tempList.toString());
        mQuickAdapter.addData(tempList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    /**
     * adapter 子控件点击事件回调
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        List<ImageInfo> imageInfos = new ArrayList<>();
        for (Images image : mTImageList) {
            ImageInfo info = new ImageInfo();
            info.setThumbnailUrl(image.getPath());
            info.setBigImageUrl(image.getPath());
            imageInfos.add(info);
            info.imageViewWidth = view.getWidth();
            info.imageViewHeight = view.getHeight();
            int[] points = new int[2];
            view.getLocationInWindow(points);
            info.imageViewX = points[0];
            info.imageViewY = points[1] - getStatusHeight(this);
        }

        Intent intent = new Intent(this, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) imageInfos);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, position);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * 获得状态栏的高度
     */
    public int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * adapter 子控件长按事件回调
     */
    @Override
    public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, final int position) {
        Snackbar.make(view, R.string.is_delete_pic, Snackbar.LENGTH_LONG).setAction(R.string.delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuickAdapter.remove(position);
            }
        }).show();
        return true;
    }

    /**
     * 添加图片adapter
     */
    public class QuickAdapter extends BaseQuickAdapter<Images, BaseViewHolder> {
        public QuickAdapter() {
            super(R.layout.item_add_images_layout, mTImageList);
        }

        @Override
        protected void convert(BaseViewHolder helper, Images item) {
            helper.addOnLongClickListener(R.id.iv_image).addOnClickListener(R.id.iv_image);
            ImageView imageView = helper.getView(R.id.iv_image);
            Glide.with(AddAccountActivity.this).load(item.getPath()).error(R.drawable.error).into(imageView);
        }
    }
}
