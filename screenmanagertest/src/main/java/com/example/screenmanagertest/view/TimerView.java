
package com.example.screenmanagertest.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;


public class TimerView extends PosterBaseView
{
    private YSTextView mTimerTxtv = null;
    private UpdateTimerThread mUpdateTimerThread = null;
    
    private int mTimerFormat = TIMER_FORMAT_DAY_HOUR_MIN_SEC;
    private int mTimerMode = TIMER_MODE_COUNTDOWN;
    private long mTimerDeadlineMillis = -1;
    
    private static final long SECOND_MILLIS = 1000;
    private static final long MINUTE_MILLIS = 60*SECOND_MILLIS;
    private static final long HOUR_MILLIS = 60*MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24*HOUR_MILLIS;

    private static final int TIMER_FORMAT_DAY_ONLYNUM = 0;
    private static final int TIMER_FORMAT_DAY = 1;
    private static final int TIMER_FORMAT_DAY_HOUR = 2;
    private static final int TIMER_FORMAT_DAY_HOUR_MIN = 3;
    private static final int TIMER_FORMAT_DAY_HOUR_MIN_SEC = 4;
    
    private static final int TIMER_MODE_COUNTDOWN = 0;
    private static final int TIMER_MODE_ELAPSE = 1;

    public TimerView(Context context)
    {
        super(context);
        initView(context);
    }
    
