package cn.chenzd.easyrecord.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.app.EasyRecordApplication;


/**
 * his provides methods to help Activities load their UI.
 * Created by chenzaidong on 2017/8/24.
 */

public class ActivityUtils {
    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        fragmentManager.beginTransaction()
                .replace(frameId, fragment,fragment.getClass().getSimpleName())
                .commit();
    }

    /**
     * 图片全屏半透明状态栏（图片位于状态栏下面）
     *
     * @param activity
     */
    public static void setImageTranslucent(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
    }

    /**
     * 获取指定资源数据
     */
    public static int[] getListRes(int id) {
        TypedArray ar = EasyRecordApplication.getContext().getResources().obtainTypedArray(id);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++) {
            int resId = ar.getResourceId(i, 0);
            if (resId != 0) {
                resIds[i] = resId;
            }
        }
        ar.recycle();
        return resIds;
    }

    public static <T> T checkNotNull(T arg) {
        if (arg == null) {
            throw new NullPointerException("Argument must not be null");
        }
        return arg;
    }

    /**
     * 创建一个显示图标的popupWindow
     *
     * @param mPopupWindowAdapter 布局适配器
     */
    public static PopupWindow createPopupWindow(Context context, BaseQuickAdapter mPopupWindowAdapter) {
        PopupWindow popupWindow = new PopupWindow();
        View v = LayoutInflater.from(context).inflate(R.layout.view_select_icon_layout, null);
        RecyclerView rl = (RecyclerView) v.findViewById(R.id.rv_list);
        rl.setLayoutManager(new GridLayoutManager(context, 5));
        mPopupWindowAdapter.bindToRecyclerView(rl);
        mPopupWindowAdapter.isFirstOnly(false);
        mPopupWindowAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(context.getResources().getDimensionPixelSize(R.dimen.y700));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setContentView(v);
        popupWindow.setElevation(20);
        return popupWindow;
    }

}
