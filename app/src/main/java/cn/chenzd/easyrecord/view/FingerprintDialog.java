package cn.chenzd.easyrecord.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.utils.FingerprintUtil;


/**
 * Created by chenzaidong on 2017/9/11.
 */

public class FingerprintDialog extends Dialog {
    private TextView mTvCancel;

    public FingerprintDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_fingerprint, null);
        setContentView(view);
        mTvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FingerprintUtil.cancel();
                dismiss();
            }
        });
        setCanceledOnTouchOutside(false);
    }

    @Override
    public void onBackPressed() {

    }
}
