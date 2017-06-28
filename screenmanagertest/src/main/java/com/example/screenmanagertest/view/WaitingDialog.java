package com.example.screenmanagertest.view;


import android.app.ProgressDialog;
import android.content.Context;

import com.example.screenmanagertest.R;

/**
 * 统一调用等待对话框的接口
 *  注：为增加效率没有增加同步机制，因此要保证在同一个线程下使用
 * @author Administrator
 *
 */
public class WaitingDialog{
    private static WaitingDialog mInstance;
    private static ProgressDialog mDialog;

    private WaitingDialog(){
    }

    public static WaitingDialog getInstance(){
        if(mInstance == null){
            mInstance = new WaitingDialog();
        }

        return mInstance;
    }

    //显示对话框
    public void show(Context context){
        if((mDialog != null) && mDialog.isShowing()){
            mDialog.dismiss();
        }

        mDialog = new ProgressDialog(context);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(context.getResources().getString(R.string.loading));
        // 设置ProgressDialog 的进度条为不明确
        mDialog.setIndeterminate(false);
        //不响应back键
        mDialog.setCancelable(false);
        mDialog.show();
    }
    //关闭对话框
    public void dissmiss(){
        if((mDialog != null) && mDialog.isShowing()){
            mDialog.dismiss();
        }
        mDialog = null;
    }
}
