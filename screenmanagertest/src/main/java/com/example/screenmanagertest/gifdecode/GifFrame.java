/*
 * Copyright (C) 2013 poster PCE YoungSee Inc.
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.gifdecode;

import android.graphics.Bitmap;

public class GifFrame
{

    public Bitmap image;
    public int delay;

    /**
     * ���캯��
     *
     * @param im
     *            ͼƬ
     * @param del
     *            ��ʱ
     */
    public GifFrame(Bitmap im, int del)
    {
        image = im;
        delay = del;
    }

    public GifFrame(String name, int del)
    {
        imageName = name;
        delay = del;
    }

    public String imageName = null;

    /** ��һ֡ */
    public GifFrame nextFrame = null;
}
