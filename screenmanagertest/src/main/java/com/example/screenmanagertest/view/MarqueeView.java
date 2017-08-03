/*
 * Copyright (C) 2013 poster PCE YoungSee Inc. 
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.FileUtils;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;

public class MarqueeView extends PosterBaseView
{
    private YSTextView              mTextView               = null;
    private ProgressBarView         mProgressBarView        = null;
    
    private float                   mTextLength             = 0.0f;
    private float                   mStep                   = 0.0f;
    private float                   mViewPlusTextLen        = 0.0f; 
    private float                   mViewPlusDoubleTextLen  = 0.0f; 
                                                                        
    private int                     mMoveSpeed             = 1;
    private int                     mMoveDirection         = 0;
    
    private boolean                 mIsMoving              = false;         // 是否滚动
    private UpdateThread            mUpdateThreadHandle    = null;

    private final static int        MOVE_LEFT              = 1;
    private final static int        MOVE_UP                = 2;

    private final static int        MOVE_INTERVAL          = 50;
    private final static int        LOW_SPEED_LEVEL        = 0;
    private final static int        MID_SPEED_LEVEL        = 1;
    private final static int        HIGH_SPEED_LEVEL       = 2;

    // Define message ID
    private final static int        EVENT_SHOWPROGRESSBAR   = 0x6001;
    private final static int        EVENT_MARQUEE_TEXT      = 0x6002;

    public MarqueeView(Context context,boolean issun)
    {
        super(context);
        initView(context);
        setIssun(issun);
    }
//    public MarqueeView(Context context)
//    {
//        super(context);
//        initView(context);
//    }

    public MarqueeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context)
    {
        Logger.d("Marquee View initialize......");

        // Get layout from XML file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_marquee, this);

        mProgressBarView = (ProgressBarView) findViewById(R.id.marquee_progressbar);
        mTextView = (YSTextView) findViewById(R.id.marqueetext);
        mTextView.setGravity((Gravity.LEFT | Gravity.CENTER));
    }

    @Override
    public void onViewDestroy()
    {
        cancelUpdateThread();
        cleanupMsg();
        this.removeAllViews();
    }

    @Override
    public void onViewPause()
    {
        pauseUpdateThread();
        cleanupMsg();
    }

    @Override
    public void onViewResume()
    {
        resumeUpdateThread();
    }

    @Override
    public void startWork()
    {
    	if (mMediaList == null)
        {
            Logger.i("Media list is null.");
            return;
        }
        else if (mMediaList.isEmpty())
        {
            Logger.i("No media in the list.");
            return;
        }

        startUpdateThread();
    }

    @Override
    public void stopWork()
    {
        cancelUpdateThread();
        mCurrentIdx   = -1;
        mCurrentMedia = null;
        mIsMoving = false;
    }

    private void cleanupMsg()
    {
        mHandler.removeMessages(EVENT_MARQUEE_TEXT);
        mHandler.removeMessages(EVENT_SHOWPROGRESSBAR);
    }

    private void startUpdateThread()
    {
        cancelUpdateThread();
        mUpdateThreadHandle = new UpdateThread();
        mUpdateThreadHandle.start();
    }

    private void cancelUpdateThread()
    {
        if (mUpdateThreadHandle != null)
        {
            mUpdateThreadHandle.cancel();
            mUpdateThreadHandle = null;
        }
    }

    private void pauseUpdateThread()
    {
        if (mUpdateThreadHandle != null && !mUpdateThreadHandle.isPaused())
        {
            mUpdateThreadHandle.onPause();
        }
    }

    private void resumeUpdateThread()
    {
        if (mUpdateThreadHandle != null && mUpdateThreadHandle.isPaused())
        {
            mUpdateThreadHandle.onResume();
        }
    }

    private void setScrollMode(int mode)
    {
        mMoveDirection = MOVE_LEFT;
        switch (mode)
        {
        case 3:
        case 11:
            mMoveDirection = MOVE_LEFT;
            break;
        case 13:
            mMoveDirection = MOVE_UP;
            break;
        default:
            break;
        }
    }

    private void setSpeedLevel(int nlevel)
    {
        switch (nlevel)
        {
        case LOW_SPEED_LEVEL:
            mMoveSpeed = 1;
            break;

        case MID_SPEED_LEVEL:
            mMoveSpeed = 3;
            break;

        case HIGH_SPEED_LEVEL:
            mMoveSpeed = 5;
            break;
        }
    }

    private final class UpdateThread extends Thread
    {
        private boolean mIsRun = false;
        private Object   mPauseLock  = null;
        private boolean  mPauseFlag  = false;

        public UpdateThread()
        {
            mIsRun = true;
            mPauseLock = new Object();
            mPauseFlag = false;
        }

        public void cancel()
        {
            Logger.i("Cancels the marquee thread.");
            mIsRun = false;
            this.interrupt();
        }

        public void onPause()
        {
            Logger.i("Pauses the marquee thread.");
            synchronized (mPauseLock)
            {
                mPauseFlag = true;
            }
        }

        public void onResume()
        {
            Logger.i("Resumes the marquee thread.");
            synchronized (mPauseLock)
            {
                mPauseFlag = false;
                mPauseLock.notify();
            }
        }

        public boolean isPaused()
        {
            return mPauseFlag;
        }

        @Override
        public void run()
        {
            Logger.i("New scroll text thread, id is: " + currentThread().getId());

            MediaInfoRef media = null;

            while (mIsRun)
            {
                try
                {
                    synchronized (mPauseLock)
                    {
                        if (mPauseFlag)
                        {
                            mPauseLock.wait();
                        }
                    }

                    if (mMediaList == null)
                    {
                        Logger.i("mMediaList is null, thread exit.");
                        return;
                    }
                    else if (mMediaList.isEmpty())
                    {
                        Logger.i("No text info in the list, thread exit.");
                        return;
                    }
                    else if ((mCurrentIdx < -1) || (mCurrentIdx >= mMediaList.size()))
                    {
                        Logger.i("mCurrentIdx (" + mCurrentIdx + ") is invalid, thread exit.");
                        return;
                    }

                    if (mCurrentIdx == -1 && noMediaValid())
                    {
                        showProgressBar();
                    }

                    if (!mIsMoving)
                    {
                        media = findNextOrSyncMedia();

                        if (media == null)
                        {
                            Logger.i("No media can be found, current index is: " + mCurrentIdx);
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        }
                        else if (FileUtils.mediaIsFile(media) && !FileUtils.isExist(media.filePath))
                        {
                            Logger.i(media.filePath + " didn't exist, skip it.");
//                            downloadMedia(media);
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        }
                        else
                        {
                            mCurrentMedia = media;
                            showMarqueeTextView();
                            mIsMoving = initScrollViewParam(mCurrentMedia);
                            Thread.sleep(MOVE_INTERVAL);
                            continue;
                        }
                    }
                    else
                    {
                        movingText(mMoveDirection);
                        Thread.sleep(MOVE_INTERVAL);
                        mIsMoving = !isMoveFinised();
                        continue;
                    }
                }
                catch (InterruptedException e)
                {
                    Logger.i("Scroll text Thread sleep over, and safe exit, the Thread id is: "
                            + currentThread().getId());
                    return;
                }
                catch (Exception e)
                {
                    Logger.e("Scroll text Thread Catch a error, id is: " + currentThread().getId());
                    e.printStackTrace();
                    mIsMoving = false;
                }
            }

            Logger.i("Scroll text Thread is safe Terminate, id is: " + currentThread().getId());
        }

        private void movingText(int nDirection)
        {
            if (mTextView != null)
            {
                switch (nDirection)
                {
                case MOVE_LEFT:
                    mStep += mMoveSpeed;
                    mTextView.setXPos(mViewPlusTextLen - mStep);
                    mTextView.postInvalidate();
                    break;

                case MOVE_UP:
                    mStep += mMoveSpeed;
                    mTextView.setYPos(mViewPlusTextLen - mStep);
                    mTextView.postInvalidate();
                    break;

                default:
                    // Invalid direction, don't start the scroll
                    break;
                }
            }
        }

        private boolean initScrollViewParam(MediaInfoRef mediaInfo) throws InterruptedException
        {
            boolean ret = false;
            if (mTextView != null)
            {
                // 获取文字内容
                String message = getText(mediaInfo);
                if (message != null)
                {
                    // 设置移动方向和速率
                    setScrollMode(mediaInfo.mode);
                    setSpeedLevel(mediaInfo.speed);

                    // 获取字体参数，并创建画笔
                    Paint paint = new Paint();
//                    paint.setColor(getFontColor(mediaInfo)); // 颜色
                    //TODO
                    paint.setColor(Color.WHITE); // 颜色
                    paint.setTextSize(getFontSize(mediaInfo)); // 字号
                    paint.setAlpha(0xff); // 字体不透明
//                    paint.setTypeface(getFont(mediaInfo)); // 字体
                    paint.setAntiAlias(true); // 去除锯齿
                    paint.setFilterBitmap(true); // 对位图进行滤波处理


                    // 初始化参数
                    float xPos = 0.0f;
                    float yPos = 0.0f;
                    int nViewWidth = mediaInfo.containerwidth;
                    int nViewHeight = mediaInfo.containerheight;
                    ArrayList<String> textList = null;
                    switch (mMoveDirection)
                    {
                    case MOVE_LEFT:
                    {
                        String textMsg = StringFilter(message).replaceAll("\\n+", "");
                        textList = new ArrayList<String>();
                        textList.add(textMsg);
                        mTextLength = (int) paint.measureText(textMsg);
                        mStep = mTextLength;
                        mViewPlusTextLen = nViewWidth + mTextLength;
                        mViewPlusDoubleTextLen = nViewWidth + mTextLength * 2;
                        xPos = nViewWidth;
                        yPos = paint.getTextSize() + mTextView.getPaddingTop();
                        mTextView.setViewAttribute(textList, xPos, yPos, paint); // 设定初始值
                        mTextView.postInvalidate();
                        ret = true;
                    }
                        break;

                    case MOVE_UP:
                    {
                        textList = autoSplit(message, paint, nViewWidth);  // 自动分行
                        FontMetrics fm = paint.getFontMetrics();
                        float fontHeight = (float)Math.ceil(fm.descent - fm.ascent) + fm.leading; // 每行高度
                        mTextLength = textList.size() * fontHeight;
                        mStep = mTextLength;
                        mViewPlusTextLen = nViewHeight + mTextLength;
                        mViewPlusDoubleTextLen = nViewHeight + mTextLength * 2;
                        xPos = mTextView.getPaddingLeft();
                        yPos = nViewHeight;
                        mTextView.setViewAttribute(textList, xPos, yPos, paint); // 设定初始值
                        mTextView.postInvalidate();
                        ret = true;
                    }
                        break;
                    
                    default:
                        // Invalid direction, don't start the scroll
                        break;
                    }
                }
            }
            
            return ret;
        }
        
        private boolean isMoveFinised()
        {
            if (mStep >= mViewPlusDoubleTextLen)
            {
                try
                {
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }
    
    private void showProgressBar()
    {
        if (mProgressBarView != null && mProgressBarView.getVisibility() != View.VISIBLE)
        {
            mHandler.sendEmptyMessage(EVENT_SHOWPROGRESSBAR);
        }
    }
    
    private void showMarqueeTextView()
    {
        if (mTextView != null && mTextView.getVisibility() != View.VISIBLE)
        {
            mHandler.sendEmptyMessage(EVENT_MARQUEE_TEXT);
        }
    }
    
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {  
            case EVENT_SHOWPROGRESSBAR:
                mTextView.setVisibility(View.GONE);
                mProgressBarView.setVisibility(View.VISIBLE);
                return;
            
            case EVENT_MARQUEE_TEXT:
                mProgressBarView.setVisibility(View.GONE);
                mTextView.setVisibility(View.VISIBLE);
                return;
            }
            
            super.handleMessage(msg);
        }
    };
}
