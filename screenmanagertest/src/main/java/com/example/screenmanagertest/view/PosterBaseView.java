/*
 * Copyright (C) 2013 poster PCE YoungSee Inc. All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.common.FileUtils;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;
import com.example.screenmanagertest.screenmanager.ScreenManager;

public abstract class PosterBaseView extends FrameLayout {
    protected Context            mContext      = null;

    protected  String Touch=null;

    public String getViewTouch() {
        return Touch;
    }

    public void setViewTouch(String touch) {
        Touch = touch;
    }

    // View attributes
    protected int                mXPos         = 0;
    protected int                mYPos         = 0;
    protected int                mWidth        = 0;
    protected int                mHeight       = 0;
    protected String             mViewName     = "";
    protected String             mViewType     = "";
    
    // The Medias need to play for the View
    protected int                mCurrentIdx   = -1;
    protected MediaInfoRef       mCurrentMedia = null;
    protected List<MediaInfoRef> mMediaList    = null;

    protected long  DEFAULT_THREAD_PERIOD      = 1000;
    protected long  DEFAULT_THREAD_QUICKPERIOD = 100;

    protected boolean mIsSyncLoadMedia      = false;
    protected boolean mIsSyncPlayMedia      = false;
    protected boolean mIsSyncStartPlayVideo = false;
    
    protected String mSyncMediaName    = null;
    protected String mSyncMediaSrc     = null;
    protected String mSyncMediaVryCode = null;
    protected int    mSyncMediaPos     = 0;
    
    public PosterBaseView(Context context) {
        super(context);
        mContext = context;
    }

    public PosterBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PosterBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setViewName(String viewName)
    {
        mViewName = viewName;
    }
    
    public void setViewType(String viewType)
    {
        mViewType = viewType;
    }
    
    public void setViewPosition(int xPos, int yPos)
    {
        mXPos = xPos;
        mYPos = yPos;
        this.setX(xPos);
        this.setY(yPos);
    }
    
    public void setViewSize(int nWidth, int nHeight)
    {
        mWidth = nWidth;
        mHeight = nHeight;
        this.setLayoutParams(new LayoutParams(nWidth, nHeight));
    }

    public void setMediaList(List<MediaInfoRef> lst)
    {
        mMediaList = lst;
    }

    public String getViewName()
    {
        return mViewName;
    }

    public String getViewType()
    {
        return mViewType;
    }

    public int getXPos()
    {
        return mXPos;
    }

    public int getYPos()
    {
        return mYPos;
    }

    public int getViewWidth()
    {
        return mWidth;
    }

    public int getViewHeight()
    {
        return mHeight;
    }

    public List<MediaInfoRef> getMediaList()
    {
        return mMediaList;
    }



    public static BitmapFactory.Options setBitmapOption(final MediaInfoRef picInfo)
    {
        BitmapFactory.Options opt = new BitmapFactory.Options();

        InputStream in = createImgInputStream(picInfo);
        if (in != null)
        {
            // 获取图片实际大小
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, opt);

            int outWidth = opt.outWidth;
            int outHeight = opt.outHeight;
            int nWndWidth = picInfo.containerwidth;
            int nWndHeight = picInfo.containerheight;

            // 设置压缩比例
            opt.inSampleSize = 1;
            if (outWidth > nWndWidth || outHeight > nWndHeight)
            {
                opt.inSampleSize = (outWidth / nWndWidth + outHeight / nWndHeight) / 2;
            }
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inDither = false;
            opt.inJustDecodeBounds = false;

            // 关闭输入??
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return opt;
    }

    public static InputStream createImgInputStream(final MediaInfoRef picInfo)
    {
        InputStream is = null;

        try
        {
            if (FileUtils.mediaIsGifFile(picInfo) || FileUtils.mediaIsPicFromFile(picInfo) || FileUtils.mediaIsTextFromFile(picInfo))
            {
                String strFileName = picInfo.filePath;
                if (FileUtils.isExist(strFileName))
                {
                    is = new FileInputStream(strFileName);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return is;
    }

    // Abstract functions
    public abstract void onViewDestroy();
    public abstract void onViewPause();
    public abstract void onViewResume();
    public abstract void startWork();
    public abstract void stopWork();


    protected String getText(final MediaInfoRef txtInfo)
    {
        String strText = null;
        if (FileUtils.mediaIsTextFromFile(txtInfo))
        {
            strText = readTextFile(txtInfo.filePath);
        }
        return strText;
    }


    protected String readTextFile(String filePath)
    {
        String dest = "";
        InputStream is = null;
        BufferedReader reader = null;
        try
        {
            String str = "";
            StringBuffer sb = new StringBuffer();
            is = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((str = reader.readLine()) != null)
            {
                sb.append(str + "\n");
            }

            // 去掉非法字符
            Pattern p = Pattern.compile("(\ufeff)");
            Matcher m = p.matcher(sb.toString());
            dest = m.replaceAll("");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }

                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return dest;
    }

    /**
     * 自动分割文本
     *
     * @param content
     *            需要分割的文本
     * @param p
     *            画笔，用来根据字体测量文本的宽度
     * @param width
     *            最大的可显示像素（一般为控件的宽度）
     * @return 一个字符串数组，保存每行的文本
     */
    protected ArrayList<String> autoSplit(String content, Paint p, float width)
    {
        String strText = StringFilter(content);
        ArrayList<String> retList = new ArrayList<String>();
        if (strText == null)
        {
            return retList;
        }

        char ch = 0;
        int w = 0;
        int istart = 0;
        int length = strText.length();
        float[] widths = new float[1];
        for (int i = 0; i < length; i++)
        {
            ch = strText.charAt(i);
            p.getTextWidths(String.valueOf(ch), widths);
            if (ch == '\n')
            {
                retList.add(strText.substring(istart, i));
                istart = i + 1;
                w = 0;
            }
            else
            {
                w += (int) Math.ceil(widths[0]);
                if (w > width)
                {
                    retList.add(strText.substring(istart, i));
                    istart = i;
                    i--;
                    w = 0;
                }
                else
                {
                    if (i == length - 1)
                    {
                        retList.add(strText.substring(istart));
                    }
                }
            }
        }

        return retList;
    }

    // 替换、过滤特殊字符
    protected String StringFilter(String str)
    {
        if (str == null)
        {
            return null;
        }

        str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    protected int getFontSize(final MediaInfoRef txtInfo)
    {
        int nFontSize = 0;
        if (txtInfo.fontSize != null)
        {
            nFontSize = Integer.parseInt(txtInfo.fontSize);
        }
        else
        {
            nFontSize = 50;
        }
        return nFontSize;
    }


    protected Bitmap getBitMap(final MediaInfoRef picInfo, boolean isUseCacheForNetPic)
    {
        Bitmap retBmp = null;
        if (!isUseCacheForNetPic && FileUtils.mediaIsPicFromNet(picInfo))
        {
//            retBmp = loadNetPicture(picInfo, isUseCacheForNetPic);
        }
        else
        {
            String strKey = getImgCacheKey(picInfo);
            if ((retBmp = PosterApplication.getBitmapFromMemoryCache(strKey)) == null)
            {
                if (FileUtils.mediaIsPicFromNet(picInfo))
                {
                    // Check whether the picture save in disk cache.
                    // if so, then load the picture to memory cache and show it.
                    if ((retBmp = PosterApplication.getBitmapFromDiskCache(strKey)) != null)
                    {
                        PosterApplication.addBitmapToMemoryCache(strKey, retBmp);
                    }
                    else
                    {
//                        retBmp = loadNetPicture(picInfo, isUseCacheForNetPic);
                    }
                }
                else if (FileUtils.mediaIsPicFromFile(picInfo))
                {
                    retBmp = loadLocalPicture(picInfo);
                }
            }
        }

        return retBmp;
    }

    protected Bitmap loadLocalPicture(final MediaInfoRef picInfo)
    {
        Bitmap srcBmp = null;

        try
        {
            if (picInfo == null || FileUtils.mediaIsPicFromNet(picInfo))
            {
                Log.e("load picture error", "picture info is error");
                return null;
            }

            // Create the Stream
            InputStream isImgBuff = createImgInputStream(picInfo);
            if (isImgBuff == null)
            {
                return null;
            }

            try
            {
                // Create the bitmap for BitmapFactory
                srcBmp = BitmapFactory.decodeStream(isImgBuff, null, setBitmapOption(picInfo));
            }
            catch (java.lang.OutOfMemoryError e)
            {
                Logger.e("picture is too big, out of memory!");

                if (srcBmp != null && !srcBmp.isRecycled())
                {
                    srcBmp.recycle();
                    srcBmp = null;
                }

                System.gc();
            }
            finally
            {
                if (isImgBuff != null)
                {
                    isImgBuff.close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Will be stored the new image to LruCache
        if (srcBmp != null)
        {
            PosterApplication.addBitmapToMemoryCache(getImgCacheKey(picInfo), srcBmp);
        }

        return srcBmp;
    }


    protected String getImgCacheKey(MediaInfoRef pInfo)
    {
        String retKey = null;
        if (TextUtils.isEmpty(mViewName))
        {
            retKey = pInfo.filePath;
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append(mViewName).append(File.separator).append(pInfo.filePath);
            retKey = sb.toString();
        }
        return retKey;
    }

    protected boolean noMediaValid()
    {
        if (mMediaList != null)
        {
            for (MediaInfoRef mediaInfo : mMediaList)
            {
            	if (mediaInfo.filePath.equals(PosterApplication.getStandbyScreenImgPath()))
            	{
            		// 加载待机画面不需要显示ProgressBar
            		return false;
            	}
            	else if (FileUtils.mediaIsFile(mediaInfo) &&
                    FileUtils.isExist(mediaInfo.filePath) )
                {
                    return false;
                }
            }
        }
        return true;
    }

    protected MediaInfoRef findNextMedia()
    {
        if (mMediaList == null)
        {
            Logger.i("View [" + mViewName + "] Media list is null.");
            return null;
        }

        if (mMediaList.isEmpty())
        {
            Logger.i("View [" + mViewName + "] No media in the list.");
            return null;
        }

        if (++mCurrentIdx >= mMediaList.size())
        {
            mCurrentIdx = 0;
//            if (mViewType.contains("Main") && materaialsIsAllShow())
//            {
//                ScreenManager.getInstance().setPrgFinishedFlag(true);
//            }
        }

        return mMediaList.get(mCurrentIdx);
    }

    protected MediaInfoRef findSyncMedia()
    {
    	if (mMediaList == null)
        {
            Logger.i("View [" + mViewName + "] Media list is null.");
            return null;
        }

        if (mMediaList.isEmpty())
        {
            Logger.i("View [" + mViewName + "] No media in the list.");
            return null;
        }

        if (this.mSyncMediaPos < mMediaList.size())
        {
        	MediaInfoRef media = mMediaList.get(mSyncMediaPos);
        	if ("File".equals(this.mSyncMediaSrc))
        	{
        		if (media.verifyCode.equals(this.mSyncMediaVryCode))
        		{
        			mCurrentIdx = mSyncMediaPos;
        			return media;
        		}
        	}
        	else
        	{
        		if (media.filePath.equals(this.mSyncMediaName))
        		{
        			mCurrentIdx = mSyncMediaPos;
        			return media;
        		}
        	}
        }

        mCurrentIdx = -1;
        for (MediaInfoRef mediaInfo : mMediaList)
        {
        	if ("File".equals(this.mSyncMediaSrc))
        	{
        		if (mediaInfo.verifyCode.equals(this.mSyncMediaVryCode))
        		{
        			return mediaInfo;
        		}
        	}
        	else
        	{
        		if (mediaInfo.filePath.equals(this.mSyncMediaName))
        		{
        			return mediaInfo;
        		}
        	}
        	mCurrentIdx++;
        }

        if (mCurrentIdx >= mMediaList.size())
        {
        	mCurrentIdx = 0;
        }

        return null;
    }






    protected boolean syncMediaIsSame(MediaInfoRef mediaInfo)
    {
    	if ("File".equals(this.mSyncMediaSrc))
    	{
    		if (mediaInfo.verifyCode.equals(this.mSyncMediaVryCode))
    		{
    			return true;
    		}
    	}
    	else
    	{
    		if (mediaInfo.filePath.equals(this.mSyncMediaName))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private boolean materaialsIsAllShow()
    {
        for (MediaInfoRef media : mMediaList)
        {
            if (media.playedtimes <= 0)
            {
                return false;
            }
        }
        return true;
    }
    protected MediaInfoRef findNextOrSyncMedia() throws InterruptedException
    {
        MediaInfoRef retMedia = null;

            retMedia = findNextMedia();

        return retMedia;
    }

}
