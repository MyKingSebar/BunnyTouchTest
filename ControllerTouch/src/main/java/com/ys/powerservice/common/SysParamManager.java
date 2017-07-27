package com.ys.powerservice.common;

import android.text.TextUtils;


import com.ys.powerservice.BaseApplication;
import com.ys.powerservice.entity.SysParam;
import com.ys.powerservice.provider.DbHelper;
import com.ys.powerservice.utils.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by ld019890217 on 2017/4/19.
 */

public class SysParamManager {

    private SysParam mSysParam = null;
    private ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();

    private SysParamManager(){}

    private static class SysParamHolder{
        static final SysParamManager INSTANCE = new SysParamManager();
    }

    public static SysParamManager getInstance(){
        return SysParamHolder.INSTANCE;
    }

    public void initSysParam(){
        mReadWriteLock.writeLock().lock();
        mSysParam = DbHelper.getInstance().getSysParamFromDB();
        if(mSysParam == null){
            mSysParam = BaseApplication.getInstance().factoryRest();
            DbHelper.getInstance().saveSysParamToDB(mSysParam);
        }else {

        }
        mReadWriteLock.writeLock().unlock();
    }

    public void setOnOffTimeParam(final ConcurrentHashMap<String, String> onofftime){
        if (mSysParam != null && onofftime !=null) {
            mReadWriteLock.writeLock().lock();
            if (mSysParam.onOffTime != null){
                String week = null;
                String onTime = null;
                String offTime = null;
                int group = Integer.parseInt(onofftime.get("group"));
                for (int i = 0; i < group; i++){
                    week = onofftime.get("week"+(i+1));
                    onTime = onofftime.get("on_time" + (i+1));
                    offTime = onofftime.get("off_time" + (i +1));


                    mSysParam.onOffTime.put(("week" + (i + 1)), TextUtils.isEmpty(week) ? "" : week);
                    mSysParam.onOffTime.put(("on_time" + (i + 1)), TextUtils.isEmpty(onTime) ? "" : onTime);
                    mSysParam.onOffTime.put(("off_time" + (i + 1)), TextUtils.isEmpty(offTime) ? "" : offTime);
                }
                mSysParam.onOffTime.put("group",String.valueOf(group));
            }else {
                mSysParam.onOffTime = new ConcurrentHashMap<String, String>(onofftime);
            }

            DbHelper.getInstance().updateOnOffTime(mSysParam.onOffTime);
            mReadWriteLock.writeLock().unlock();
        }else {
            Logger.i("On&Off time param is null" );
        }
    }


    public ConcurrentHashMap<String, String> getOnOffTimeParam(){
        ConcurrentHashMap<String, String> onofftime = null;
        BaseApplication.getInstance().initAppParam();

        if(mSysParam !=null && mSysParam.onOffTime !=null){
            mReadWriteLock.readLock().lock();
            onofftime = new ConcurrentHashMap<String, String>(mSysParam.onOffTime);
            mReadWriteLock.readLock().unlock();
        }else {
            Logger.i("On&Off time param is null.");
        }

        return onofftime;
    }
}
