package com.example.screenmanagertest.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

public class YSTextView extends AppCompatTextView
{
    private ArrayList<String> mContent = null;
    private float  mXPos    = 0;
    private float  mYPos    = 0;
    private Paint  mPaint   = null;

    public YSTextView(Context context)
    {
        super(context);
        this.setWillNotDraw(false);
    }
    
    public YSTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setWillNotDraw(false);
    }
    
    public YSTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.setWillNotDraw(false);
    }
    
    public void setViewAttribute(ArrayList<String> text, float x, float y, Paint p)
    {
        mContent = text;
        mXPos = x;
        mYPos = y;
        mPaint = p;
    }
    
    public void setXPos(float x)
    {
        mXPos = x;
    }
    
    public void setYPos(float y)
    {
        mYPos = y;
    }
    
    public void setContent(ArrayList<String> text)
    {
        mContent = text;
    }
    
    public void setPaint(Paint p)
    {
        mPaint = p;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mContent != null && mPaint != null)
        {
            canvas.drawColor(Color.TRANSPARENT);

            float yPos = mYPos;
            FontMetrics fm = mPaint.getFontMetrics();
            float lineHeight = (float)Math.ceil(fm.descent - fm.ascent); // 每行高度
            for (String text : mContent)
            {
                canvas.drawText(text, mXPos, yPos, mPaint);
                yPos = yPos + lineHeight + fm.leading; // (字高+行间距)
            }
        }
        super.onDraw(canvas);
    }
}
