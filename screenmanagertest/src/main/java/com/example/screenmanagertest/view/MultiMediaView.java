/*
 * Copyright (C) 2015 poster PCE YoungSee Inc. 
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */
package com.example.screenmanagertest.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.screenmanagertest.MainActivity;
import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.FileUtils;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.gifdecode.GifAction;
import com.example.screenmanagertest.gifdecode.GifDecodeInfo;
import com.example.screenmanagertest.gifdecode.GifDecoder;
import com.example.screenmanagertest.gifdecode.GifFrame;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;
import com.example.screenmanagertest.screenmanager.ScreenManager;

public class MultiMediaView extends PosterBaseView {
    private Intent intent = null;
    private Context context = null;
    private final static int EVENT_BASE = 0x9000;
    private final static int EVENT_SHOWPROGBAR = EVENT_BASE + 0;
    private final static int EVENT_SHOWSURFACEVIEW = EVENT_BASE + 1;
    private final static int EVENT_PLAYVIDEO = EVENT_BASE + 2;
    private final static int EVENT_SHOWPICTURE = EVENT_BASE + 3;
    private final static int EVENT_HIDE_CONTROLER = EVENT_BASE + 4;
    private final static int EVENT_PROGRESS_CHANGED = EVENT_BASE + 5;

    private final static int SHOWTYPE_NONE = 0;
    private final static int SHOWTYPE_PROGBAR = 1;
    private final static int SHOWTYPE_VIDEO = 2;
    private final static int SHOWTYPE_PICTURE = 3;

    private final static long DEFAULT_MEDIA_DURATION = 1000;
    private final static long DEFAULT_ANIMATION_DURATION = 1000;
    private final static int DEFAULT_VOLUME_VALUE = 3;
    private final static int CONTROLBAR_STAY_TIME = 5000;

    private int PICTURE_DURING = 1000;

    // Define special effects for picture shown
    private static final int NONE = 0;
    private static final int MOVE_LEFTTORIGHT = 1;
    private static final int MOVE_RIGHTTOLEFT = 2;
    private static final int MOVE_TOPTOBOTTOM = 3;
    private static final int MOVE_BOTTOMTOTOP = 4;
    private static final int MOVE_LEFTTOPTORIGHTBOTTOM = 5;
    private static final int MOVE_RIGHTTOPTOLEFTBOTTON = 6;
    private static final int INSIDETOSIDE = 7;
    private static final int SIDETOINSIDE = 8;
    private static final int LAND_LOUVER = 9;
    private static final int VERT_LOUBER = 10;
    private static final int LAND_PUSH = 11;
    private static final int VERT_PUSH = 12;
    private static final int RANDOM = 255;

    private ProgressBarView mProgressbar = null;
    private SurfaceView mSurfaceView = null;
    private ImageSwitcher mImageSwitcher = null;

    private ControlView mControlView = null;
    private PopupWindow mControlWindow = null;
    private SoundView mSoundView = null;
    private PopupWindow mSoundWindow = null;

    private int mCurrentVolume = 0;
    private int mControlWidth = 0;
    private int mControlHeight = 0;
    private int mControlXPos = 0;
    private int mControlYPos = 0;

    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mMediaPlayer = null;
    private MediaMetadataRetriever mMediaRetriever = null;
    private MyThread mMyThread = null;

    private int mMediaPosition = -1;
    private boolean mSurfaceIsReady = false;
    private boolean mIsPlayingVideo = false;
    private int mCurrentShowType = SHOWTYPE_NONE;

    // 保存所有的GIF decoder信息
    private GifDecoder mGifDecoder = null;
    private GifDecodeInfo mDecodeInfo = null;
    private HashMap<String, GifDecodeInfo> mGifDecodeInfoMap = null;

    public MultiMediaView(Context context) {
        super(context);
        initView(context, false);
    }

    public MultiMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, false);
    }

    public MultiMediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, false);
    }

    public MultiMediaView(Context context, boolean hasSurface) {
        super(context);
        initView(context, hasSurface);
    }

    private void initView(Context context, boolean isShowSurface) {
        this.context = context;
        Logger.d("[" + mViewName + "] MultiMedia View initialize......");

        // Get layout from XML file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_multimedia, this);

        // Get widgets from XML file
        mProgressbar = (ProgressBarView) findViewById(R.id.multimedia_progressbar);
        mSurfaceView = (SurfaceView) findViewById(R.id.multimedia_surfaceview);
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.multimedia_imgswitcher);

        // Init surfaceview parameters
        //mSurfaceView.setZOrderOnTop(true);  // surfaceView is always on TOP
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(new SurfaceHolderCallBack());
        //mSurfaceView.setFocusable(true);