    public TimerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }
    
    public TimerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }
    
    private void initView(Context context)
    {
        Logger.d("Timer View initialize......");

        // Get layout from XML file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_timer, this);
        
        // Get widgets from XML file
        mTimerTxtv = (YSTextView) findViewById(R.id.timer_txtv);
        //TODO 临时color
        mTimerTxtv.setTextColor(Color.WHITE);
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
        setTimerFormat(getFormat(mCurrentMedia));
        setTimerMode(getMode(mCurrentMedia));
        setTimerDeadline(getDeadline(mCurrentMedia));
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
    
    private int getFormat(final MediaInfoRef txtInfo)
    {
        if("d".equals(txtInfo.format))
        	return TIMER_FORMAT_DAY_ONLYNUM;
        else if("d天".equals(txtInfo.format))
        	return TIMER_FORMAT_DAY;
        else if("d天H小时".equals(txtInfo.format))
        	return TIMER_FORMAT_DAY_HOUR;
        else if("d天H小时i分".equals(txtInfo.format))
        	return TIMER_FORMAT_DAY_HOUR_MIN;
        else if("d天H小时i分s秒".equals(txtInfo.format))
        	return TIMER_FORMAT_DAY_HOUR_MIN_SEC;
        else
        	return TIMER_FORMAT_DAY_HOUR_MIN_SEC;
    }

    private int getMode(final MediaInfoRef txtInfo)
    {
    	return txtInfo.mode;
    }

    private long getDeadline(final MediaInfoRef txtInfo)
    {
    	Time t = new Time("Asia/Shanghai");
    	t.parse(txtInfo.deadline.replace("-", "").replace(":", "").replace(" ", "T"));
    	return t.toMillis(true);
    }

    private void setTimerFormat(int format)
    {
    	mTimerFormat = format;
    }

    private void setTimerMode(int mode)
    {
    	mTimerMode = mode;
    }

    private void setTimerDeadline(long deadlineMillis)
    {
    	mTimerDeadlineMillis = deadlineMillis;
    }

    private void setViewTextSize(int fSize)
    {
    	mTimerTxtv.setTextSize(fSize);
    }

    private void setTypeface(Typeface typeface)
    {
        if (typeface != null)
        {
        	mTimerTxtv.setTypeface(typeface);
        }
    }

    private void setViewTextColor(int color)
    {
    	mTimerTxtv.setTextColor(color);
    }

    private void startUpdateThread()
    {
        cancelUpdateThread();
        mUpdateTimerThread = new UpdateTimerThread();
        mUpdateTimerThread.start();
    }

    private void cancelUpdateThread()
    {
        if (mUpdateTimerThread != null)
        {
            mUpdateTimerThread.cancel();
            mUpdateTimerThread = null;
        }
    }

    private void pauseUpdateThread()
    {
        if (mUpdateTimerThread != null && !mUpdateTimerThread.isPaused())
        {
            mUpdateTimerThread.onPause();
        }
    }

    private void resumeUpdateThread()
    {
        if (mUpdateTimerThread != null && mUpdateTimerThread.isPaused())
        {
            mUpdateTimerThread.onResume();
        }
    }

    @SuppressLint("DefaultLocale")
    private final class UpdateTimerThread extends Thread
    {
        private boolean mIsRun = false;
        private Object   mPauseLock  = null;
        private boolean  mPauseFlag  = false;

        public UpdateTimerThread()
        {
            mIsRun = true;
            mPauseLock = new Object();
            mPauseFlag = false;
        }

        public void cancel()
        {
            Logger.i("Cancels the timer thread.");
            mIsRun = false;
            this.interrupt();
        }

        public void onPause()
        {
            Logger.i("Pauses the timer thread.");
            synchronized (mPauseLock)
            {
                mPauseFlag = true;
            }
        }

        public void onResume()
        {
            Logger.i("Resumes the timer thread.");
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
            Logger.i("New UpdateTimerThread, id is: " + currentThread().getId());

            Time t = new Time();
            long currentMillis, diffMillis;
            long day, hour, minute, second;
            String strTime;
            StringBuilder sb = new StringBuilder();

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

                    t.setToNow();
                    currentMillis = t.toMillis(true);

                    switch (mTimerMode)
                    {
                    case TIMER_MODE_COUNTDOWN:
                    	if (currentMillis >= mTimerDeadlineMillis)
                    	{
                    		diffMillis = 0;
                    	}
                    	else
                    	{
                    		diffMillis = mTimerDeadlineMillis-currentMillis;
                    	}
                    	break;
                    case TIMER_MODE_ELAPSE:
                    	if (currentMillis <= mTimerDeadlineMillis)
                    	{
                    		diffMillis = 0;
                    	}
                    	else
                    	{
                    		diffMillis = currentMillis-mTimerDeadlineMillis;
                    	}
                    	break;
                    default:
                    	diffMillis = 0;
                    }

                    day = diffMillis/DAY_MILLIS;
                    hour = (diffMillis%DAY_MILLIS)/HOUR_MILLIS;
                    minute = (diffMillis%HOUR_MILLIS)/MINUTE_MILLIS;
                    second = (diffMillis%MINUTE_MILLIS)/SECOND_MILLIS;

                    switch (mTimerFormat)
                    {
                    case TIMER_FORMAT_DAY_ONLYNUM:
                    	sb.setLength(0);
                    	if ((mTimerMode == TIMER_MODE_COUNTDOWN)
                    			&& ((hour != 0) || (minute != 0) || (second != 0)))
                    	{
                    		day += 1;
                    	}
                    	if (day < 10)
                    	{
                    		sb.append("00");
                    	}
                    	else if (day < 100)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(day);
                    	strTime = sb.toString();
                    	break;
                    case TIMER_FORMAT_DAY:
                    	sb.setLength(0);
                    	if ((mTimerMode == TIMER_MODE_COUNTDOWN)
                    			&& ((hour != 0) || (minute != 0) || (second != 0)))
                    	{
                    		day += 1;
                    	}
                    	if (day < 10)
                    	{
                    		sb.append("00");
                    	}
                    	else if (day < 100)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(day).append("天");
                    	strTime = sb.toString();
                    	break;
                    case TIMER_FORMAT_DAY_HOUR:
                    	sb.setLength(0);
                    	if ((mTimerMode == TIMER_MODE_COUNTDOWN)
                    			&& ((minute != 0) || (second != 0)))
                    	{
                    		if (hour != 23)
                    		{
                    			hour += 1;
                    		}
                    		else
                    		{
                    			day += 1;
                    			hour = 0;
                    		}
                    	}
                    	if (day < 10)
                    	{
                    		sb.append("00");
                    	}
                    	else if (day < 100)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(day).append("天");
                    	if (hour < 10)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(hour).append("小时");
                    	strTime = sb.toString();
                    	break;
                    case TIMER_FORMAT_DAY_HOUR_MIN:
                    	sb.setLength(0);
                    	if ((mTimerMode == TIMER_MODE_COUNTDOWN) && (second != 0))
                    	{
                    		if (minute != 59)
                    		{
                    			minute += 1;
                    		}
                    		else
                    		{
                    			if (hour != 23)
                        		{
                        			hour += 1;
                        		}
                        		else
                        		{
                        			day += 1;
                        			hour = 0;
                        		}
                    			minute = 0;
                    		}
                    	}
                    	if (day < 10)
                    	{
                    		sb.append("00");
                    	}
                    	else if (day < 100)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(day).append("天");
                    	if (hour < 10)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(hour).append("小时");
                    	if (minute < 10)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(minute).append("分");
                    	strTime = sb.toString();
                    	break;
                    case TIMER_FORMAT_DAY_HOUR_MIN_SEC:
                    	sb.setLength(0);
                    	if (day < 10)
                    	{
                    		sb.append("00");
                    	}
                    	else if (day < 100)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(day).append("天");
                    	if (hour < 10)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(hour).append("小时");
                    	if (minute < 10)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(minute).append("分");
                    	if (second < 10)
                    	{
                    		sb.append("0");
                    	}
                    	sb.append(second).append("秒");
                    	strTime = sb.toString();
                    	break;
                    default:
                    	strTime = "";
                    }
                    
                    updateTextView(mTimerTxtv, strTime);
                	
                    Thread.sleep(DEFAULT_THREAD_PERIOD);
                }
                catch (InterruptedException e)
                {
                    Logger.i("UpdateTimerThread sleep over, and safe exit, the Thread id is: " + currentThread().getId());
                    return;
                }
                catch (Exception e)
                {
                    Logger.e("UpdateTimerThread Catch a error");
                    e.printStackTrace();
                }
            }

            Logger.i("UpdateTimerThread is safe Terminate, id is: " + currentThread().getId());
        }
    }
}
