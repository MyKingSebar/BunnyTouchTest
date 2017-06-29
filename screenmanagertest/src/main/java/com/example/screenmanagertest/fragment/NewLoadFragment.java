package com.example.screenmanagertest.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.screenmanager.MediaInfoRef;
import com.example.screenmanagertest.screenmanager.SubWindowInfoRef;
import com.example.screenmanagertest.view.AudioView;
import com.example.screenmanagertest.view.DateTimeView;
import com.example.screenmanagertest.view.GalleryView;
import com.example.screenmanagertest.view.MarqueeView;
import com.example.screenmanagertest.view.MultiMediaView;
import com.example.screenmanagertest.view.PosterBaseView;
import com.example.screenmanagertest.view.TimerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by admin on 2017-06-29.
 */

public class NewLoadFragment extends SupportFragment {
    private String bundleKey="sublist";
    private ArrayList<SubWindowInfoRef> loadList=null;
    private FrameLayout mMainLayout = null;
    private Context context=null;
    private Set<PosterBaseView> mSubWndCollection = null; // 屏幕布局信息


    public static NewLoadFragment newInstance(ArrayList<SubWindowInfoRef> subWndList,Context context) {
//        this.context=context;
        NewLoadFragment fragment = new NewLoadFragment();
        Bundle bundle = new Bundle();
        ArrayList list = new ArrayList(); //这个list用于在budnle中传递 需要传递的ArrayList<Object>
        list.add(subWndList);
        list.add(context);
        bundle.putParcelableArrayList("bundleKey",list);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList list = bundle.getParcelableArrayList("bundleKey");
            if(list.size()>0){

                loadList= (ArrayList<SubWindowInfoRef>) list.get(0);//强转成你自己定义的list，这样list2就是你传过来的那个list了。
                this.context=(Context)list.get(1);
            }else{
                Log.i("jialei","loadList.size=0");
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newfragmentlayout, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        mMainLayout= (FrameLayout) view.findViewById(R.id.fl);
        mMainLayout.setBackgroundColor(Color.BLACK);
if(loadList!=null){

    loadNewProgram(loadList);
}else{
    Log.i("jialei","NewLoadFragment.loadList=null");
}
    }

    // 加载新节目
    public void loadNewProgram(ArrayList<SubWindowInfoRef> subWndList) {
        // Create new program windows
        if (subWndList != null) {
            Logger.i("Window number is: " + subWndList.size());

            // Clean old program
//            cleanupLayout();

            // initialize
            String touch=null;

            int xPos = 0;
            int yPos = 0;
            int width = 0;
            int height = 0;
            String wndName = null;
            String wndType = null;
            List<MediaInfoRef> mediaList = null;

            PosterBaseView tempSubWnd = null;
            mSubWndCollection = new HashSet<PosterBaseView>();

            // Through the sub window list, and create the correct view for it.
            for (SubWindowInfoRef subWndInfo : subWndList) {
                tempSubWnd = null;

                // 窗体类型和名称
                if ((wndType = subWndInfo.getSubWindowType()) == null) {
                    continue;
                }
                wndName = subWndInfo.getSubWindowName();

                touch=subWndInfo.getTouch();
                Log.i("jialei","loadNewProgram.touch:"+touch);
                // 窗体位置
                xPos = subWndInfo.getXPos();
                yPos = subWndInfo.getYPos();
                width = subWndInfo.getWidth();
                height = subWndInfo.getHeight();

                // 素材
                mediaList = subWndInfo.getSubWndMediaList();

                // 创建窗口
                if (wndType.contains("Main") || wndType.contains("StandbyScreen")) {
                    tempSubWnd = new MultiMediaView(context, true);
                } else if (wndType.contains("Image") || wndType.contains("Weather")) {
                    tempSubWnd = new MultiMediaView(context);
                } else if (wndType.contains("Audio")) {
                    tempSubWnd = new AudioView(context);
                } else if (wndType.contains("Scroll")) {
                    tempSubWnd = new MarqueeView(context);
                } else if (wndType.contains("Clock")) {
                    tempSubWnd = new DateTimeView(context);
                } else if (wndType.contains("Gallery")) {
                    tempSubWnd = new GalleryView(context);
                } else if (wndType.contains("Timer")) {
                    tempSubWnd = new TimerView(context);
                }

                // 设置窗口参数，并添加
                if (tempSubWnd != null) {
                    tempSubWnd.setViewTouch(touch);
                    tempSubWnd.setViewName(wndName);
                    tempSubWnd.setViewType(wndType);
                    tempSubWnd.setMediaList(mediaList);
                    tempSubWnd.setViewPosition(xPos, yPos);
                    tempSubWnd.setViewSize(width, height);
                    mMainLayout.addView(tempSubWnd);
                    mSubWndCollection.add(tempSubWnd);
                }
            }
        }

        if (mSubWndCollection != null) {
            for (PosterBaseView subWnd : mSubWndCollection) {
                subWnd.startWork();
            }
        }
    }


}
