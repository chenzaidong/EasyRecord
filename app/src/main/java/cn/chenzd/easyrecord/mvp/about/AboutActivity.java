package cn.chenzd.easyrecord.mvp.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.chenzd.easyrecord.R;
import cn.chenzd.easyrecord.base.BaseActivity;

/**
 * 关于
 * Created by chenzaidong on 2017/9/25.
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.tv_blog)
    TextView mTvBlog;
    @BindView(R.id.tv_github)
    TextView mTvGithub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_layout);
        ButterKnife.bind(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isActive = true;
        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick(R.id.tv_blog)
    public void onTvBlogClicked() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(mTvBlog.getText().toString());
        intent.setData(content_url);
        startActivityForResult(intent, 1000);
    }

    @OnClick(R.id.tv_github)
    public void onTvGithubClicked() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(mTvGithub.getText().toString());
        intent.setData(content_url);
        startActivityForResult(intent, 1000);
    }
}
