/*
 * Copyright (C) 2013 poster PCE YoungSee Inc.
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.gifdecode;

public interface GifAction
{
    /**
     * gif解码观察者
     *
     * @hide
     *            解码是否成功，成功会为true
     */
    public void parseOk(boolean parseStatus, int frameIndex);
}
