package cn.chenzd.easyrecord.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.entity.Type;


/**
 * 自定义Dialog
 * 用于点击事件回调
 * Created by chenzaidong on 2017/9/11.
 */

public class DeleteItemDialog extends Dialog implements View.OnClickListener {
    private TextView tvContent, tvDelete, tvCancel;
    private Type mType;
    private int mPosition;


    private OnDialogItemClickListener mOnDialogItemClickListener;

    public DeleteItemDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public DeleteItemDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected DeleteItemDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public void setType(Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }
    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_delete_item_layout, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvDelete = (TextView) view.findViewById(R.id.tv_delete);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvDelete.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                if (mOnDialogItemClickListener != null)
                    mOnDialogItemClickListener.onDialogItemClick(view);
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        tvContent.setText(getContext().getString(R.string.tip_content, mType.getAccounts().size()));
        super.show();
    }

    public interface OnDialogItemClickListener {
        void onDialogItemClick(View view);
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener) {
        this.mOnDialogItemClickListener = onDialogItemClickListener;
    }

    @Override
    public void onBackPressed() {

    }
}
