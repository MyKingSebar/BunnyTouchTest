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
     * gif����۲���
     *
     * @hide
     * @param parseStaus
     *            �����Ƿ�ɹ����ɹ���Ϊtrue
     */
    public void parseOk(boolean parseStatus, int frameIndex);
}
