/*
 * Copyright (C) 2013 poster PCE YoungSee Inc.
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.gifdecode;

import java.util.ArrayList;

public class GifDecodeInfo
{
    public static final int NOT_DECODE = 0;
    public static final int DECODE_START = 1;
    public static final int DECODING_STATE = 2;
    public static final int DECODE_COMPLATED = 3;
    
    private int mStatus = NOT_DECODE;
    private int mFrameCnt = 0;
    private ArrayList<Integer> mFrameDelay = null;
    
    public void setFrameCount(int nCnt)
    {
        mFrameCnt = nCnt;
    }
    
    public int getFrameCount()
    {
        return mFrameCnt;
    }
    
    public void addFrameDelay(int nDelay)
    {
        if (mFrameDelay == null)
        {
            mFrameDelay = new ArrayList<Integer>();
        }
        mFrameDelay.add(nDelay);
    }
    
    public int getFrameDelay(int nIdx)
    {
        if (mFrameDelay != null && !mFrameDelay.isEmpty())
        {
            return mFrameDelay.get(nIdx);
        }
        
        return -1;
    }
    
    public void setDecodeState(int nState)
    {
        mStatus = nState;
    }
    
    public int getDecodeState()
    {
        return mStatus;
    }
    
    public void clear()
    {
        mFrameCnt = 0;
        mStatus = NOT_DECODE;
        mFrameDelay = new ArrayList<Integer>();
    }
}
