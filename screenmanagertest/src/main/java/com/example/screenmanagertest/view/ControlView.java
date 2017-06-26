/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential. 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;

public class ControlView
{
    private Context mContext = null;
    private View mControlView = null;
    private OnControlChangedListener listener = null;

    private TextView mDurationText = null;
    private TextView mPlayedText = null;
    private SeekBar mSeekBar = null;

    private ImageButton mBnPlay = null;
    private ImageButton mBnVolume = null;

    // Defined a interface
    public interface OnControlChangedListener
    {
        public void onSeekBarProgressChanged(SeekBar seekbar, int progress, boolean fromUser);

        public void onSeekBarStartTrackingTouch(SeekBar arg0);

        public void onSeekBarStopTrackingTouch(SeekBar mSeekBar);

        public void onPlayButtonClick(View v);

        public void onVolumeButtonClick(View v);

        public void onVolumeButtonLongPress(View v);
    }

    public void setOnControlChangedListener(OnControlChangedListener l)
    {
        listener = l;
    }

    public ControlView(Context context)
    {
        mContext = context;
        initControlVeiw();
    }

    private void initControlVeiw()
    {
        Logger.d("Control View initialize......");
        // Get layout form XML
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mControlView = inflater.inflate(R.layout.controler, null);

        // Get Text View & SeekBar
        mDurationText = (TextView) mControlView.findViewById(R.id.duration);
        mPlayedText = (TextView) mControlView.findViewById(R.id.has_played);
        mSeekBar = (SeekBar) mControlView.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser)
            {
                if (listener != null)
                {
                    listener.onSeekBarProgressChanged(seekbar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar mSeekBar)
            {
                if (listener != null)
                {
                    listener.onSeekBarStartTrackingTouch(mSeekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar mSeekBar)
            {
                if (listener != null)
                {
                    listener.onSeekBarStopTrackingTouch(mSeekBar);
                }
            }
        });

        // Get Buttons
        mBnPlay = (ImageButton) mControlView.findViewById(R.id.playbutton);
        mBnPlay.setImageResource(R.drawable.play);
        mBnPlay.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onPlayButtonClick(v);
                }
            }
        });

        mBnVolume = (ImageButton) mControlView.findViewById(R.id.volumebutton);
        mBnVolume.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listener != null)
                {
                    listener.onVolumeButtonClick(v);
                }
            }
        });
        mBnVolume.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (listener != null)
                {
                    listener.onVolumeButtonLongPress(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void setSeekBarProgress(int nProgress)
    {
        if (mSeekBar != null)
        {
            mSeekBar.setProgress(nProgress);
        }
    }

    public void setSeekBarSecondaryProgress(int nProgress)
    {
        if (mSeekBar != null)
        {
            mSeekBar.setSecondaryProgress(nProgress);
        }
    }

    public void setSeekBarMax(int nMax)
    {
        if (mSeekBar != null)
        {
            mSeekBar.setMax(nMax);
        }
    }

    public int getSeekBarMax()
    {
        int nMax = 0;
        if (mSeekBar != null)
        {
            nMax = mSeekBar.getMax();
        }

        return nMax;
    }

    public void setPlayButtonImage(int resId)
    {
        if (mBnPlay != null)
        {
            mBnPlay.setImageResource(resId);
        }
    }

    public void setVolumeButtonImage(int resId)
    {
        if (mBnVolume != null)
        {
            mBnVolume.setImageResource(resId);
        }
    }

    public void setPlayedText(String str)
    {
        if (mPlayedText != null)
        {
            mPlayedText.setText(str);
        }
    }

    public void setDurationText(String str)
    {
        if (mDurationText != null)
        {
            mDurationText.setText(str);
        }
    }

    public View getViewScreen()
    {
        return mControlView;
    }
}
