package com.immotor.batterystation.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by Ashion on 2017/5/8.
 */

public class WebActivity extends BaseActivity {
    public static final int TYPE_VALUE_USER_PROTOCOL = 1;
    public static final int TYPE_VALUE_RECHARGE_PROTOCOL = 2;
    public static final String TYPE_KEY = "web_type";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.webView)
    WebView webView;
    @Bind(R.id.loading_progress)
    ProgressBar loadingProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
    }

    @Override
    public void initUIView() {
        int type = getIntent().getIntExtra(TYPE_KEY,1);
        String title;
        String url;
        switch (type){
            case TYPE_VALUE_USER_PROTOCOL:
                title = "用户协议";
                url = "http://www.baidu.com";
                break;
            case TYPE_VALUE_RECHARGE_PROTOCOL:
                title = "充值协议";
                url = "http://www.baidu.com";
                break;
                default:
                    title = "用户协议";
                    url = "http://www.baidu.com";
                    break;
        }
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadingProgress.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成
                    if (loadingProgress != null) {
                        loadingProgress.setVisibility(View.GONE);
                    }
                } else {
                    // 加载中
                    if (loadingProgress != null) {
                        loadingProgress.setProgress(newProgress);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        if(webView!=null){
            webView.stopLoading();
        }
        super.onStop();
    }
}
