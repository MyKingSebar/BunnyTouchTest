package com.example.screenmanagertest.view;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.screenmanagertest.R;

public class RunningView extends FrameLayout {
	private ImageView mImageView;
	private String mLoadingTip;
	private TextView mLoadingTv;
	private AnimationDrawable mAnimation;
	
	public RunningView(Context context) {
		super(context);
		initView(context);
		initData();
	}

	private void initView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_pro_running, this);
		mImageView=(ImageView) this.findViewById(R.id.loadingIv);
		mLoadingTv=(TextView) this.findViewById(R.id.loadingTv);
	}
	
	private void initData(){
		mLoadingTip="节目更新中...";
		mImageView.setBackgroundResource(R.drawable.frame_run);

		// 通过ImageView对象拿到背景显示的AnimationDrawable
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		// 为了防止在onCreate方法中只显示第一帧的解决方案之一
		mImageView.post(new Runnable() {
			@Override
			public void run() {
				mAnimation.start();
			}
		});
		mLoadingTv.setText(mLoadingTip);
	}

	public RunningView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData();
	}
	
	public RunningView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
		initData();
	}
}
