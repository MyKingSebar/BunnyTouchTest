/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential. 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.os.SystemClock;

import com.example.screenmanagertest.MainActivity;
import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;

public class YSWebView extends PosterBaseView
{
    private WebView mWv = null;

    private static int MAX_CLICK_CNTS    = 5;
	private long  mLastClickTime         = 0;
	private static int mCurrentClickCnts = 0;
	
	
	private SharedPreferences sharedPreferences = mContext.getSharedPreferences("reload_for_period", Activity.MODE_PRIVATE);
	private int timeForPeriod ;
	
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (mWv !=null) {
				timeForPeriod = sharedPreferences.getInt("time_period", 60);
				if (sharedPreferences.getBoolean("isReload", false)) {
					try {
						mWv.reload();
						Logger.i("webview is reloading");
					} catch (Exception e) {
						// TODO: handle exception
						Logger.e("YsWebView mWv.reload error");
						e.printStackTrace();
					}
				}
				mHandler.postDelayed(mRunnable, timeForPeriod * 1000);
			}
		}
	};
	
    public YSWebView(Context context)
    {
        super(context);
        initView(context);
    }
    
    public YSWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }
    
    public YSWebView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_web, this);
        
        // Get widgets from XML file
        mWv = (WebView) findViewById(R.id.wv);
       
        if (mWv != null)
        {
            WebSettings webSettings = mWv.getSettings();
            
            // Support java script
            webSettings.setJavaScriptEnabled(true);// 可用JS
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setSaveFormData(true);
            webSettings.setSavePassword(true);
            CookieManager.getInstance().setAcceptCookie(true);

            // Support access Assets and resources
            webSettings.setAllowFileAccess(true);

            //Support zoom page
            webSettings.setSupportZoom(true); // 可缩放
            webSettings.setBuiltInZoomControls(true);
    		webSettings.setDisplayZoomControls(true);

            //set xml dom cache
            webSettings.setDomStorageEnabled(true);

            //提高渲染的优先级
            webSettings.setRenderPriority(RenderPriority.HIGH);

            // set cache
            String appCachePath = MainActivity.INSTANCE.getDir("netCache", Context.MODE_PRIVATE).getAbsolutePath();
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(appCachePath);
            webSettings.setAppCacheMaxSize(1024*1024*5);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            webSettings.setSupportMultipleWindows(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setPluginsEnabled(true);
            webSettings.setPluginState(PluginState.ON);
            // 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
            mWv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            mWv.setLongClickable(true);
            mWv.setClickable(true);
            mWv.setScrollbarFadingEnabled(true);
            mWv.setDrawingCacheEnabled(true);




            // set WebViewClient
            mWv.setWebViewClient(new WebViewClient()
            {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    try {
                        mWv.stopLoading();
                        mWv.clearFormData();
                        mWv.clearAnimation();
                        mWv.clearDisappearingChildren();
                        mWv.clearView();
                        mWv.clearHistory();
                        mWv.destroyDrawingCache();
                        mWv.freeMemory();
                        if (mWv.canGoBack()) {
                            mWv.goBack();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    /* 当load有ssl层的https页面时，如果这个网站的安全证书在Android无法得到认证， *
                     * WebView就会变成一个空白页，而并不会像PC浏览器中那样跳出一个风险提示框              *
                     * 忽略证书的错误继续Load页面内容                                                                                          */
                     handler.proceed();
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                     super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                  super.onPageFinished(view, url);
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                    String processUrl = url;
                    if (!processUrl.startsWith("http://"))
                    {
                        processUrl = "http://" + url;
                    }
                    view.loadUrl(processUrl);
                    return true;
                }
            });

            // Set WebChromeClient
            mWv.setWebChromeClient(new WebChromeClient()
            {
                @Override
                public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
                    quotaUpdater.updateQuota(spaceNeeded * 2);
                }
            });

            mWv.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                    	long clickTime = SystemClock.uptimeMillis();
        				long dtime = clickTime - mLastClickTime;
        				if (mLastClickTime == 0 || dtime < 3000) {
        					mCurrentClickCnts++;
        					mLastClickTime = clickTime;
        				} else {
        					mLastClickTime = 0;
        					mCurrentClickCnts = 0;
        				}

        				// When click times is more than 5, then popup the tools bar
        				if (mCurrentClickCnts > MAX_CLICK_CNTS) {
                            MainActivity.INSTANCE.showOsd();
        					mLastClickTime = 0;
        					mCurrentClickCnts = 0;
        					return true;
        				}
                    }
                   return false;
                }
            });

            this.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!v.hasFocus())
                    {
                        v.requestFocus();
                    }
                }
            });
        }
    }

    private void setUrl(final String url)
    {
        if (mWv != null)
        {
            mWv.loadUrl(url);
			mHandler.postDelayed(mRunnable, timeForPeriod*1000);
        }
    }

    // 捕捉返回键
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWv.canGoBack())
        {
            mWv.goBack();
            return true;
        }
        
        return false;
    }

    @Override
    public void onViewDestroy()
    {
    	if(mHandler!=null){
    		mHandler.removeCallbacks(mRunnable);
    	}
    	
        if (mWv != null)
        {
            mWv.destroy();
        }
        this.removeAllViews();
    }

    @Override
    public void onViewPause()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onViewResume()
    {
        // TODO Auto-generated method stub
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
        setUrl(mCurrentMedia.filePath);
    }
    
    @Override
    public void stopWork()
    {
    	
    }
}