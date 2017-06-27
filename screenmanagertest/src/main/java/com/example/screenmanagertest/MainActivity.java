package com.example.screenmanagertest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;
import com.example.screenmanagertest.screenmanager.ScreenManager;
import com.example.screenmanagertest.screenmanager.SubWindowInfoRef;
import com.example.screenmanagertest.view.AudioView;
import com.example.screenmanagertest.view.DateTimeView;
import com.example.screenmanagertest.view.GalleryView;
import com.example.screenmanagertest.view.MarqueeView;
import com.example.screenmanagertest.view.MultiMediaView;
import com.example.screenmanagertest.view.PosterBaseView;
import com.example.screenmanagertest.view.TimerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {
    private Context mContext = null;
    private FrameLayout mMainLayout = null;

    private MediaInfoRef mBgImgInfo = null;

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

    // 加载新节目
    public void loadNewProgram(ArrayList<SubWindowInfoRef> subWndList) {
        // Create new program windows
        if (subWndList != null) {
            Logger.i("Window number is: " + subWndList.size());

            // Clean old program
            cleanupLayout();

            // initialize
            String touch=null;

            int xPos = 0;
            int yPos = 0;
            int width = 0;
            int height = 0;
            String wndName = null;
            String wndType = null;
            List<MediaInfoRef> mediaList = null;

            PosterBaseView tempSubWnd = null;
            mSubWndCollection = new HashSet<PosterBaseView>();

            // Through the sub window list, and create the correct view for it.
            for (SubWindowInfoRef subWndInfo : subWndList) {
                tempSubWnd = null;

                // 窗体类型和名称
                if ((wndType = subWndInfo.getSubWindowType()) == null) {
                    continue;
                }
                wndName = subWndInfo.getSubWindowName();

                touch=subWndInfo.getTouch();
                Log.i("jialei","loadNewProgram.touch:"+touch);
                // 窗体位置
                xPos = subWndInfo.getXPos();
                yPos = subWndInfo.getYPos();
                width = subWndInfo.getWidth();
                height = subWndInfo.getHeight();

                // 素材
                mediaList = subWndInfo.getSubWndMediaList();

                // 创建窗口
                if (wndType.contains("Main") || wndType.contains("StandbyScreen")) {
                    tempSubWnd = new MultiMediaView(this, true);
                } else if (wndType.contains("Background")) {
                    // 背景图片
                    if (mediaList != null && mediaList.size() > 0 && "File".equals(mediaList.get(0).source)) {
                        mBgImgInfo = mediaList.get(0);
//                        setWindowBackgroud();
                    }
                    continue;
                } else if (wndType.contains("Image") || wndType.contains("Weather")) {
                    tempSubWnd = new MultiMediaView(this);
                } else if (wndType.contains("Audio")) {
                    tempSubWnd = new AudioView(this);
                } else if (wndType.contains("Scroll")) {
                    tempSubWnd = new MarqueeView(this);
                } else if (wndType.contains("Clock")) {
                    tempSubWnd = new DateTimeView(this);
                } else if (wndType.contains("Gallery")) {
                    tempSubWnd = new GalleryView(this);
                } else if (wndType.contains("Timer")) {
                    tempSubWnd = new TimerView(this);
                }

                // 设置窗口参数，并添加
                if (tempSubWnd != null) {
                    tempSubWnd.setViewTouch(touch);
                    tempSubWnd.setViewName(wndName);
                    tempSubWnd.setViewType(wndType);
                    tempSubWnd.setMediaList(mediaList);
                    tempSubWnd.setViewPosition(xPos, yPos);
                    tempSubWnd.setViewSize(width, height);
                    mMainLayout.addView(tempSubWnd);
                    mSubWndCollection.add(tempSubWnd);
                }
            }
        }

        if (mSubWndCollection != null) {
            for (PosterBaseView subWnd : mSubWndCollection) {
                subWnd.startWork();
            }
        }
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


    public void startAudio() {
        if (mSubWndCollection != null) {
            for (PosterBaseView wnd : mSubWndCollection) {
                if (wnd.getViewName().startsWith("Audio")) {
                    wnd.onViewResume();
                }
            }
        }
    }

    public void stopAudio() {
        if (mSubWndCollection != null) {
            for (PosterBaseView wnd : mSubWndCollection) {
                if (wnd.getViewName().startsWith("Audio")) {
                    wnd.onViewPause();
                }
            }
        }
    }
}
