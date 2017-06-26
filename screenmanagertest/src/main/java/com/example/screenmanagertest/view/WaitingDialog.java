package com.example.screenmanagertest.view;


import android.app.ProgressDialog;
import android.content.Context;

import com.example.screenmanagertest.R;

/**
 * ͳһ���õȴ��Ի���Ľӿ�
 *  ע��Ϊ����Ч��û������ͬ�����ƣ����Ҫ��֤��ͬһ���߳���ʹ��
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

    //��ʾ�Ի���
    public void show(Context context){
        if((mDialog != null) && mDialog.isShowing()){
            mDialog.dismiss();
        }

        mDialog = new ProgressDialog(context);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(context.getResources().getString(R.string.loading));
        // ����ProgressDialog �Ľ�����Ϊ����ȷ
        mDialog.setIndeterminate(false);
        //����Ӧback��
        mDialog.setCancelable(false);
        mDialog.show();
    }
    //�رնԻ���
    public void dissmiss(){
        if((mDialog != null) && mDialog.isShowing()){
            mDialog.dismiss();
        }
        mDialog = null;
    }
}
