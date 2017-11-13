package cn.chenzd.easyrecord.mvp.addAccount;

import android.Manifest;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jph.takephoto.app.TakePhoto;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.R;
import io.reactivex.functions.Consumer;

import static cn.chenzd.easyrecord.constant.Constant.MAX_PAGES;


/**
 * 显示图片选择弹窗
 * Created by chenzaidong on 2017/9/3.
 */

public class SelectImagePopupWindow extends PopupWindow implements BaseQuickAdapter.OnItemChildClickListener{
    @BindView(R.id.rv_list)
    RecyclerView mRvList;
    @BindView(R.id.tv_album)
    TextView mTvAlbum;
    @BindView(R.id.tv_take_pictures)
    TextView mTvTakePictures;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    private AddAccountActivity mActivity;
    private File mOutImageFile;
    private ArrayList<String> mArrayList = new ArrayList<>();
    private QuickAdapter mQuickAdapter;
    public SelectImagePopupWindow(Activity activity, View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        mActivity = (AddAccountActivity) activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.view_select_images, null);
        ButterKnife.bind(this, view);
        setContentView(view);
        setAnimationStyle(R.style.popipwindow_anim_style);
        setTouchable(true);
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL,false));
        mQuickAdapter = new QuickAdapter();
        mQuickAdapter.bindToRecyclerView(mRvList);
        mQuickAdapter.setOnItemChildClickListener(this);
        initImageData();
    }

    /**
     * 初始化系统内图片
     */
    private void initImageData() {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            Cursor cursor = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
                            if (cursor!=null){
                                while (cursor.moveToNext()){
                                    byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                    mArrayList.add(new String(data,0,data.length-1));
                                }
                                mQuickAdapter.setNewData(mArrayList);
                            }
                        }
                    }
                });

    }


    /**
     * 打开系统相册
     */
    private void openAlbum() {
        int itemCount = mActivity.mQuickAdapter.getItemCount();
        if (itemCount >= MAX_PAGES) {
            Toast.makeText(mActivity, mActivity.getString(R.string.warning_max_pages, MAX_PAGES), Toast.LENGTH_SHORT).show();
        } else {
            TakePhoto takePhoto = mActivity.getTakePhoto();
            takePhoto.onPickMultiple(MAX_PAGES - itemCount);
        }
    }

    /**
     * 打开相机
     */
    private void takePhoto() {
        int itemCount = mActivity.mQuickAdapter.getItemCount();
        if (itemCount >= MAX_PAGES) {
            Toast.makeText(mActivity, mActivity.getString(R.string.warning_max_pages, MAX_PAGES), Toast.LENGTH_SHORT).show();
        } else {
            Uri imageUri;
            TakePhoto takePhoto = mActivity.getTakePhoto();
            mOutImageFile = new File(Environment.getExternalStorageDirectory(), "/EasyRecord/" + System.currentTimeMillis() + ".jpg");
            if (!mOutImageFile.getParentFile().exists()) mOutImageFile.getParentFile().mkdirs();
            imageUri = Uri.fromFile(mOutImageFile);
            takePhoto.onPickFromCapture(imageUri);
        }
    }


    @OnClick(R.id.tv_cancel)
    public void onMTvCancelClicked() {
        dismiss();
    }

    @OnClick({R.id.tv_album, R.id.tv_take_pictures, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_album://打开系统相册
                openAlbum();
                break;
            case R.id.tv_take_pictures://打开相机
                takePhoto();
                break;
            case R.id.tv_cancel://取消
                dismiss();
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int itemCount = mActivity.mQuickAdapter.getItemCount();
        if (itemCount >= MAX_PAGES) {
            Toast.makeText(mActivity, mActivity.getString(R.string.warning_max_pages, MAX_PAGES), Toast.LENGTH_SHORT).show();
        } else {
            mActivity.onGetImagesFromFast((String) adapter.getItem(position));
            dismiss();
        }
    }

    private class QuickAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        QuickAdapter() {
            super(R.layout.item_select_image_ayout, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.addOnClickListener(R.id.cv_image);
            ImageView imageView = helper.getView(R.id.iv_image);
            Glide.with(mActivity).load(item).error(R.drawable.error).into(imageView);
        }
    }

}
