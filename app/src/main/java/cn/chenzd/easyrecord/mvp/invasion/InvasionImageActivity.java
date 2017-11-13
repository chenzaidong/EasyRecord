package cn.chenzd.easyrecord.mvp.invasion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.chenzd.easyrecord.R;


/**
 * Created by chenzaidong on 2017/9/21.
 */

public class InvasionImageActivity extends AppCompatActivity {
    @BindView(R.id.iv_image)
    ImageView mIvImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invasion_image_layout);
        ButterKnife.bind(this);
        String path = getIntent().getStringExtra("path");
        mIvImage.setTransitionName(path);
        Glide.with(this).load(path).into(mIvImage);
    }
}
