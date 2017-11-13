package cn.chenzd.easyrecord.mvp.addAccount;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;

import static cn.chenzd.easyrecord.constant.Constant.DEFAULT_TRANSITION_NAME;

/**
 * 添加备注视图
 * Created by chenzaidong on 2017/8/29.
 */

public class AddNoteActivity extends BaseActivity {
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.et_note)
    EditText etNotes;
    @BindView(R.id.ll_bg)
    LinearLayout llBg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ButterKnife.bind(this);
        etNotes.setText(getIntent().getStringExtra("notes"));
        tvNote.setTransitionName(DEFAULT_TRANSITION_NAME);
        etNotes.setSelection(etNotes.getText().length());
        byte[] bitmapByte = getIntent().getByteArrayExtra("bitmap");
        if (bitmapByte != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            if (bitmap != null)
                llBg.setBackground(new BitmapDrawable(getResources(), bitmap));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.tv_done)
    public void onClick() {
        Intent intent = new Intent();
        intent.putExtra("notes", etNotes.getText().toString());
        setResult(RESULT_OK, intent);
        finishAfterTransition();
    }
}
