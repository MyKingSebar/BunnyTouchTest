/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential. 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;

import java.io.IOException;
import java.io.InputStream;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.view.MotionEvent;
import android.view.View;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;

@SuppressLint("DrawAllocation")
public class SoundView extends View
{
    private Context mContext = null;
    private int index = -1;
    private OnVolumeChangedListener mOnVolumeChangedListener = null;

    private final static int HEIGHT = 11;
    public  final static int MY_HEIGHT = 163;
    public  final static int MY_WIDTH = 44;

    public interface OnVolumeChangedListener
    {
        public void setYourVolume(int index);
    }

    public void setOnVolumeChangeListener(OnVolumeChangedListener l)
    {
        mOnVolumeChangedListener = l;
    }

    public SoundView(Context context)
    {
        super(context);
        mContext = context;
        initSoundView();
    }

    private void initSoundView()
    {
        Logger.d("Sound View intialize......");
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setIndex(am.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int y = (int) event.getY();
        int x = (int) event.getX();
        int nIdx = -1;

        Logger.d("setIndex: x = " + x + " y = " + y);

        if ((x >= 0 && x <= MY_WIDTH) && (y >= 0 && y <= MY_HEIGHT))
        {
            nIdx = 15 - (y * 15 / MY_HEIGHT);
            setIndex(nIdx);
        }

        Logger.d("setIndex: " + nIdx);
        return true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;

        InputStream imgTmpBuff = mContext.getResources().openRawResource(R.drawable.sound_line);
        Bitmap bm = BitmapFactory.decodeStream(imgTmpBuff, null, options);

        imgTmpBuff = mContext.getResources().openRawResource(R.drawable.sound_line1);
        Bitmap bm1 = BitmapFactory.decodeStream(imgTmpBuff, null, options);

        int bitmapWidth = bm.getWidth();
        int bitmapHeight = bm.getHeight();
        int reverseIndex = 15 - index;
        for(int i = 0; i != reverseIndex; ++i)
        {
            canvas.drawBitmap(bm1, new Rect(0, 0, bitmapWidth, bitmapHeight), 
                    new Rect(0, (i * HEIGHT), bitmapWidth, (i * HEIGHT + bitmapHeight)), null);
        }
        
        for(int i = reverseIndex; i != 15; ++i)
        {
            canvas.drawBitmap(bm, new Rect(0, 0, bitmapWidth, bitmapHeight), 
                    new Rect(0, (i * HEIGHT), bitmapWidth, (i * HEIGHT + bitmapHeight)), null);
        }

        if (!bm.isRecycled())
        {
            bm.recycle();
        }
        
        if (!bm1.isRecycled())
        {
            bm1.recycle();
        }

        if (imgTmpBuff != null)
        {
            try
            {
                imgTmpBuff.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        super.onDraw(canvas);
    }

    private void setIndex(int n)
    {
        if (n > 15)
        {
            n = 15;
        }
        else if (n < 0)
        {
            n = 0;
        }

        if (index != n)
        {
            index = n;
            if (mOnVolumeChangedListener != null)
            {
                mOnVolumeChangedListener.setYourVolume(n);
            }
        }
        invalidate();
    }
}
