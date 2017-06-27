/*
 * Copyright (C) 2013 poster PCE YoungSee Inc. 
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.screenmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SubWindowInfoRef implements Serializable
{
    private boolean mFocusable     = false;
    private String  mSubWindowName = null;
    private String  mSubWindowType = null;
    private int mScreenWidth  = 0;
    private int mScreenHeight = 0;
    private int mXPos = 0;
    private int mYPos = 0;
    private List<MediaInfoRef> mMediaList = null;
    private String touch=null;

    public String getTouch() {
        return touch;
    }

    public void setTouch(String touch) {
        this.touch = touch;
    }

    public SubWindowInfoRef(){}
    
    public SubWindowInfoRef(String SubWndName, String subWndType, int mScreenWidth, int mScreenHeight, int XPos, int YPos, List<MediaInfoRef> mediaList)
    {
        this.mSubWindowType = subWndType;
        this.mSubWindowName = SubWndName;
        this.mScreenWidth  = mScreenWidth;
        this.mScreenHeight = mScreenHeight;
        this.mXPos = XPos;
        this.mYPos = YPos;
        this.mMediaList = new ArrayList<MediaInfoRef>(mediaList);
    }
    
    public void setWidth(int mScreenWidth)
    {
        this.mScreenWidth = mScreenWidth;
    }
    
    public int getWidth()
    {
        return mScreenWidth;
    }
    
    public void setHeight(int mScreenHeight)
    {
        this.mScreenHeight = mScreenHeight;
    }
    
    public int getHeight()
    {
        return mScreenHeight;
    }
    
    public void setXPos(int XPos)
    {
        this.mXPos = XPos;
    }
    
    public int getXPos()
    {
        return mXPos;
    }
    
    public void setYPos(int YPos)
    {
        this.mYPos = YPos;
    }
    
    public int getYPos()
    {
        return mYPos; 
    }
    
    public void setSubWindowName(String SubWindowName)
    {
        this.mSubWindowName = SubWindowName;
    }
    
    public String getSubWindowName()
    {
        return mSubWindowName; 
    }
    
    public void setSubWindowType(String SubWndType)
    {
        this.mSubWindowType = SubWndType;
    }
    
    public String getSubWindowType()
    {
        return mSubWindowType; 
    }
    
    public void setSubWndMediaList(List<MediaInfoRef> mediaList)
    {
        this.mMediaList = new ArrayList<MediaInfoRef>(mediaList);
    }
    
    public List<MediaInfoRef> getSubWndMediaList()
    {
        return mMediaList; 
    }
    
    public void setFocus(boolean focusable)
    {
        this.mFocusable = focusable;
    }
    
    public boolean hasFocus()
    {
        return mFocusable;
    }
}
