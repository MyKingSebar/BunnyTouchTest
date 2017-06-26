package com.example.screenmanagertest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.example.screenmanagertest.screenmanager.ScreenManager;
import com.example.screenmanagertest.screenmanager.SubWindowInfoRef;
import com.example.screenmanagertest.view.PosterBaseView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity {
    private Context mContext = null;
    private FrameLayout mMainLayout = null;


    private Set<PosterBaseView> mSubWndCollection = null; // 屏幕布局信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initScreen();

        mMainLayout = ((FrameLayout) findViewById(R.id.fl));
        mMainLayout.setBackgroundColor(Color.BLACK);

        // 启动屏幕管理线程
        if (ScreenManager.getInstance() == null) {
            ScreenManager.createInstance(this).startRun();
        }
    }

    private void initScreen() {
        PosterApplication.setScreenHeight(getScreenHeigth());
        PosterApplication.setScreenWidth(getScreenWidth());
    }

    public int getScreenHeigth() {
        int screenHeight = 0;
        // 获取屏幕实际大小(以像素为单位)
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenHeight = metric.heightPixels;


        return screenHeight;
    }

    public int getScreenWidth() {
        int screenWidth = 0;

        // 获取屏幕实际大小(以像素为单位)
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;

        return screenWidth;
    }

    // 加载新节??
    public void loadNewProgram(ArrayList<SubWindowInfoRef> subWndList) {

    }


    private void cleanupLayout() {
        // 移除所有窗??
        if (mSubWndCollection != null) {
            for (PosterBaseView wnd : mSubWndCollection) {
                wnd.onViewDestroy();
            }
            mMainLayout.removeAllViews();
            mSubWndCollection.clear();
            mSubWndCollection = null;
        }

        // 清除背景图片
        if (mBgImgInfo != null) {
            mBgImgInfo = null;
            mMainLayout.setBackground(null);
        }

        // 清空上一个节目的缓存
        PosterApplication.clearMemoryCache();
    }
}
