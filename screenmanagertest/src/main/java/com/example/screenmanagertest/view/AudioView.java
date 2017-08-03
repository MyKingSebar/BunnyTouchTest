/*
 * Copyright (C) 2013 poster PCE YoungSee Inc. 
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;

import java.io.IOException;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.FileUtils;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;

public class AudioView extends PosterBaseView {
    private MediaPlayer mMediaPlayer = null;
    private boolean mIsPlayingMusic = false;

    private UpdateThread mUpdateThreadHandle = null;

    public AudioView(Context context,boolean issun) {
        super(context);
        initView(context);
        setIssun(issun);
    }
//    public AudioView(Context context) {
//        super(context);
//        initView(context);
//    }

    public AudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        Logger.d("Audio View initialize......");

        // Get layout from XML file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_audio, this);
    }

    @Override
    public void onViewDestroy() {
        cancelUpdateThread();
        releaseMediaPlayer();
        this.removeAllViews();
    }

    @Override
    public void onViewPause() {
        pauseAudio();
    }

    @Override
    public void onViewResume() {
        resumeAudio();
    }

    @Override
    public void startWork() {
        if (mMediaList == null) {
            Logger.i("Media list is null.");
            return;
        } else if (mMediaList.isEmpty()) {
            Logger.i("No media in the list.");
            return;
        }

        startUpdateThread();
    }

    @Override
    public void stopWork() {
        cancelUpdateThread();
        releaseMediaPlayer();
        mCurrentIdx = -1;
        mCurrentMedia = null;
        mIsPlayingMusic = false;
    }

    private void startUpdateThread() {
        cancelUpdateThread();
        mUpdateThreadHandle = new UpdateThread();
        mUpdateThreadHandle.start();
    }

    private void cancelUpdateThread() {
        if (mUpdateThreadHandle != null) {
            mUpdateThreadHandle.cancel();
            mUpdateThreadHandle = null;
        }
    }

    private void pauseUpdateThread() {
        if (mUpdateThreadHandle != null && !mUpdateThreadHandle.isPaused()) {
            mUpdateThreadHandle.onPause();
        }
    }

    private void resumeUpdateThread() {
        if (mUpdateThreadHandle != null && mUpdateThreadHandle.isPaused()) {
            mUpdateThreadHandle.onResume();
        }
    }

    private final class UpdateThread extends Thread {
        private boolean mIsRun = true;
        private Object mPauseLock = null;
        private boolean mPauseFlag = false;

        public UpdateThread() {
            mIsRun = true;
            mPauseLock = new Object();
            mPauseFlag = false;
        }

        public void cancel() {
            Logger.i("Cancels the audio thread.");
            mIsRun = false;
            this.interrupt();
        }

        public void onPause() {
            Logger.i("Pauses the audio thread.");
            synchronized (mPauseLock) {
                mPauseFlag = true;
            }
        }

        public void onResume() {
            Logger.i("Resumes the audio thread.");
            synchronized (mPauseLock) {
                mPauseFlag = false;
                mPauseLock.notify();
            }
        }

        public boolean isPaused() {
            return mPauseFlag;
        }

        @Override
        public void run() {
            Logger.i("New audio thread, id is: " + currentThread().getId());

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
                        Logger.i("mMediaList is null, thread exit.");
                        return;
                    } else if (mMediaList.isEmpty()) {
                        Logger.i("No audio info in the list, thread exit.");
                        return;
                    } else if ((mCurrentIdx < -1) || (mCurrentIdx >= mMediaList.size())) {
                        Logger.i("mCurrentIdx (" + mCurrentIdx + ") is invalid, thread exit.");
                        return;
                    }

                    if (!mIsPlayingMusic) {
                        media = findNextOrSyncMedia();

                        if (media == null) {
                            Logger.i("No media can be found, current index is: " + mCurrentIdx);
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        }
                        if (FileUtils.mediaIsFile(media) && !FileUtils.isExist(media.filePath)) {
                            Logger.i(media.filePath + " didn't exist, skip it.");
//                            downloadMedia(media);
                            Thread.sleep(DEFAULT_THREAD_QUICKPERIOD);
                            continue;
                        } else {
                            mCurrentMedia = media;


                            mIsPlayingMusic = true;
                            playMusic(media.filePath);
                        }
                    } else {
                    }

                } catch (InterruptedException e) {
                    Logger.i("Audio thread sleep over, and safe exit, the thread id is: " + currentThread().getId());
                    return;
                } catch (Exception e) {
                    Logger.e("Audio thread catch a error, id is: " + currentThread().getId());
                    mIsPlayingMusic = false;
                }
            }

            Logger.i("Audio thread is safely terminated, id is: " + currentThread().getId());
        }
    }

    private void playMusic(String path) {
        if (path == null) {
            Logger.i("Music path is null!");
            mIsPlayingMusic = false;
            return;
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
        }

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }

        try {
            mMediaPlayer.reset(); // 重置
            mMediaPlayer.setDataSource(path); // 设置数据源
            mMediaPlayer.prepareAsync(); // 异步准备
        } catch (IOException e) {
            e.printStackTrace();
            mIsPlayingMusic = false;
        }
    }

    private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mMediaPlayer.start();
        }
    };

    private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mCurrentMedia != null) {
                mCurrentMedia.playedtimes++;
            }
            mIsPlayingMusic = false;
        }
    };

    private OnErrorListener mOnErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            releaseMediaPlayer();
            mIsPlayingMusic = false;
            return true;
        }
    };

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void pauseAudio() {
        pauseUpdateThread();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        mIsPlayingMusic = false;
    }

    private void resumeAudio() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mIsPlayingMusic = true;
        }
        resumeUpdateThread();
    }
}
