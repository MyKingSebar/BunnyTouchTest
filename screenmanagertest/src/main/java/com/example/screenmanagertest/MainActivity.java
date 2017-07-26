package com.example.screenmanagertest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.example.screenmanagertest.fragment.NewLoadFragment;
import com.example.screenmanagertest.fragment.PowerOnOffFragment;
import com.example.screenmanagertest.power.PowerOnOffManager;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;
import com.example.screenmanagertest.screenmanager.ScreenManager;
import com.example.screenmanagertest.screenmanager.SubWindowInfoRef;
import com.example.screenmanagertest.view.PosterBaseView;

import java.util.ArrayList;
import java.util.Set;

import me.yokeyword.fragmentation.SupportActivity;

public class MainActivity extends SupportActivity {
    private float mouseX=0;
    private float mouseY=0;


    private Context mContext = null;
    private FrameLayout mMainLayout = null;

    private MediaInfoRef mBgImgInfo = null;

    private Set<PosterBaseView> mSubWndCollection = null; // 屏幕布局信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        if (savedInstanceState == null) {
//            loadRootFragment(R.id.fl, NewLoadFragment.newInstance(null,this));  // 加载根Fragment
//        }

        mContext = this;
        initScreen();

        mMainLayout = ((FrameLayout) findViewById(R.id.fl));
        mMainLayout.setBackgroundColor(Color.BLACK);

        // 启动屏幕管理线程
        if (ScreenManager.getInstance() == null) {
            ScreenManager.createInstance(this).startRun();
            Log.i("jialei","ScreenManager.getInstance() == null");
        }else{
            Log.i("jialei","ScreenManager.getInstance() != null");
            ScreenManager.getInstance().startRun();
        }
//        PosterApplication.getInstance().initAppParam();
        PowerOnOffManager.getInstance().checkAndSetOnOffTime(PowerOnOffManager.AUTOSCREENOFF_COMMON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initService();
        PowerOnOffManager.getInstance().checkAndSetOnOffTime(PowerOnOffManager.AUTOSCREENOFF_COMMON);
    }

    @Override
    protected void onDestroy() {
        Log.i("jialei","ActivityDestoy");
        super.onDestroy();
        // 结束屏幕管理线程
        if (ScreenManager.getInstance() != null) {
            ScreenManager.getInstance().stopRun();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("jialei","ActivityonStop");
//        if (ScreenManager.getInstance() != null) {
//            ScreenManager.getInstance().stopRun();
//        }
    }

    private void initScreen() {
        PosterApplication.setScreenHeight(getScreenHeigth());
        PosterApplication.setScreenWidth(getScreenWidth());
    }

    private void initService(){
        Intent service = new Intent();
        service.setAction("com.ys.powerservice.sysctrlservice");
        startService(service);
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
    public void loadNewProgram2(ArrayList<SubWindowInfoRef> subWndList) {
        start(NewLoadFragment.newInstance(subWndList,this));
    }
    // 加载新节目
    public void loadNewProgram(ArrayList<SubWindowInfoRef> subWndList) {
        loadRootFragment(R.id.fl, NewLoadFragment.newInstance(subWndList,this))  ;
//        start(NewLoadFragment.newInstance(subWndList,this));
    }


//    public void loadNewProgram(ArrayList<SubWindowInfoRef> subWndList) {
//        // Create new program windows
//        if (subWndList != null) {
//            Logger.i("Window number is: " + subWndList.size());
//
//            // Clean old program
//            cleanupLayout();
//
//            // initialize
//            String touch=null;
//
//            int xPos = 0;
//            int yPos = 0;
//            int width = 0;
//            int height = 0;
//            String wndName = null;
//            String wndType = null;
//            List<MediaInfoRef> mediaList = null;
//
//            PosterBaseView tempSubWnd = null;
//            mSubWndCollection = new HashSet<PosterBaseView>();
//
//            // Through the sub window list, and create the correct view for it.
//            for (SubWindowInfoRef subWndInfo : subWndList) {
//                tempSubWnd = null;
//
//                // 窗体类型和名称
//                if ((wndType = subWndInfo.getSubWindowType()) == null) {
//                    continue;
//                }
//                wndName = subWndInfo.getSubWindowName();
//
//                touch=subWndInfo.getTouch();
//                Log.i("jialei","loadNewProgram.touch:"+touch);
//                // 窗体位置
//                xPos = subWndInfo.getXPos();
//                yPos = subWndInfo.getYPos();
//                width = subWndInfo.getWidth();
//                height = subWndInfo.getHeight();
//
//                // 素材
//                mediaList = subWndInfo.getSubWndMediaList();
//
//                // 创建窗口
//                if (wndType.contains("Main") || wndType.contains("StandbyScreen")) {
//                    tempSubWnd = new MultiMediaView(this, true);
//                } else if (wndType.contains("Background")) {
//                    // 背景图片
//                    if (mediaList != null && mediaList.size() > 0 && "File".equals(mediaList.get(0).source)) {
//                        mBgImgInfo = mediaList.get(0);
////                        setWindowBackgroud();
//                    }
//                    continue;
//                } else if (wndType.contains("Image") || wndType.contains("Weather")) {
//                    tempSubWnd = new MultiMediaView(this);
//                } else if (wndType.contains("Audio")) {
//                    tempSubWnd = new AudioView(this);
//                } else if (wndType.contains("Scroll")) {
//                    tempSubWnd = new MarqueeView(this);
//                } else if (wndType.contains("Clock")) {
//                    tempSubWnd = new DateTimeView(this);
//                } else if (wndType.contains("Gallery")) {
//                    tempSubWnd = new GalleryView(this);
//                } else if (wndType.contains("Timer")) {
//                    tempSubWnd = new TimerView(this);
//                }
//
//                // 设置窗口参数，并添加
//                if (tempSubWnd != null) {
//                    tempSubWnd.setViewTouch(touch);
//                    tempSubWnd.setViewName(wndName);
//                    tempSubWnd.setViewType(wndType);
//                    tempSubWnd.setMediaList(mediaList);
//                    tempSubWnd.setViewPosition(xPos, yPos);
//                    tempSubWnd.setViewSize(width, height);
//                    mMainLayout.addView(tempSubWnd);
//                    mSubWndCollection.add(tempSubWnd);
//                }
//            }
//        }
//
//        if (mSubWndCollection != null) {
//            for (PosterBaseView subWnd : mSubWndCollection) {
//                subWnd.startWork();
//            }
//        }
//    }


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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

switch (event.getAction()){
    case MotionEvent.ACTION_DOWN:
        mouseX=event.getX();
        mouseY=event.getY();
        break;
    case MotionEvent.ACTION_UP:
        if((event.getX()-mouseX)>200|(mouseX-event.getX())>200){
            start(PowerOnOffFragment.newInstance(this));
        }

        break;
    case MotionEvent.ACTION_MOVE:

        break;

}



        return super.onTouchEvent(event);
    }
}
