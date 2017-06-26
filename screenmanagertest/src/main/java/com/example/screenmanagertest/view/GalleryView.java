/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential. 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;


public class GalleryView extends PosterBaseView
{
    private RelativeLayout galleryLayout = null;
    
    @SuppressWarnings("unused")
    private ImageButton left = null;
    @SuppressWarnings("unused")
    private ImageButton right = null;

    public GalleryView(Context context)
    {
        super(context);
        initView(context);
    }
    
    public GalleryView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }
    
    public GalleryView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }
    
    private void initView(Context context)
    {
        Logger.d("Gallery View initialize......");
        
        // Get layout from XML file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_gallery, this);
        
        galleryLayout = (RelativeLayout) findViewById(R.id.gallerylayout);
        left = (ImageButton) findViewById(R.id.left);
        right = (ImageButton) findViewById(R.id.right);
    }

    @Override
    public void onViewDestroy()
    {
        galleryLayout.destroyDrawingCache();
        this.removeAllViews();
    }

    @Override
    public void onViewPause()
    {
        
    }

    @Override
    public void onViewResume()
    {
        
    }

    @Override
    public void startWork()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopWork()
    {
        // TODO Auto-generated method stub
        
    }
}