//        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.setFocusableInTouchMode(true);
        if (isShowSurface) {
            doShowSurfaceView();
        }

        // Init imageswithcher parameters
        ViewFactory factory = new ViewFactory() {
            @Override
            public View makeView() {
                ImageView iv = new ImageView(mContext);
                iv.setScaleType(ScaleType.FIT_XY);
                iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                return iv;
            }
        };
        mImageSwitcher.setFactory(factory);

        // Create Sound Window
        mSoundView = new SoundView(mContext);
        mSoundView.setOnVolumeChangeListener(new SoundView.OnVolumeChangedListener() {
            @Override
            public void setYourVolume(int index) {
                mHandler.removeMessages(EVENT_HIDE_CONTROLER);
                updateVolume(index);
                hideControllerDelay();
            }
        });
        mSoundWindow = new PopupWindow(mSoundView);
        mSoundWindow.setOutsideTouchable(true);
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = (mCurrentVolume > 0) ? mCurrentVolume : 0;

        // Create Control Window
        mControlView = new ControlView(mContext);
        if (mCurrentVolume <= 0) {
            mControlView.setVolumeButtonImage(R.drawable.sounddisable);
        }
        mControlView.setOnControlChangedListener(new ControlView.OnControlChangedListener() {
            @Override
            public void onSeekBarProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                if (fromUser && mMediaPlayer != null) {
                    mMediaPlayer.seekTo(progress);
                    if (!mMediaPlayer.isPlaying()) {
                        mMediaPlayer.start();
                        resumeUpdateThread();
                    }
                }
            }

            @Override
            public void onSeekBarStartTrackingTouch(SeekBar arg0) {
                mHandler.removeMessages(EVENT_HIDE_CONTROLER);
            }

            @Override
            public void onSeekBarStopTrackingTouch(SeekBar mSeekBar) {
                hideControllerDelay();
            }

            @Override
            public void onPlayButtonClick(View v) {
                mHandler.removeMessages(EVENT_HIDE_CONTROLER);
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        pauseUpdateThread();
                        mMediaPlayer.pause();
                        mControlView.setPlayButtonImage(R.drawable.play);
                    } else {
                        mMediaPlayer.start();
                        mControlView.setPlayButtonImage(R.drawable.pause);
                        resumeUpdateThread();
                        hideControllerDelay();
                    }
                }
            }

            @Override
            public void onVolumeButtonClick(View v) {
                mHandler.removeMessages(EVENT_HIDE_CONTROLER);
                if (mSoundWindow.isShowing()) {
                    hideSoundView();
                } else {
                    showSoundView();
                }
                hideControllerDelay();
            }

            @Override
            public void onVolumeButtonLongPress(View v) {
                mHandler.removeMessages(EVENT_HIDE_CONTROLER);

                if (mSoundWindow != null && mSoundWindow.isShowing()) {
                    hideSoundView();
                }

                // Update the system Volume
                if (mCurrentVolume > 0) {
                    // change volume to Slient
                    updateVolume(0);
                    mControlView.setVolumeButtonImage(R.drawable.sounddisable);
                } else {
                    // change volume to Default
                    updateVolume(DEFAULT_VOLUME_VALUE);
                    mControlView.setVolumeButtonImage(R.drawable.soundenable);
                }

                hideControllerDelay();
            }
        });
        mControlWindow = new PopupWindow(mControlView.getViewScreen());
        mControlWindow.setOutsideTouchable(true);

        // defined the hover listener
        this.setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                if ((mMediaPlayer != null &&
                        mMediaPlayer.isPlaying() &&
                        (mXPos + event.getX()) > mControlXPos) &&
                        (mYPos + event.getY()) > mControlYPos) {
                    showController();
                }
                return false;
            }
        });
    }

    private final class SurfaceHolderCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Logger.i("Surface is changed, holder = " + holder);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Logger.i("Surface is created, holder = " + holder);
            mSurfaceIsReady = true;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Logger.i("Surface is destroyed, holder = " + holder);
            mSurfaceIsReady = false;
            releaseMediaPlayer();
        }
    }

    private MediaPlayer.OnPreparedListener mMediaPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Logger.i("Media player prepared.");

            if (mMediaPosition != -1) {
                mMediaPlayer.seekTo(mMediaPosition);
                mMediaPosition = -1;
            }
            mMediaPlayer.start();
            mIsPlayingVideo = true;

            mControlView.setPlayButtonImage(R.drawable.pause);
            mControlView.setSeekBarMax(mMediaPlayer.getDuration());
        }
    };

    private MediaPlayer.OnCompletionListener mMediaPlayerCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Logger.i("Media player completion.");

            hideController();
            if (mCurrentMedia != null) {
                mCurrentMedia.playedtimes++;
            }
            mIsPlayingVideo = false;
        }
    };

    private MediaPlayer.OnErrorListener mMediaPlayerErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Logger.i("Media player error.");

            hideController();
            releaseMediaPlayer();
            if (mCurrentMedia != null) {
                mCurrentMedia.playedtimes++;
            }
            mIsPlayingVideo = false;
            return true;
        }
    };

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            try {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mMediaRetriever != null) {
            mMediaRetriever.release();
        }
    }

    @Override
    public void onViewPause() {
        pauseUpdateThread();
        cleanupMsg();

        if (mIsPlayingVideo) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPosition = mMediaPlayer.getCurrentPosition();
            }
            releaseMediaPlayer();
            doHideAllViews();
        }
    }

    @Override
    public void onViewResume() {
        if (mIsPlayingVideo) {
            if (mCurrentMedia != null && FileUtils.mediaIsVideo(mCurrentMedia)) {
                doShowSurfaceView();
                playVideo(mCurrentMedia);
            } else {
                mIsPlayingVideo = false;
            }
        }

        resumeUpdateThread();
    }

    @Override
    public void onViewDestroy() {
        cancelUpdateThread();
        cleanupMsg();
        hideController();
        releaseMediaPlayer();
        clearImageView();
        this.removeAllViews();
    }

    private void cleanupMsg() {
        mHandler.removeMessages(EVENT_SHOWPROGBAR);
        mHandler.removeMessages(EVENT_SHOWSURFACEVIEW);
        mHandler.removeMessages(EVENT_PLAYVIDEO);
        mHandler.removeMessages(EVENT_SHOWPICTURE);
        mHandler.removeMessages(EVENT_HIDE_CONTROLER);
        mHandler.removeMessages(EVENT_PROGRESS_CHANGED);
    }

    @Override
    public void startWork() {
        Log.i("jialei", "startwork");
        if (mMediaList == null) {
            Logger.i("Media list is null.");
            if ("Main".equals(mViewType)) {
//                ScreenManager.getInstance().setPrgFinishedFlag(true);
                Logger.i("No media info for main window, Program play finished.");
            }
            return;
        } else if (mMediaList.isEmpty()) {
            Logger.i("No media in the list.");
            if ("Main".equals(mViewType)) {
//                ScreenManager.getInstance().setPrgFinishedFlag(true);
                Logger.i("No media info for main window, Program play finished.");
            }
            return;
        }
        if (getViewTouch() != null) {
            mImageSwitcher.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent();
                    intent.setAction("touchBroadcast");
                    intent.putExtra("xml", getViewTouch());
                    context.sendBroadcast(intent);
                }
            });
        }
        startUpdateThread();
    }

    @Override
    public void stopWork() {
        cancelUpdateThread();
        cleanupMsg();
        releaseMediaPlayer();
        doHideAllViews();
        if (mMediaList != null) {
            mMediaList.clear();
            mMediaList = null;
        }

        mCurrentIdx = -1;
        mCurrentMedia = null;

        mMediaPosition = -1;
        mIsPlayingVideo = false;
        mSurfaceIsReady = false;
    }

    private void startUpdateThread() {
        cancelUpdateThread();
        mMyThread = new MyThread("multiMediaThread");
        mMyThread.start();
    }

    private void cancelUpdateThread() {
        if (mMyThread != null) {
            mMyThread.cancel();
            mMyThread = null;
        }
    }

    private void pauseUpdateThread() {
        if (mMyThread != null && !mMyThread.isPaused()) {
            mMyThread.onPause();
        }
    }

    private void resumeUpdateThread() {
        if (mMyThread != null && mMyThread.isPaused()) {
            mMyThread.onResume();
        }
    }

    private void updateVolume(int index) {
        // Get System Volume Information
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, AudioManager.FLAG_PLAY_SOUND);
            mCurrentVolume = index;
        }
    }

    private void hideSoundView() {
        if (mSoundWindow != null && mSoundWindow.isShowing()) {
            mSoundWindow.dismiss();
        }
    }

    private void showSoundView() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mSoundWindow != null && !mSoundWindow.isShowing()) {
            int xPos = mWidth - SoundView.MY_WIDTH - 10;
            int yPos = mHeight - mControlHeight - SoundView.MY_HEIGHT;
            mSoundWindow.showAtLocation(mSurfaceView, Gravity.NO_GRAVITY, xPos, yPos);
            mSoundWindow.update(xPos, yPos, SoundView.MY_WIDTH, SoundView.MY_HEIGHT);
        }
    }

    private void hideController() {
        mHandler.removeMessages(EVENT_HIDE_CONTROLER);

        hideSoundView();

        if (mControlWindow != null && mControlWindow.isShowing()) {
            mControlWindow.dismiss();
        }

        mHandler.removeMessages(EVENT_PROGRESS_CHANGED);
    }

    private void showController() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mControlWindow != null && !mControlWindow.isShowing()) {
            // initialize control bar size and position
            mControlWidth = mWidth;
            mControlHeight = mHeight / 6;
            mControlXPos = mXPos;
            mControlYPos = mYPos + mHeight - mControlHeight;

            mControlWindow.showAtLocation(mSurfaceView, Gravity.NO_GRAVITY, mControlXPos, mControlYPos);
            mControlWindow.update(mControlXPos, mControlYPos, mControlWidth, mControlHeight);
            hideControllerDelay();
        }
    }

    private void hideControllerDelay() {
        mHandler.sendEmptyMessageDelayed(EVENT_HIDE_CONTROLER, CONTROLBAR_STAY_TIME);
    }

    private void showProgressBar() {
        mHandler.sendEmptyMessage(EVENT_SHOWPROGBAR);
    }

    private void showSurfaceView() {
        mHandler.sendEmptyMessage(EVENT_SHOWSURFACEVIEW);
    }

    private void playVideo(MediaInfoRef media) {
        Logger.i("Play video '" + media.filePath + "'.");
        // sync play: start load video

        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_PLAYVIDEO;
        msg.obj = media;
        msg.sendToTarget();
    }

    private void showPicture(Bitmap pic, int mode) {
        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_SHOWPICTURE;
        msg.obj = pic;
        msg.arg1 = mode;
        msg.sendToTarget();
    }

    private void playImage(MediaInfoRef media) throws InterruptedException {
//        Logger.i("Play image '" + media.filePath + "'.");

        boolean bIsUseCache = mViewType.contains("Weather") ? false : true;
        Bitmap img = getBitMap(media, bIsUseCache);


        showPicture(img, media.mode);
    }

    private void playText(MediaInfoRef media) throws InterruptedException {
        Logger.i("Play text '" + media.filePath + "'.");

        String strText = getText(media);
        if (TextUtils.isEmpty(strText)) {
            Logger.i("Text '" + media.filePath + "' is empty.");
            return;
        }

        int fSize = getFontSize(media);
//        int fColor = getFontColor(media);
//        Typeface font = getFont(media);
        int durationPerPage = media.durationPerPage;
        int nWidth = media.containerwidth;
        int nHeight = media.containerheight;

        // 创建画笔
        Paint paint = new Paint();
        paint.setTextSize(fSize); // 字号
//        paint.setColor(fColor); // 颜色
        paint.setAlpha(0xff); // 字体不透明
//        paint.setTypeface(font); // 字体
        paint.setAntiAlias(true); // 去除锯齿
        paint.setFilterBitmap(true); // 对位图进行滤波处理

        // 计算页数
        FontMetrics fm = paint.getFontMetrics();
        float lineHeight = (float) Math.ceil(fm.descent - fm.ascent); // 每行高度
        int linesPerPage = (int) (nHeight / (lineHeight + fm.leading)); // 每一页的行数
        ArrayList<String> textList = autoSplit(strText, paint, (nWidth - 10)); // 自动分行
        int lineCount = textList.size(); // 总行数
        int pages = 1; // 总页数
        if ((lineCount % linesPerPage) == 0) {
            pages = lineCount / linesPerPage;
        } else {
            pages = lineCount / linesPerPage + 1;
        }

        // 创建canvas
        Bitmap bmp = Bitmap.createBitmap(nWidth, nHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bmp);


        // 画文本
        float x = 5;
        float y = lineHeight;
        int nIdx = 0;
        for (int i = 0; i < pages; i++) {
            x = 5;
            y = lineHeight;
            nIdx = i * linesPerPage; // 页的起始行

            if (nIdx >= textList.size() || TextUtils.isEmpty(textList.get(nIdx))) {
                continue; // 空白页则跳过
            }

            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
            for (int j = 0; j < linesPerPage; j++) {
                nIdx = i * linesPerPage + j;
                if (nIdx >= textList.size()) {
                    break; // 最后一页不满一屏的情况
                } else if (textList.get(nIdx) != null) {
                    canvas.drawText(textList.get(nIdx), x, y, paint);
                    y = y + lineHeight + fm.leading; // (字高+行间距)
                }
            }

            // 显示文字
            showPicture(bmp, media.mode);

            // 等待显示下一页
            Thread.sleep(durationPerPage);
        }
    }

    private final class MyThread extends Thread {
        private boolean mIsRun = false;
        private Object mPauseLock = new Object();
        private boolean mPauseFlag = false;

        public MyThread(String threadName) {
            super(threadName);
            mIsRun = true;
        }

        public void cancel() {
            Logger.i("Cancel the multimedia thread.");
            mIsRun = false;
            this.interrupt();
        }

        public void onPause() {
            Logger.i("Pause the multimedia thread.");
            synchronized (mPauseLock) {
                mPauseFlag = true;
            }
        }

        public void onResume() {
            Logger.i("Resume the multimedia thread.");
            synchronized (mPauseLock) {
                mPauseFlag = false;
                mPauseLock.notify();
            }
        }

        public boolean isPaused() {
            return mPauseFlag;
        }

        private void updateVideoProgress() {
            int nCurrentPrg = mMediaPlayer.getCurrentPosition();
            int nTime = nCurrentPrg / 1000;
            int minute = nTime / 60;
            int hour = minute / 60;
            int second = nTime % 60;
            minute %= 60;
            StringBuilder sb = new StringBuilder();
            sb.append(hour).append(":");
            sb.append(minute).append(":");
            sb.append(second);
            String strPlayedTime = sb.toString();

            nTime = mMediaPlayer.getDuration() / 1000;
            minute = nTime / 60;
            hour = minute / 60;
            second = nTime % 60;
            minute %= 60;
            sb.setLength(0);
            sb.append(hour).append(":");
            sb.append(minute).append(":");
            sb.append(second);
            String strDurationTime = sb.toString();

            /***********************************************************
             * Build the message body, and send Message to UI thread, *
             * and inform UI thread to update the view content *
             ***********************************************************/
            Bundle bundle = new Bundle();
            bundle.putInt("CurrentPostion", nCurrentPrg);
            bundle.putString("PlayedTime", strPlayedTime);
            bundle.putString("DurationTime", strDurationTime);

            Message msg = mHandler.obtainMessage();
            msg.what = EVENT_PROGRESS_CHANGED;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        @Override
        public void run() {
            Logger.i("New multimedia thread, id is: " + currentThread().getId());

            int nPos = 0;
            long lastSendSyncInfoTime = 0;
            MediaInfoRef media = null;

            while (mIsRun) {
                try {
                    synchronized (mPauseLock) {
                        if (mPauseFlag) {
                            mPauseLock.wait();
                        }
                    }

                    if (mMediaList == null) {
                        Logger.i("Media list is null, thread exit.");
                        return;
                    } else if (mMediaList.isEmpty()) {
                        Logger.i("No media in the list, thread exit.");
                        return;
                    } else if ((mCurrentIdx < -1) || (mCurrentIdx >= mMediaList.size())) {
                        Logger.i("mCurrentIdx (" + mCurrentIdx + ") is invalid, thread exit.");
                        return;
                    }

                    if (mCurrentIdx == -1 && (mCurrentShowType != SHOWTYPE_PROGBAR) && noMediaValid()) {
                        showProgressBar();
                    }

                    if (!mIsPlayingVideo) {
                        media = findNextOrSyncMedia();

                        if (media == null) {
                            Logger.i("No media can be found, current index is: " + mCurrentIdx);
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        } else if (FileUtils.mediaIsFile(media) && !FileUtils.isExist(media.filePath) &&
                                !media.filePath.equals(PosterApplication.getStandbyScreenImgPath())) {
                            Logger.i(media.filePath + " didn't exist, skip it.");
//                            downloadMedia(media);
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        }

                        if (FileUtils.mediaIsVideo(media)) {
                            mCurrentMedia = media;


                            mIsPlayingVideo = true;
                            if (mViewType.contains("Main")) {
                                informStopAudio();
                            }
                            if (mCurrentShowType != SHOWTYPE_VIDEO) {
                                showSurfaceView();
                                while (!mSurfaceIsReady) {
                                    Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                                }
                            }
                            playVideo(mCurrentMedia);
                        } else if (FileUtils.mediaIsGifFile(media)) {
                            mCurrentMedia = media;


                            if (mViewType.contains("Main")) {
                                informStartAudio();
                            }

                            if (mGifDecodeInfoMap == null) {
                                mGifDecodeInfoMap = new HashMap<String, GifDecodeInfo>();
                            }

                            mCurrentMedia.playedtimes++;   // if media has error, the played times will increase.
                            continue;
                        } else if (FileUtils.mediaIsPicFromFile(media) || FileUtils.mediaIsPicFromNet(media)) {
                            if (mCurrentMedia != null &&
                                    FileUtils.mediaIsFile(media) &&
                                    mCurrentMedia.filePath.equals(media.filePath)) {
                                Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                                continue;
                            }

                            mCurrentMedia = media;


                            if (mViewType.contains("Main")) {
                                informStartAudio();
                            }
                            if (media.filePath.equals(PosterApplication.getStandbyScreenImgPath()) && !FileUtils.isExist(media.filePath)) {
                                // 如果待机画面不存在，则播放默认的待机画面
//                        		Bitmap img = PosterApplication.getInstance().getDefaultScreenImg();
                                Resources resources = mContext.getResources();
                                Drawable drawable = resources.getDrawable(R.drawable.del);
                                BitmapDrawable bd = (BitmapDrawable) drawable;
                                Bitmap img = bd.getBitmap();
                                showPicture(img, media.mode);
                            } else {
                                playImage(mCurrentMedia);
                            }
                            mCurrentMedia.playedtimes++;
                            //TODO
                            sleep(PICTURE_DURING);
                            continue;
                        } else if (FileUtils.mediaIsTextFromFile(media) || FileUtils.mediaIsTextFromNet(media)) {
                            mCurrentMedia = media;


                            if (mViewType.contains("Main")) {
                                informStartAudio();
                            }
                            playText(mCurrentMedia);
                            media.playedtimes++;
                            continue;
                        }

                    } else {

                        if (mMediaPlayer != null && mMediaPlayer.isPlaying() && mControlWindow.isShowing()) {
                            updateVideoProgress();
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        }
                        Thread.sleep(DEFAULT_THREAD_PERIOD);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            terminateGifDecode();
            Logger.i("Multimedia thread is safely terminated, id is: " + currentThread().getId());
        }
    }

    private void informStartAudio() {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).startAudio();
        }
    }

    private void informStopAudio() {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).stopAudio();
        }
    }

    private void doShowProgressBar() {
        if (mCurrentShowType != SHOWTYPE_PROGBAR) {
            mProgressbar.setVisibility(View.VISIBLE);
            mSurfaceView.setVisibility(View.GONE);
            mImageSwitcher.setVisibility(View.GONE);
            mCurrentShowType = SHOWTYPE_PROGBAR;
        }
    }

    private void stopVideo() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    private void doHideAllViews() {
        hideController();
        mProgressbar.setVisibility(View.GONE);
        mSurfaceView.setVisibility(View.GONE);
        mImageSwitcher.setVisibility(View.GONE);
        mCurrentShowType = SHOWTYPE_NONE;
    }

    private void doShowSurfaceView() {
        mProgressbar.setVisibility(View.GONE);
        mSurfaceView.setVisibility(View.VISIBLE);
        mImageSwitcher.setVisibility(View.GONE);
        mCurrentShowType = SHOWTYPE_VIDEO;
    }

    private void doPlayVideo(MediaInfoRef media) {
        // Tell the music playback service to pause
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mMediaPlayerPreparedListener);
            mMediaPlayer.setOnCompletionListener(mMediaPlayerCompletionListener);
            mMediaPlayer.setOnErrorListener(mMediaPlayerErrorListener);
        }

        try {
            stopVideo();
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setDataSource(media.filePath);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            // 截屏用(流媒体不支持截屏)
            if (FileUtils.mediaIsFile(media) && FileUtils.isExist(media.filePath)) {
                mMediaRetriever = new MediaMetadataRetriever();
                mMediaRetriever.setDataSource(media.filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mIsPlayingVideo = false;
        }
    }

    private void updateAnimation(int mode) {
        Animation inanimation;
        switch (mode) {
            case NONE:
                mImageSwitcher.setInAnimation(null);
                mImageSwitcher.setOutAnimation(null);

                break;
            case LAND_PUSH:
            case MOVE_LEFTTORIGHT:
                inanimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case MOVE_RIGHTTOLEFT:
                inanimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case VERT_PUSH:
            case MOVE_TOPTOBOTTOM:
                inanimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case MOVE_BOTTOMTOTOP:
                inanimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case MOVE_LEFTTOPTORIGHTBOTTOM:
                inanimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case MOVE_RIGHTTOPTOLEFTBOTTON:
                inanimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case INSIDETOSIDE:
                inanimation = new ScaleAnimation(0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case SIDETOINSIDE:
                inanimation = new ScaleAnimation(3.0f, 1.0f, 3.0f, 1.0f, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
                inanimation.setDuration(DEFAULT_ANIMATION_DURATION);

                mImageSwitcher.setInAnimation(inanimation);
                mImageSwitcher.setOutAnimation(null);

                break;
            case LAND_LOUVER:

                break;
            case VERT_LOUBER:

                break;
            case RANDOM:
                updateAnimation(new Random().nextInt(VERT_PUSH));

                break;
            default:
                Logger.i("Invalid mode, mode = " + mode + ".");

                mImageSwitcher.setInAnimation(null);
                mImageSwitcher.setOutAnimation(null);

                break;
        }
    }

    private void doShowPicture(Bitmap img, int mode) {
        if (img != null && !img.isRecycled()) {
            if (mCurrentShowType != SHOWTYPE_PICTURE) {
                mProgressbar.setVisibility(View.GONE);
                mSurfaceView.setVisibility(View.GONE);
                mImageSwitcher.setVisibility(View.VISIBLE);
            }

            mCurrentShowType = SHOWTYPE_PICTURE;
            BitmapDrawable imgdwb = new BitmapDrawable(mContext.getResources(), img);
            if (imgdwb != null && mImageSwitcher.getNextView() != null) {
                updateAnimation(mode);
                mImageSwitcher.setImageDrawable(imgdwb);
            }
        }
    }

    private void clearImageView() {
        mImageSwitcher.clearAnimation();
        mImageSwitcher.removeAllViews();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_SHOWPROGBAR:
                    doShowProgressBar();

                    break;

                case EVENT_SHOWSURFACEVIEW:
                    doShowSurfaceView();

                    break;
                case EVENT_PLAYVIDEO:
                    doPlayVideo((MediaInfoRef) msg.obj);

                    break;
                case EVENT_SHOWPICTURE:
                    doShowPicture((Bitmap) msg.obj, msg.arg1);

                    break;

                case EVENT_HIDE_CONTROLER:
                    hideController();

                    break;

                case EVENT_PROGRESS_CHANGED:
                    if (mControlWindow.isShowing()) {
                        // set Seek bar value
                        mControlView.setSeekBarProgress(msg.getData().getInt("CurrentPostion"));

                        // set Seek bar secondary value
                        mControlView.setSeekBarSecondaryProgress(0);

                        // Update the Played time on Control bar
                        mControlView.setPlayedText(msg.getData().getString("PlayedTime"));
                        mControlView.setDurationText(msg.getData().getString("DurationTime"));
                    }
                    break;

                default:
                    Logger.i("Unknown event, msg.what = " + msg.what + ".");
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public boolean needCombineCap() {
        return (mCurrentMedia != null && mCurrentMedia.vType != null && !mCurrentMedia.vType.endsWith("BroadcastVideo") && mMediaPlayer != null && mMediaPlayer.isPlaying());
    }

    public Bitmap getVideoCap() {
        Bitmap bitmap = mMediaRetriever.getFrameAtTime(mMediaPlayer.getCurrentPosition(), MediaMetadataRetriever.OPTION_NEXT_SYNC);
        if (bitmap != null) {
            int swidth = bitmap.getWidth();
            int sheight = bitmap.getHeight();
            float scaleWidht = (float) mWidth / swidth;
            float scaleHeight = (float) mHeight / sheight;
            Matrix matrix = new Matrix();
            matrix.setScale(scaleWidht, scaleHeight);
            return Bitmap.createBitmap(bitmap, 0, 0, swidth, sheight, matrix, true);
        }
        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    private void decodeGifPicture(final MediaInfoRef picInfo) {
        // add decode info to the hash map
        if ((mDecodeInfo = getGifDecodeInfo(picInfo)) == null) {
            mDecodeInfo = new GifDecodeInfo();
            mGifDecodeInfoMap.put(picInfo.verifyCode, mDecodeInfo);
        }

        mDecodeInfo.clear();
        releaseGifDecoder();

        // Create the Stream
        InputStream isImgBuff = createImgInputStream(picInfo);

        // start decoder GIF file
        if (isImgBuff != null) {
            mGifDecoder = new GifDecoder(isImgBuff, new GifAction() {
                @Override
                public void parseOk(boolean parseStatus, int frameIndex) {
                    if (mDecodeInfo != null && mGifDecoder != null) {
                        if (frameIndex == -1) {
                            mDecodeInfo.setFrameCount(mGifDecoder.getFrameCount());
                            mDecodeInfo.setDecodeState(GifDecodeInfo.DECODE_COMPLATED);
                        } else {
                            mDecodeInfo.setDecodeState(GifDecodeInfo.DECODING_STATE);
                            if (parseStatus) {
                                GifFrame frame = mGifDecoder.getFrame(frameIndex - 1);
                                if (frame != null && frame.image != null) {
                                    Bitmap tmpBmp = frame.image;
                                    mDecodeInfo.addFrameDelay(frame.delay);
                                    StringBuilder sbFileName = new StringBuilder();
                                    sbFileName.append(PosterApplication.getGifImagePath(picInfo.verifyCode));
                                    sbFileName.append(File.separator);
                                    sbFileName.append(frameIndex - 1).append(".jpg");
                                    PosterApplication.resizeImage(tmpBmp, sbFileName.toString(), tmpBmp.getWidth(), tmpBmp.getHeight());
                                }
                            }
                        }
                    }
                }
            });

            mGifDecoder.start();
            mDecodeInfo.setDecodeState(GifDecodeInfo.DECODE_START);
        }
    }

    private GifDecodeInfo getGifDecodeInfo(MediaInfoRef gifInfo) {
        synchronized (mGifDecodeInfoMap) {
            if (mGifDecodeInfoMap != null && !mGifDecodeInfoMap.isEmpty()) {
                return mGifDecodeInfoMap.get(gifInfo.verifyCode);
            }
        }
        return null;
    }

    private boolean isDecodeComplated(MediaInfoRef gifInfo) {
        GifDecodeInfo decodeInfo = getGifDecodeInfo(gifInfo);
        return (decodeInfo != null && decodeInfo.getDecodeState() == GifDecodeInfo.DECODE_COMPLATED);
    }

    private void releaseGifDecoder() {
        if (mGifDecoder != null) {
            mGifDecoder.free();
            mGifDecoder = null;
        }
    }

    private boolean gifImgIsExsit(MediaInfoRef gifInfo) {
        GifDecodeInfo decodeInfo = getGifDecodeInfo(gifInfo);
        String imgPath = PosterApplication.getGifImagePath(gifInfo.verifyCode);
        if (decodeInfo != null && FileUtils.isExist(imgPath)) {
            int nFrameCnt = decodeInfo.getFrameCount();
            if (new File(imgPath).listFiles().length == nFrameCnt) {
                return true;
            }
        }
        return false;
    }

    private void terminateGifDecode() {
        if (mGifDecoder != null) {
            mGifDecoder.terminate();
        }
    }

    private GifDecodeInfo getCurrentDecodeInfo() {
        return mDecodeInfo;
    }

    private boolean playGif(MediaInfoRef picInfo) throws InterruptedException {
        // decode for GIF
        boolean isComplated = isDecodeComplated(picInfo);
        GifDecodeInfo decodeInfo = getGifDecodeInfo(picInfo);
        if ((decodeInfo == null) ||
                (isComplated && !gifImgIsExsit(picInfo)))   // 未解码或已解码但文件不存在，则启动解码
        {
            // Start decode
            decodeGifPicture(picInfo);

            // Get Current Decode Info
            decodeInfo = getCurrentDecodeInfo();

            // Wait for decode
            while (decodeInfo.getDecodeState() != GifDecodeInfo.DECODE_COMPLATED) {
                Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
            }

            // release resource for decoder
            releaseGifDecoder();
        } else if (decodeInfo != null && !isComplated) {
            return false;   // decoding by other view
        }

        // Show GIF
        boolean ret = false;
        if (decodeInfo != null) {
            MediaInfoRef tempMedia = new MediaInfoRef();
            tempMedia.mediaType = "Image";
            tempMedia.source = "File";
            tempMedia.aspect = picInfo.aspect;
            tempMedia.duration = picInfo.duration;
            tempMedia.durationPerPage = picInfo.durationPerPage;
            tempMedia.endtime = picInfo.endtime;
            tempMedia.mid = picInfo.mid;
            tempMedia.mode = picInfo.mode;
            tempMedia.md5Key = picInfo.md5Key;
            tempMedia.playedtimes = picInfo.playedtimes;
            tempMedia.playlistmode = picInfo.playlistmode;
            tempMedia.starttime = picInfo.starttime;
            tempMedia.times = picInfo.times;
            tempMedia.timetype = picInfo.timetype;
            tempMedia.containerwidth = picInfo.containerwidth;
            tempMedia.containerheight = picInfo.containerheight;

            Bitmap srcBmp = null;
            StringBuilder sbImgFile = new StringBuilder();
            String strPath = PosterApplication.getGifImagePath(picInfo.verifyCode);
            for (int i = 0; i < decodeInfo.getFrameCount(); i++) {
                sbImgFile.setLength(0);
                sbImgFile.append(strPath);
                sbImgFile.append(File.separator);
                sbImgFile.append(i).append(".jpg");
                tempMedia.filePath = sbImgFile.toString();
                if ((srcBmp = getBitMap(tempMedia, true)) != null) {
                    try {
                        ret = true;
                        showPicture(srcBmp, NONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    Thread.sleep(decodeInfo.getFrameDelay(i));
                }
            }
        }
        return ret;
    }
}
