package com.example.screenmanagertest.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.screenmanagertest.R;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class DialogUtil {

	public interface DialogThreeButtonListener {
		public void onFirstClick(Context context, View v, int which);

		public void onSecondClick(Context context, View v, int which);

		public void onThirdClick(Context context, View v, int which);

	}

	public interface DialogDoubleButtonListener {
		public void onLeftClick(Context context, View v, int which);

		public void onRightClick(Context context, View v, int which);
	}

	public interface DialogSingleButtonListener {
		public void onSingleClick(Context context, View v, int which);
	}

	// 标题，一个按钮
	public static Dialog showTipsDialog(Context context, String titleStr, String btnTextSingle, final DialogSingleButtonListener listener, final boolean cancelAble) {
		return showTipsDialog(context, titleStr, null, null, btnTextSingle, listener, cancelAble);
	}

	//标题，插入view,一个按钮
	public static Dialog showTipsDialog(Context context, String titleStr, View contentView, String btnTextSingle, final DialogSingleButtonListener listener, final boolean cancelAble) {
		return showTipsDialog(context, titleStr, null, contentView, btnTextSingle, listener, cancelAble);
	}
		
	
	// 标题，文本内容，一个按钮
	public static Dialog showTipsDialog(Context context, String titleStr, String descStr, String btnTextSingle, final DialogSingleButtonListener listener, final boolean cancelAble) {
		return showTipsDialog(context, titleStr, descStr, null, btnTextSingle, listener, cancelAble);
	}

	// 标题，两个按钮
	public static Dialog showTipsDialog(Context context, String titleStr, String btnTextLeft, String btnTextRight, final DialogDoubleButtonListener listener, final boolean cancelAble) {
		return showTipsDialog(context, titleStr, null, null, btnTextLeft, btnTextRight, listener, cancelAble);
	}

	// 标题，文本内容，两个按钮
	public static Dialog showTipsDialog(Context context, String titleStr, String descStr, String btnTextLeft, String btnTextRight, DialogDoubleButtonListener listener, boolean cancelAble) {
		return showTipsDialog(context, titleStr, descStr, null, btnTextLeft, btnTextRight, listener, cancelAble);
	}

	// 标题，插入view，两个按钮
	public static Dialog showTipsDialog(Context context, String titleStr, View contentView, String btnTextLeft, String btnTextRight, DialogDoubleButtonListener listener, boolean cancelAble) {
		Log.i("jialei","showTipsDialog1");
		return showTipsDialog(context, titleStr, null, contentView, btnTextLeft, btnTextRight, listener, cancelAble);
	}

	// 单按钮通用
	public static Dialog showTipsDialog(Context context, String titleStr, String descStr, View contentView, String btnTextSingle, final DialogSingleButtonListener listener, final boolean cancelAble) {
		Log.i("jialei","showTipsDialog2");
		return showTipsDialog(context, titleStr, descStr, contentView, btnTextSingle, null, new DialogDoubleButtonListener() {

			@Override
			public void onRightClick(Context context , View v , int which) {
			}

			@Override
			public void onLeftClick(Context context , View v , int which) {
				if (listener != null) {
					listener.onSingleClick(context, v, which);
				}
			}
		}, cancelAble);
	}

	// 双按钮通用
	public static Dialog showTipsDialog(Context context, String titleStr, String descStr, View contentView, String btnTextLeft, String btnTextRight, final DialogDoubleButtonListener listener, final boolean cancelAble) {
		return showTipsDialog(context, titleStr, descStr, contentView, btnTextLeft, btnTextRight, null, new DialogThreeButtonListener() {
			@Override
			public void onFirstClick(Context context , View v , int which) {
				if (listener != null) {
					listener.onLeftClick(context, v, which);
				}
			}

			@Override
			public void onSecondClick(Context context , View v , int which) {
				if (listener != null) {
					listener.onRightClick(context, v, which);
				}
			}

			@Override
			public void onThirdClick(Context context , View v ,int which) {
			}
		}, cancelAble);

	}

	// 三按钮通用
	@SuppressLint("InflateParams")
	public static Dialog showTipsDialog(final Context context, String titleStr, String descStr, final View contentView, String btnTextFirst, String btnTextSecond, String btnTextThird, final DialogThreeButtonListener listener, final boolean cancelAble) {
//		Looper.prepare();
		Dialog dlg = new Dialog(context, R.style.FloatNormalDialogStyle);
//		Looper.loop();
		LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_show_scan, null);
		TextView title = (TextView) layout.findViewById(R.id.dialog_title);
		TextView desc = (TextView) layout.findViewById(R.id.dialog_desc);
		RelativeLayout layoutContent = (RelativeLayout) layout.findViewById(R.id.layout_content);
		Button btnFirst = (Button) layout.findViewById(R.id.btn_first);
		Button btnSecond = (Button) layout.findViewById(R.id.btn_second);
		Button btnThird = (Button) layout.findViewById(R.id.btn_third);
		View lineBetweenBtn1 = layout.findViewById(R.id.line_between_btn1);
		View lineBetweenBtn2 = layout.findViewById(R.id.line_between_btn2);

		// 控制按键一是否显示
		if (TextUtils.isEmpty(btnTextFirst)) {
			btnFirst.setVisibility(View.GONE);
			lineBetweenBtn1.setVisibility(View.GONE);
		} else {
			btnFirst.setVisibility(View.VISIBLE);
			btnFirst.setText(btnTextFirst);
		}

		// 控制按键二是否显示
		if (TextUtils.isEmpty(btnTextSecond)) {
			btnSecond.setVisibility(View.GONE);
			lineBetweenBtn1.setVisibility(View.GONE);
		} else {
			btnSecond.setVisibility(View.VISIBLE);
			lineBetweenBtn1.setVisibility(View.VISIBLE);
			btnSecond.setText(btnTextSecond);
		}

		// 控制按键三是否显示,默认隐藏
		if (!TextUtils.isEmpty(btnTextThird)) {

			btnThird.setVisibility(View.VISIBLE);
			lineBetweenBtn2.setVisibility(View.VISIBLE);
			btnThird.setText(btnTextThird);
		}

		// 控制是否显示标题
		if (TextUtils.isEmpty(titleStr)) {
			title.setVisibility(View.GONE);
		} else {
			title.setVisibility(View.VISIBLE);
			title.setText(titleStr);
		}

		// 控制是否显示内容
		if (!TextUtils.isEmpty(descStr)) {
			desc.setVisibility(View.VISIBLE);
			desc.setText(descStr);
		} else {
			desc.setVisibility(View.GONE);
		}

		// 是否在RelativeLayout插入特殊内容
		if (contentView != null) {
			layoutContent.setVisibility(View.VISIBLE);
			layoutContent.addView(contentView);
		}

		btnFirst.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onFirstClick(context , contentView, 0);
				}
			}
		});

		btnSecond.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onSecondClick(context, contentView, 0);
				}
			}
		});

		btnThird.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onThirdClick(context, contentView, 0);
				}
			}
		});

		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(cancelAble);
		dlg.setContentView(layout);
		return dlg;
	}

	public static void dialogTimeOff(final Dialog dlg,int time){
		dialogTimeOff(dlg, null, time);
	}
	
	public static void dialogTimeOff(final Dialog dlg,final View v, int time)
	{
		if (dlg.isShowing()) 
		{
			Timer quitHomeTimer = new Timer("dialog");
			quitHomeTimer.schedule(new TimerTask() {
				public void run() 
				{
					hideInputMethod(dlg.getContext(), v, dlg);
					dlg.dismiss();
				}
			}, time);
		}
	}

	
	public static void hideInputMethod(Context context, View v, Dialog dialog) {
		if (context == null || v == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		try {
			Field field = dialog.getClass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
