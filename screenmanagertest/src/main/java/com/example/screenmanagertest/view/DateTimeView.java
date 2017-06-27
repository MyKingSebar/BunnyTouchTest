/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential. 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;


public class DateTimeView extends PosterBaseView
{
    private YSTextView tv_top = null;
    private YSTextView tv_middle = null;
    private YSTextView tv_bottom = null;
    
    private UpdateTimeThread mUpdateTimeThread = null;
    private int mDateFormatType = DATE_FORMAT_TWO_CHINA;

    private final static String[] WEEK_LONG =
    { "星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    private final static String[] WEEK_SHORT =
    { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

    // Define format
    private static final int DATE_FORMAT_SINGLE_TIME = 0;
    private static final int DATE_FORMAT_SINGLE_TIMES = 1;
    private static final int DATE_FORMAT_SINGLE_DAY = 2;
    private static final int DATE_FORMAT_SINGLE_DAY_TIME = 3;
    private static final int DATE_FORMAT_SINGLE_DAY_WEEK_CHINA = 4;
    private static final int DATE_FORMAT_SINGLE_DAY_TIME_WEEK_CHINA = 5;
    private static final int DATE_FORMAT_TWO = 6;
    private static final int DATE_FORMAT_TWO_CHINA = 7;
    private static final int DATE_FORMAT_DAY_WEEK_TIME = 8;
    private static final int DATE_FORMAT_TIME_WEEK_DAY = 9;
    private static final int DATE_FORMAT_DAY_WEEK_TIME_CHINA = 10;
    private static final int DATE_FORMAT_TIME_WEEK_DAY_CHINA = 11;

    public DateTimeView(Context context)
    {
        super(context);
        initView(context);
    }

    public DateTimeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context)
    {
        Logger.d("DateTime View initialize......");

        // Get layout from XML file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_datetime, this);

        // Get widgets from XML file

        tv_top = (YSTextView) findViewById(R.id.date_top);
        tv_middle = (YSTextView) findViewById(R.id.date_middle);
        tv_bottom = (YSTextView) findViewById(R.id.date_bottom);

        if(getViewTouch()!=null){
            tv_top.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("jialei","touch:"+getViewTouch());
                }
            });
        }
    }

    @Override
    public void onViewDestroy()
    {
        cancelUpdateThread();
        this.removeAllViews();
    }

    @Override
    public void onViewPause()
    {
        pauseUpdateThread();
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

        mCurrentIdx = 0;
        mCurrentMedia = mMediaList.get(mCurrentIdx);
        setDefultValue(mCurrentMedia);
        setDateFormat(getFormatId(mCurrentMedia));
        setViewTextSize(getFontSize(mCurrentMedia));
//        setTypeface(getFont(mCurrentMedia));
//        setViewTextColor(getFontColor(mCurrentMedia));
        startUpdateThread();
    }

    @Override
    public void stopWork()
    {
        cancelUpdateThread();
        mCurrentIdx   = -1;
        mCurrentMedia = null;
    }

    private int getFormatId(final MediaInfoRef txtInfo)
    {
        String format = txtInfo.format;
        if("H:i".equals(format))
            mDateFormatType = DATE_FORMAT_SINGLE_TIME;
        else if("H:i:s".equals(format))
            mDateFormatType = DATE_FORMAT_SINGLE_TIMES;
        else if("Y-m-d".equals(format))
            mDateFormatType = DATE_FORMAT_SINGLE_DAY;
        else if("Y-m-d H:i".equals(format))
            mDateFormatType = DATE_FORMAT_SINGLE_DAY_TIME;
        else if("Y-m-d\\nH:i:s".equals(format))
            mDateFormatType = DATE_FORMAT_TWO;
        else if("Y-m-d\\nl\\nH:i:s".equals(format))
            mDateFormatType = DATE_FORMAT_TIME_WEEK_DAY;
        else if("H:i:s\\nl\\nY-m-d".equals(format))
            mDateFormatType = DATE_FORMAT_DAY_WEEK_TIME;
        else if("Y年m月d日(周D)".equals(format))
            mDateFormatType = DATE_FORMAT_SINGLE_DAY_WEEK_CHINA;
        else if("Y年m月d日(周D) H:i".equals(format))
            mDateFormatType = DATE_FORMAT_SINGLE_DAY_TIME_WEEK_CHINA;
        else if("Y年m月d日\\n周D H:i:s".equals(format))
            mDateFormatType = DATE_FORMAT_TWO_CHINA;
        else if("Y年m月d日\\nl\\nH:i:s".equals(format))
            mDateFormatType = DATE_FORMAT_DAY_WEEK_TIME_CHINA;
        else if("H:i:s\\nl\\nY年m月d日".equals(format))
            mDateFormatType = DATE_FORMAT_TIME_WEEK_DAY_CHINA;

        return mDateFormatType;
    }

    private void setDateFormat(int dateFormatType)
    {
        mDateFormatType = dateFormatType;
        if (mDateFormatType <= 5)
        {
            tv_bottom.setVisibility(View.GONE);
            tv_middle.setVisibility(View.GONE);
        }
        else if (mDateFormatType == 6 || mDateFormatType == 7)
        {
            tv_bottom.setVisibility(View.GONE);
        }
    }

    private void setViewTextSize(int fSize)
    {
        tv_top.setTextSize(fSize);
        tv_bottom.setTextSize(fSize);
        tv_middle.setTextSize(fSize);
    }

    private void setTypeface(Typeface typeface)
    {
        if (typeface != null)
        {
            tv_top.setTypeface(typeface);
            tv_bottom.setTypeface(typeface);
            tv_middle.setTypeface(typeface);
        }
    }

    private void setViewTextColor(int nColor)
    {
        tv_top.setTextColor(nColor);
        tv_bottom.setTextColor(nColor);
        tv_middle.setTextColor(nColor);
    }

    private void setDefultValue(MediaInfoRef mediaInfo)
    {
        if (mediaInfo.format == null)
        {
            mediaInfo.format = "Y年m月d日\\n周D H:i:s";
        }
        if (mediaInfo.fontName == null)
        {
//            mediaInfo.fontName = TypefaceManager.DEFAULT;
        }
        if (mediaInfo.fontColor == null)
        {
            mediaInfo.fontColor = "0xffffffff";
        }
        if (mediaInfo.fontSize == null)
        {
            mediaInfo.fontSize = "40";
        }
    }

    private void startUpdateThread()
    {
        cancelUpdateThread();
        mUpdateTimeThread = new UpdateTimeThread();
        mUpdateTimeThread.start();
    }

    private void cancelUpdateThread()
    {
        if (mUpdateTimeThread != null)
        {
            mUpdateTimeThread.cancel();
            mUpdateTimeThread = null;
        }
    }

    private void pauseUpdateThread()
    {
        if (mUpdateTimeThread != null && !mUpdateTimeThread.isPaused())
        {
            mUpdateTimeThread.onPause();
        }
    }

    private void resumeUpdateThread()
    {
        if (mUpdateTimeThread != null && mUpdateTimeThread.isPaused())
        {
            mUpdateTimeThread.onResume();
        }
    }

    // Defined a thread to update the time & date
    @SuppressLint("DefaultLocale")
    private final class UpdateTimeThread extends Thread
    {
        private boolean mIsRun = false;
        private Object   mPauseLock  = null;
        private boolean  mPauseFlag  = false;

        public UpdateTimeThread()
        {
            mIsRun = true;
            mPauseLock = new Object();
            mPauseFlag = false;
        }

        public void cancel()
        {
            Logger.i("Cancels the date-time thread.");
            mIsRun = false;
            this.interrupt();
        }

        public void onPause()
        {
            Logger.i("Pauses the date-time thread.");
            synchronized (mPauseLock)
            {
                mPauseFlag = true;
            }
        }

        public void onResume()
        {
            Logger.i("Resumes the date-time thread.");
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

        private void updateTextView(YSTextView txtView, String text)
        {
            float yPos = txtView.getTextSize() + txtView.getPaddingTop();
            float xPos = (mCurrentMedia.containerwidth - txtView.getPaint().measureText(text)) / 2;
            ArrayList<String> message = new ArrayList<String>();
            message.add(text);
            txtView.setViewAttribute(message, xPos, yPos, txtView.getPaint());
            txtView.postInvalidate();
        }

        @Override
        public void run()
        {
            Logger.i("New UpdateTimeThread, id is: " + currentThread().getId());

            Calendar calendar = null;
            int nYear, nMonth, nDay = 0;
            int nHour, nMinute, nSec = 0;

            int nWeekIdx = 0;
            String strWeek_long = null;
            String strWeek_short = null;
            String strTime = null;
            String strTime_s = null;
            String strDate = null;
            String strDate_china = null;
            StringBuilder sbTime = new StringBuilder();
            StringBuilder sbTime_s = new StringBuilder();
            StringBuilder sbDate = new StringBuilder();
            StringBuilder sbDate_china = new StringBuilder();

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

                    // Calculate Time & Date & Week
                    calendar = Calendar.getInstance(Locale.CHINA);
                    nYear = calendar.get(Calendar.YEAR);
                    nMonth = calendar.get(Calendar.MONTH) + 1;
                    nDay = calendar.get(Calendar.DAY_OF_MONTH);
                    nHour = calendar.get(Calendar.HOUR_OF_DAY);
                    nMinute = calendar.get(Calendar.MINUTE);
                    nSec = calendar.get(Calendar.SECOND);

                    // Week
                    nWeekIdx = calendar.get(Calendar.DAY_OF_WEEK);
                    if (nWeekIdx > 0 && nWeekIdx < 8)
                    {
                        strWeek_long = WEEK_LONG[nWeekIdx - 1];
                        strWeek_short = WEEK_SHORT[nWeekIdx - 1];
                    }

                    // Short Time
                    sbTime_s.setLength(0);
                    sbTime_s.append(nHour).append(":");
                    sbTime_s.append((nMinute < 10) ? ("0" + nMinute) : nMinute).append(":");
                    sbTime_s.append((nSec < 10) ? ("0" + nSec) : nSec);
                    strTime_s = sbTime_s.toString();

                    // Long Time
                    sbTime.setLength(0);
                    sbTime.append(nHour).append(":");
                    sbTime.append((nMinute < 10) ? ("0" + nMinute) : nMinute);
                    strTime = sbTime.toString();

                    // China Date
                    sbDate_china.setLength(0);
                    sbDate_china.append(nYear).append("年");
                    sbDate_china.append(nMonth).append("月");
                    sbDate_china.append(nDay).append("日");
                    strDate_china = sbDate_china.toString();
                    
                    // Date
                    sbDate.setLength(0);
                    sbDate.append(nYear).append("-");
                    sbDate.append(nMonth).append("-");
                    sbDate.append(nDay);
                    strDate = sbDate.toString();
                    
                    /***********************************************************
                     * Update the view content *
                     ***********************************************************/
                    switch (mDateFormatType)
                    {
                    case DATE_FORMAT_SINGLE_TIME:
                        if (strTime != null)
                        {
                            updateTextView(tv_top, strTime);
                        }
                        break;
                        
                    case DATE_FORMAT_SINGLE_TIMES:
                        if (strTime_s != null)
                        {
                            updateTextView(tv_top, strTime_s);
                        }
                        break;
                    case DATE_FORMAT_SINGLE_DAY:
                        if (strDate != null)
                        {    
                            updateTextView(tv_top, strDate);
                        }
                        break;
                        
                    case DATE_FORMAT_SINGLE_DAY_TIME:
                        if (strDate != null && strTime != null)
                        {
                            sbDate.setLength(0);
                            sbDate.append(strDate).append("  ").append(strTime);
                            updateTextView(tv_top, sbDate.toString());
                        }
                        break;
                        
                    case DATE_FORMAT_SINGLE_DAY_WEEK_CHINA:
                        if (strDate_china != null && strWeek_short != null)
                        {
                            sbDate.setLength(0);
                            sbDate.append(strDate_china).append("(").append(strWeek_short).append(")");
                            updateTextView(tv_top, sbDate.toString());
                        }
                        break;
                        
                    case DATE_FORMAT_SINGLE_DAY_TIME_WEEK_CHINA:
                        if (strDate_china != null && strWeek_short != null && strTime != null)
                        {
                            sbDate.setLength(0);
                            sbDate.append(strDate_china).append("(").append(strWeek_short).append(")    ").append(strTime);
                            updateTextView(tv_top, sbDate.toString());
                        }
                        break;
                        
                    case DATE_FORMAT_TWO:
                        if (strDate != null)
                        {
                            updateTextView(tv_top, strDate);
                        }
                        
                        if (strTime_s != null)
                        {
                            updateTextView(tv_middle, strTime_s);
                        }
                        break;
                        
                    case DATE_FORMAT_TWO_CHINA:
                        if (strDate_china != null)
                        {
                            updateTextView(tv_top, strDate_china);
                        }
                        
                        if (strWeek_short != null && strTime_s != null)
                        {
                            sbDate.setLength(0);
                            sbDate.append(strWeek_short).append("  ").append(strTime_s);
                            updateTextView(tv_middle, sbDate.toString());
                        }
                        break;
                        
                    case DATE_FORMAT_TIME_WEEK_DAY:
                        if (strDate != null)
                        {
                            updateTextView(tv_top, strDate);
                        }

                        if (strWeek_long != null)
                        {
                            updateTextView(tv_middle, strWeek_long);
                        }

                        if (strTime_s != null)
                        {
                            updateTextView(tv_bottom, strTime_s);
                        }
                        break;
                        
                    case DATE_FORMAT_DAY_WEEK_TIME:
                        if (strTime_s != null)
                        {
                            updateTextView(tv_top, strTime_s);
                        }

                        if (strWeek_long != null)
                        {
                            updateTextView(tv_middle, strWeek_long);
                        }

                        if (strDate != null)
                        {
                            updateTextView(tv_bottom, strDate);
                        }
                        break;
                        
                    case DATE_FORMAT_DAY_WEEK_TIME_CHINA:
                        if (strDate_china != null)
                        {
                            updateTextView(tv_top, strDate_china);
                        }

                        if (strWeek_long != null)
                        {
                            updateTextView(tv_middle, strWeek_long);
                        }

                        if (strTime_s != null)
                        {
                            updateTextView(tv_bottom, strTime_s);
                        }
                        break;
                        
                    case DATE_FORMAT_TIME_WEEK_DAY_CHINA:
                        if (strDate_china != null)
                        {
                            updateTextView(tv_bottom, strDate_china);
                        }

                        if (strTime_s != null)
                        {
                            updateTextView(tv_top, strTime_s);
                        }

                        if (strWeek_long != null)
                        {
                            updateTextView(tv_middle, strWeek_long);
                        }
                        break;
                    }
                    
                    Thread.sleep(DEFAULT_THREAD_PERIOD);
                }
                catch (InterruptedException e)
                {
                    Logger.i("UpdateTimeThread sleep over, and safe exit, the Thread id is: " + currentThread().getId());
                    return;
                }
                catch (Exception e)
                {
                    Logger.e("UpdateTimeThread Catch a error");
                    e.printStackTrace();
                }
            }

            Logger.i("UpdateTimeThread is safe Terminate, id is: " + currentThread().getId());
        }
    }
}
