package com.example.screenmanagertest.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.fragment.NewLoadFragment;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;
import com.example.screenmanagertest.screenmanager.ScreenManager;
import com.example.screenmanagertest.screenmanager.SubWindowInfoRef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017-08-01.
 */

public class Window extends PosterBaseView {
    private FrameLayout fl = null;
    private ScreenManager screenManager = null;

    private ArrayList<SubWindowInfoRef> loadList = null;
    private Context context = null;
    private Set<PosterBaseView> mSubWndCollection = null; // 屏幕布局信息
    private boolean misFirst = false;

    public Window(Context context, boolean issun) {
        super(context);
        this.context = context;
        setIssun(issun);
    }
//    public Window(Context context)
//    {
//        super(context);
//        initView(context);
//    }


    public Window(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Window(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        Log.i("jialei", "Window.initView");
        this.context = context;
        fl = this;

        // 启动屏幕管理线程
        if (screenManager == null) {
//                if(getSmallLayout()!=null){
            Log.i("jialei", "new ScreenManager" + context + ":" + this + ":" + getSmallLayout());
            screenManager = new ScreenManager(context, this, getSmallLayout());
//                    screenManager=new ScreenManager(context,this,"1593.xml");
            screenManager.startRun();
//                }else{
//                    Log.i("jialei", "ScreenManager.getViewLayout() == null");
//                }
            Log.i("jialei", "ScreenManager.getInstance() == null");
        } else {
            Log.i("jialei", "ScreenManager.getInstance() != null");
            screenManager.startRun();
        }

    }

    public void loadNewProgram2(ArrayList<SubWindowInfoRef> subWndList) {
        Log.i("jialei", "Window.loadNewProgram2");
        this.setBackgroundColor(Color.BLACK);
        if (subWndList != null) {
            loadNewProgramin(subWndList);
        } else {
            Log.i("jialei", "NewLoadFragment.loadList=null");
        }
    }

    // 加载新节目
    public void loadNewProgram(ArrayList<SubWindowInfoRef> subWndList) {
        Log.i("jialei", "Window.loadNewProgram");
        this.setBackgroundColor(Color.BLACK);
        if (subWndList != null) {
            loadNewProgramin(subWndList);
        } else {
            Log.i("jialei", "NewLoadFragment.loadList=null");
        }
    }

    @Override
    public void onViewDestroy() {

    }

    @Override
    public void onViewPause() {

    }

    @Override
    public void onViewResume() {

    }

    @Override
    public void startWork() {
        Log.i("jialei", "Window" + getSmallLayout() + ":" + getViewTouch() + ":" + getX());
        initView(context);
    }

    @Override
    public void stopWork() {
        if (mSubWndCollection != null) {
            for (PosterBaseView subWnd : mSubWndCollection) {
                subWnd.stopWork();
            }
        }
    }

    // 加载新节目
    public void loadNewProgramin(ArrayList<SubWindowInfoRef> subWndList) {

        if (mSubWndCollection != null) {
            for (PosterBaseView subWnd : mSubWndCollection) {
                subWnd.stopWork();
            }
        }

        // Create new program windows
        if (subWndList != null) {
            Logger.i("Window number is: " + subWndList.size());

            // Clean old program
//            cleanupLayout();

            // initialize
            String touch = null;
            String viewLayout = null;

            int xPos = 0;
            int yPos = 0;
            int width = 0;
            int height = 0;
            String wndName = null;
            String wndType = null;
            List<MediaInfoRef> mediaList = null;

            PosterBaseView tempSubWnd = null;
            mSubWndCollection = new HashSet<PosterBaseView>();

            // Through the sub window list, and create the correct vie
            for (SubWindowInfoRef subWndInfo : subWndList) {
                tempSubWnd = null;

                // 窗体类型和名称
                if ((wndType = subWndInfo.getSubWindowType()) == null) {
                    continue;
                }
                wndName = subWndInfo.getSubWindowName();

                touch = subWndInfo.getTouch();
                viewLayout = subWndInfo.getLayout();
                Log.i("jialei", "loadNewProgram.touch:" + touch);
                Log.i("jialei", "loadNewProgram.viewLayout:" + viewLayout);
                // 窗体位置
                xPos = subWndInfo.getXPos();
                yPos = subWndInfo.getYPos();
                width = subWndInfo.getWidth();
                height = subWndInfo.getHeight();

                // 素材
                mediaList = subWndInfo.getSubWndMediaList();

                // 创建窗口
                if (wndType.contains("Main") || wndType.contains("StandbyScreen")) {
                    tempSubWnd = new MultiMediaView(context, true, true);
                } else if (wndType.contains("Image") || wndType.contains("Weather")) {
                    tempSubWnd = new MultiMediaView(context, true);
                } else if (wndType.contains("Audio")) {
                    tempSubWnd = new AudioView(context, true);
                } else if (wndType.contains("Scroll")) {
                    tempSubWnd = new MarqueeView(context, true);
                } else if (wndType.contains("Clock")) {
                    tempSubWnd = new DateTimeView(context, true);
                } else if (wndType.contains("Gallery")) {
                    tempSubWnd = new GalleryView(context, true);
                } else if (wndType.contains("Timer")) {
                    tempSubWnd = new TimerView(context, true);
                } else if (wndType.contains("Window")) {
                    tempSubWnd = new Window(context, true);
                }

                // 设置窗口参数，并添加
                if (tempSubWnd != null) {
                    tempSubWnd.setViewTouch(touch);
                    tempSubWnd.setSmallLayout(viewLayout);
                    tempSubWnd.setViewName(wndName);
                    tempSubWnd.setViewType(wndType);
                    tempSubWnd.setMediaList(mediaList);
                    tempSubWnd.setViewPosition((int) getX() + (int) (xPos * getWidth() / PosterApplication.getScreenWidth()), (int) getY() + yPos * getHeight() / PosterApplication.getScreenHeight());
                    tempSubWnd.setViewSize(width * getWidth() / PosterApplication.getScreenWidth(), height * getHeight() / PosterApplication.getScreenHeight());
                    this.addView(tempSubWnd);
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
}
