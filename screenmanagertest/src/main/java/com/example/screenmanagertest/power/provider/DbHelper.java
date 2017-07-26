package com.example.screenmanagertest.power.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;


import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.power.entity.SysParam;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ld019890217 on 2017/4/19.
 */

public class DbHelper {

    private static final int DEFAULT_SYSPARAM_DBID = 1;

    private ContentResolver mContentResolver;

    private DbHelper() {
        mContentResolver = PosterApplication.getInstance().getContentResolver();
    }

    private static class DbHolder {
        static final DbHelper INSTANCE = new DbHelper();
    }

    public static DbHelper getInstance() {
        Log.i("jialei","DbHolder.INSTANCE==null"+(DbHolder.INSTANCE==null));
        return DbHolder.INSTANCE;
    }

    public byte[] getMac()
    {
        byte[] mac = null;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_AUTHINFO, null, null, null, null);
        if (c.moveToFirst())
        {
            mac = c.getBlob(c.getColumnIndex(DbConstants.AIT_MACADDR));
        }
        c.close();
        return mac;
    }

    public void setMac(byte[] mac)
    {
        if (mac != null)
        {
            Cursor c = mContentResolver.query(DbConstants.CONTENTURI_AUTHINFO, null, null, null, null);

            if (c.moveToFirst())
            {
                long id = c.getLong(c.getColumnIndex(DbConstants._ID));
                ContentValues cv = new ContentValues();
                cv.put(DbConstants.AIT_MACADDR, mac);
                mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_AUTHINFO, id), cv, null, null);
            }
            else
            {
                ContentValues cv = new ContentValues();
                cv.put(DbConstants.AIT_MACADDR, mac);
                mContentResolver.insert(DbConstants.CONTENTURI_AUTHINFO, cv);
            }

            c.close();
        }
    }

    public String getAuthKey()
    {
        String strKey = null;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_AUTHINFO, null, null, null, null);
        if (c.moveToFirst())
        {
            byte[] authKey = c.getBlob(c.getColumnIndex(DbConstants.AIT_KEY));
            if (authKey != null)
            {
                try
                {
                    strKey = new String(authKey, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        }
        c.close();

        return strKey;
    }

    public void setAuthKey(String key)
    {
        if (TextUtils.isEmpty(key))
        {
            Logger.i("Auth Key is null.");
            return;
        }

        byte[] keydata = null;
        try
        {
            keydata = key.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (keydata == null)
        {
            Logger.i("auth key bytes data is null.");
            return;
        }

        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_AUTHINFO, null, null, null, null);
        if (c.moveToFirst())
        {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            ContentValues cv = new ContentValues();
            cv.put(DbConstants.AIT_KEY, keydata);
            mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_AUTHINFO, id), cv, null, null);
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(DbConstants.AIT_KEY, keydata);
            mContentResolver.insert(DbConstants.CONTENTURI_AUTHINFO, cv);
        }
        c.close();
    }

    public String getAuthCode()
    {
        String strCode = null;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_AUTHINFO, null, null, null, null);
        if (c.moveToFirst())
        {
            byte[] authCode = c.getBlob(c.getColumnIndex(DbConstants.AIT_AUTHCODE));
            if (authCode != null)
            {
                try
                {
                    strCode = new String(authCode, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        }
        c.close();

        return strCode;
    }

    public void setAuthCode(String code)
    {
        if (TextUtils.isEmpty(code))
        {
            Logger.i("Auth Code is null.");
            return;
        }

        byte[] codedata = null;
        try
        {
            codedata = code.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (codedata == null)
        {
            Logger.i("auth code bytes data is null.");
            return;
        }

        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_AUTHINFO, null, null, null, null);
        if (c.moveToFirst())
        {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            ContentValues cv = new ContentValues();
            cv.put(DbConstants.AIT_AUTHCODE, codedata);
            mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_AUTHINFO, id), cv, null, null);
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(DbConstants.AIT_AUTHCODE, codedata);
            mContentResolver.insert(DbConstants.CONTENTURI_AUTHINFO, cv);
        }
        c.close();
    }

    public int getPgmSyncFlagFromDB()
    {
        int pgmSyncFlag = 0;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST, null, null, null, null);
        if (c.moveToFirst())
        {
            pgmSyncFlag = c.getInt(c.getColumnIndex(DbConstants.MULT_SYNC_FLAG));
        }
        c.close();
        return pgmSyncFlag;
    }

    public String getBcastIpFromDB()
    {
        String bcastIp = null;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST, null, null, null, null);
        if (c.moveToFirst())
        {
            bcastIp = c.getString(c.getColumnIndex(DbConstants.MULT_IP));
        }
        c.close();
        return bcastIp;
    }

    public int getBcastPortFromDB()
    {
        int bcastPort = 0;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST, null, null, null, null);
        if (c.moveToFirst())
        {
            bcastPort = c.getInt(c.getColumnIndex(DbConstants.MULT_PORT));
        }
        c.close();

        return bcastPort;
    }

    public int getBcastLocalPortFromDB()
    {
        int bcastLocalPort = 0;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST, null, null, null, null);
        if (c.moveToFirst())
        {
            bcastLocalPort = c.getInt(c.getColumnIndex(DbConstants.MULT_LOCAL_PORT));
        }
        c.close();

        return bcastLocalPort;
    }

    public int getBcastFollowFromDB()
    {
        int bcastFollow = 0;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST, null, null, null, null);
        if (c.moveToFirst())
        {
            bcastFollow = c.getInt(c.getColumnIndex(DbConstants.MULT_FOLLOWDELT));
        }
        c.close();

        return bcastFollow;
    }

    public void saveBcastParamToDB(int syncFlag, int port, int localPort, int follow, String ip)
    {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.MULT_SYNC_FLAG, syncFlag);
        cv.put(DbConstants.MULT_PORT, port);
        cv.put(DbConstants.MULT_LOCAL_PORT, localPort);
        cv.put(DbConstants.MULT_FOLLOWDELT, follow);
        if (!TextUtils.isEmpty(ip))
        {
            cv.put(DbConstants.MULT_IP, ip);
        }

        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST, null, null, null, null);
        if (c.moveToFirst())
        {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_MULTICAST, id), cv, null, null);
        }
        else
        {
            mContentResolver.insert(DbConstants.CONTENTURI_MULTICAST, cv);
        }
        c.close();
    }

    //保存同步状态到数据库
    public void saveSyncFlagToDb(int syncFlag) {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.MULT_SYNC_FLAG, syncFlag);
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST,
                null, null, null, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(
                    DbConstants.CONTENTURI_MULTICAST, id), cv, null, null);
        } else {
            mContentResolver.insert(DbConstants.CONTENTURI_MULTICAST, cv);
        }
        c.close();
    }

    //保存本地图案口到数据库
    public void saveSyncPortToDb(int port) {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.MULT_PORT, port);
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST,
                null, null, null, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(
                    DbConstants.CONTENTURI_MULTICAST, id), cv, null, null);
        } else {
            mContentResolver.insert(DbConstants.CONTENTURI_MULTICAST, cv);
        }
        c.close();
    }

    public void saveSynLocalPortToDb(int localPort) {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.MULT_LOCAL_PORT, localPort);
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST,
                null, null, null, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(
                    DbConstants.CONTENTURI_MULTICAST, id), cv, null, null);
        } else {
            mContentResolver.insert(DbConstants.CONTENTURI_MULTICAST, cv);
        }
        c.close();
    }

    public void saveSynFollowToDb(int follow) {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.MULT_FOLLOWDELT, follow);
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST,
                null, null, null, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(
                    DbConstants.CONTENTURI_MULTICAST, id), cv, null, null);
        } else {
            mContentResolver.insert(DbConstants.CONTENTURI_MULTICAST, cv);
        }
        c.close();
    }

    //保存同步组播地址IP到数据库
    public void saveSyncIpToDb(String ip) {
        ContentValues cv = new ContentValues();
        cv.put(DbConstants.MULT_IP, ip);
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_MULTICAST,
                null, null, null, null);
        if (c.moveToFirst()) {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(
                    DbConstants.CONTENTURI_MULTICAST, id), cv, null, null);
        } else {
            mContentResolver.insert(DbConstants.CONTENTURI_MULTICAST, cv);
        }
        c.close();
    }

    public SysParam getSysParamFromDB()
    {
        SysParam sysParam = new SysParam();
        Log.i("jialei","mContentResolver==null"+(mContentResolver==null));
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_SYSPARAM, null, null, null, null);
        Log.i("jialei","Cursor==null"+(c==null));
        if (c.moveToFirst())
        {
            sysParam.setBit = c.getInt(c.getColumnIndex(XmlCmdInfoRef.SETBIT));
            sysParam.osdLangSetosd_lang = c.getInt(c.getColumnIndex(XmlCmdInfoRef.OSDLANGSET));
            sysParam.getTaskPeriodtime = c.getInt(c.getColumnIndex(XmlCmdInfoRef.GETTASKPERIOD));
            sysParam.delFilePeriodtime = c.getInt(c.getColumnIndex(XmlCmdInfoRef.DELFILEPERIOD));
            sysParam.timeZonevalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.TIMEZONE));
            sysParam.scrnRotatevalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.SCRNROTATE));
            sysParam.passwdvalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.PASSWD));
            sysParam.syspasswdvalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.SYSPASSWD));
            sysParam.devSelvalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.DEVSEL));
            sysParam.brightnessvalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.BRIGHTNESS));
            sysParam.volumevalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.VOLUME));
            sysParam.swVervalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.SWVER));
            sysParam.hwVervalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.HWVER));
            sysParam.kernelvervalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.KERNELVER));
            sysParam.cfevervalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.CFEVER));
            sysParam.certNumvalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.CERTNUM));
            sysParam.termmodelvalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.TERMMDL));
            sysParam.termname = c.getString(c.getColumnIndex(XmlCmdInfoRef.TERM));
            sysParam.termGrpvalue = c.getString(c.getColumnIndex(XmlCmdInfoRef.TERMGRP));
            sysParam.dwnLdrSpdvalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.DWNLDRSPD));
            sysParam.cycleTimevalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.CYCLETIME));
            sysParam.dispScalevalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.DISPSCALE));
            sysParam.autoupgradevalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.AUTOGRADE));
            sysParam.runmodevalue = c.getInt(c.getColumnIndex(XmlCmdInfoRef.RUNMODER));
            c.close();
        }
        else
        {
            c.close();
            return null;
        }

        Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_NETCONN, null, null, null, null);
        if (c1.moveToFirst())
        {
            String mode = c1.getString(c1.getColumnIndex(DbConstants.NCT_MODE));
            String ip = c1.getString(c1.getColumnIndex(DbConstants.NCT_IP));
            String gateway = c1.getString(c1.getColumnIndex(DbConstants.NCT_GATEWAY));
            String mask = c1.getString(c1.getColumnIndex(DbConstants.NCT_MASK));
            String dns1 = c1.getString(c1.getColumnIndex(DbConstants.NCT_DNS1));
            String dns2 = c1.getString(c1.getColumnIndex(DbConstants.NCT_DNS2));
            String module = c1.getString(c1.getColumnIndex(DbConstants.NCT_MODULE));
            String time = c1.getString(c1.getColumnIndex(DbConstants.NCT_TIME));
            if (sysParam.netConn == null)
            {
                sysParam.netConn = new ConcurrentHashMap<String, String>();
            }
            sysParam.netConn.put("mode", TextUtils.isEmpty(mode) ? "" : mode);
            sysParam.netConn.put("ip", TextUtils.isEmpty(ip) ? "0.0.0.0" : ip);
            if (!TextUtils.isEmpty(gateway))
            {
                sysParam.netConn.put("gateway", gateway);
            }
            if (!TextUtils.isEmpty(mask))
            {
                sysParam.netConn.put("mask", mask);
            }
            if (!TextUtils.isEmpty(dns1))
            {
                sysParam.netConn.put("dns1", dns1);
            }
            if (!TextUtils.isEmpty(dns2))
            {
                sysParam.netConn.put("dns2", dns2);
            }
            if (!TextUtils.isEmpty(module))
            {
                sysParam.netConn.put("module", module);
            }
            if (!TextUtils.isEmpty(time))
            {
                sysParam.netConn.put("time", time);
            }
        }
        c1.close();

        Cursor c2 = mContentResolver.query(DbConstants.CONTENTURI_SERVINFO, null, null, null, null);
        if (c2.moveToFirst())
        {
            String webUrl = c2.getString(c2.getColumnIndex(DbConstants.SIF_URL));
            String ftpIp = c2.getString(c2.getColumnIndex(DbConstants.SIF_FTPIP));
            String ftpName = c2.getString(c2.getColumnIndex(DbConstants.SIF_FTPNAME));
            String ftpPort = c2.getString(c2.getColumnIndex(DbConstants.SIF_FTPPORT));
            String ftpPwd = c2.getString(c2.getColumnIndex(DbConstants.SIF_FTPPWD));
            String ntpIp = c2.getString(c2.getColumnIndex(DbConstants.SIF_NTPIP));
            String ntpPort = c2.getString(c2.getColumnIndex(DbConstants.SIF_NTPPORT));
            if (sysParam.serverSet == null)
            {
                sysParam.serverSet = new ConcurrentHashMap<String, String>();
            }
            sysParam.serverSet.put("weburl", TextUtils.isEmpty(webUrl) ? "" : webUrl);
            sysParam.serverSet.put("ftpip", TextUtils.isEmpty(ftpIp) ? "0.0.0.0" : ftpIp);
            sysParam.serverSet.put("ftpport", TextUtils.isEmpty(ftpPort) ? "" : ftpPort);
            sysParam.serverSet.put("ftpname", TextUtils.isEmpty(ftpName) ? "" : ftpName);
            sysParam.serverSet.put("ftppasswd", TextUtils.isEmpty(ftpPwd) ? "" : ftpPwd);
            sysParam.serverSet.put("ntpip", TextUtils.isEmpty(ntpIp) ? "" : ntpIp);
            sysParam.serverSet.put("ntpport", TextUtils.isEmpty(ntpPort) ? "" : ntpPort);
        }
        c2.close();

        Cursor c3 = mContentResolver.query(DbConstants.CONTENTURI_SIGOUT, null, null, null, null);
        if (c3.moveToFirst())
        {
            String mode = c3.getString(c3.getColumnIndex(DbConstants.SOT_MODE));
            String value = c3.getString(c3.getColumnIndex(DbConstants.SOT_VALUE));
            String rpt = c3.getString(c3.getColumnIndex(DbConstants.SOT_RPT));
            if (sysParam.sigOutSet == null)
            {
                sysParam.sigOutSet = new ConcurrentHashMap<String, String>();
            }
            sysParam.sigOutSet.put("mode", TextUtils.isEmpty(mode) ? "" : mode);
            sysParam.sigOutSet.put("value", TextUtils.isEmpty(value) ? "" : value);
            sysParam.sigOutSet.put("repratio", TextUtils.isEmpty(rpt) ? "" : rpt);
        }
        c3.close();

        Cursor c4 = mContentResolver.query(DbConstants.CONTENTURI_WIFICFG, null, null, null, null);
        if (c4.moveToFirst())
        {
            String ssid = c4.getString(c4.getColumnIndex(DbConstants.WCG_SSID));
            String wpapsk = c4.getString(c4.getColumnIndex(DbConstants.WCG_WPAPSK));
            String authmode = c4.getString(c4.getColumnIndex(DbConstants.WCG_AUTHMODE));
            String type = c4.getString(c4.getColumnIndex(DbConstants.WCG_EPT));
            if (sysParam.wifiSet == null)
            {
                sysParam.wifiSet = new ConcurrentHashMap<String, String>();
            }
            sysParam.wifiSet.put("ssid", TextUtils.isEmpty(ssid) ? "" : ssid);
            sysParam.wifiSet.put("wpapsk", TextUtils.isEmpty(wpapsk) ? "" : wpapsk);
            sysParam.wifiSet.put("authmode", TextUtils.isEmpty(authmode) ? "" : authmode);
            sysParam.wifiSet.put("encryptype", TextUtils.isEmpty(type) ? "" : type);
        }
        c4.close();

        Cursor c5 = mContentResolver.query(DbConstants.CONTENTURI_ONOFFTIME, null, null, null, null);
        if (c5.moveToFirst())
        {
            int groupid = 0;
            String week = null;
            String ontime = null;
            String offtime = null;
            if (sysParam.onOffTime == null)
            {
                sysParam.onOffTime = new ConcurrentHashMap<String, String>();
            }
            while (!c5.isAfterLast())
            {
                week = c5.getString(c5.getColumnIndex(DbConstants.OFT_WEEK));
                ontime = c5.getString(c5.getColumnIndex(DbConstants.OFT_ONTIME));
                offtime = c5.getString(c5.getColumnIndex(DbConstants.OFT_OFFTIME));
                if (!TextUtils.isEmpty(ontime) && !TextUtils.isEmpty(offtime))
                {
                    groupid++;
                    sysParam.onOffTime.put(("week" + groupid), TextUtils.isEmpty(week) ? "" : week);
                    sysParam.onOffTime.put(("on_time" + groupid), TextUtils.isEmpty(ontime) ? "" : ontime);
                    sysParam.onOffTime.put(("off_time" + groupid), TextUtils.isEmpty(offtime) ? "" : offtime);
                }
                c5.moveToNext();
            }
            sysParam.onOffTime.put("group", String.valueOf(groupid));
        }
        else
        {
            if (sysParam.onOffTime == null)
            {
                sysParam.onOffTime = new ConcurrentHashMap<String, String>();
            }
            sysParam.onOffTime.put("group", "0");
        }
        c5.close();

        Cursor c6 = mContentResolver.query(DbConstants.CONTENTURI_OFFDLTIME, null, null, null, null);
        if (c6.moveToFirst())
        {
            int groupid = 0;
            String week = null;
            String ontime = null;
            String offtime = null;
            if (sysParam.offdlTime == null)
            {
                sysParam.offdlTime = new ConcurrentHashMap<String, String>();
            }
            while (!c6.isAfterLast())
            {
                week = c6.getString(c6.getColumnIndex(DbConstants.ODL_WEEK));
                ontime = c6.getString(c6.getColumnIndex(DbConstants.ODL_BTIME));
                offtime = c6.getString(c6.getColumnIndex(DbConstants.ODL_ETIME));
                if (!TextUtils.isEmpty(ontime) && !TextUtils.isEmpty(offtime))
                {
                    groupid++;
                    sysParam.offdlTime.put(("week" + groupid), TextUtils.isEmpty(week) ? "" : week);
                    sysParam.offdlTime.put(("on_time" + groupid), TextUtils.isEmpty(ontime) ? "" : ontime);
                    sysParam.offdlTime.put(("off_time" + groupid), TextUtils.isEmpty(offtime) ? "" : offtime);
                }
                c6.moveToNext();
            }
            sysParam.offdlTime.put("group", String.valueOf(groupid));
        }
        else
        {
            if (sysParam.offdlTime == null)
            {
                sysParam.offdlTime = new ConcurrentHashMap<String, String>();
            }
            sysParam.offdlTime.put("group", "0");
        }
        c6.close();

        return sysParam;
    }


    public String getPgmPath()
    {
        String strPath = null;
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_PGMPATH, null, null, null, null);
        if (c.moveToFirst())
        {
            byte[] path = c.getBlob(c.getColumnIndex(DbConstants.PGM_PATH));
            if (path != null)
            {
                try
                {
                    strPath = new String(path, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        }
        c.close();

        return strPath;
    }

    public void setPgmPath(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            Logger.i("Program path is null.");
            return;
        }

        byte[] codedata = null;
        try
        {
            codedata = path.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (codedata == null)
        {
            Logger.i("Program path bytes data is null.");
            return;
        }

        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_PGMPATH, null, null, null, null);
        if (c.moveToFirst())
        {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            ContentValues cv = new ContentValues();
            cv.put(DbConstants.PGM_PATH, codedata);
            mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_PGMPATH, id), cv, null, null);
        }
        else
        {
            ContentValues cv = new ContentValues();
            cv.put(DbConstants.PGM_PATH, codedata);
            mContentResolver.insert(DbConstants.CONTENTURI_PGMPATH, cv);
        }
        c.close();
    }


    public void saveSysParamToDB(final SysParam sysParam)
    {
        if (sysParam == null)
        {
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.SETBIT, sysParam.setBit);
        cv.put(XmlCmdInfoRef.OSDLANGSET, sysParam.osdLangSetosd_lang);
        cv.put(XmlCmdInfoRef.GETTASKPERIOD, sysParam.getTaskPeriodtime);
        cv.put(XmlCmdInfoRef.DELFILEPERIOD, sysParam.delFilePeriodtime);
        if (!TextUtils.isEmpty(sysParam.timeZonevalue))
        {
            cv.put(XmlCmdInfoRef.TIMEZONE, sysParam.timeZonevalue);
        }
        cv.put(XmlCmdInfoRef.SCRNROTATE, sysParam.scrnRotatevalue);
        if (!TextUtils.isEmpty(sysParam.passwdvalue))
        {
            cv.put(XmlCmdInfoRef.PASSWD, sysParam.passwdvalue);
        }
        if (!TextUtils.isEmpty(sysParam.syspasswdvalue))
        {
            cv.put(XmlCmdInfoRef.SYSPASSWD, sysParam.syspasswdvalue);
        }
        cv.put(XmlCmdInfoRef.DEVSEL, sysParam.devSelvalue);
        cv.put(XmlCmdInfoRef.BRIGHTNESS, sysParam.brightnessvalue);
        cv.put(XmlCmdInfoRef.VOLUME, sysParam.volumevalue);
        if (!TextUtils.isEmpty(sysParam.swVervalue))
        {
            cv.put(XmlCmdInfoRef.SWVER, sysParam.swVervalue);
        }
        if (!TextUtils.isEmpty(sysParam.hwVervalue))
        {
            cv.put(XmlCmdInfoRef.HWVER, sysParam.hwVervalue);
        }
        if (!TextUtils.isEmpty(sysParam.kernelvervalue))
        {
            cv.put(XmlCmdInfoRef.KERNELVER, sysParam.kernelvervalue);
        }
        if (!TextUtils.isEmpty(sysParam.cfevervalue))
        {
            cv.put(XmlCmdInfoRef.CFEVER, sysParam.cfevervalue);
        }
        if (!TextUtils.isEmpty(sysParam.certNumvalue))
        {
            cv.put(XmlCmdInfoRef.CERTNUM, sysParam.certNumvalue);
        }
        if (!TextUtils.isEmpty(sysParam.termmodelvalue))
        {
            cv.put(XmlCmdInfoRef.TERMMDL, sysParam.termmodelvalue);
        }
        if (!TextUtils.isEmpty(sysParam.termname))
        {
            cv.put(XmlCmdInfoRef.TERM, sysParam.termname);
        }
        if (!TextUtils.isEmpty(sysParam.termGrpvalue))
        {
            cv.put(XmlCmdInfoRef.TERMGRP, sysParam.termGrpvalue);
        }
        cv.put(XmlCmdInfoRef.DWNLDRSPD, sysParam.dwnLdrSpdvalue);
        cv.put(XmlCmdInfoRef.CYCLETIME, sysParam.cycleTimevalue);
        cv.put(XmlCmdInfoRef.DISPSCALE, sysParam.dispScalevalue);
        cv.put(XmlCmdInfoRef.AUTOGRADE, sysParam.autoupgradevalue);
        cv.put(XmlCmdInfoRef.RUNMODER, sysParam.runmodevalue);
        Cursor c = mContentResolver.query(DbConstants.CONTENTURI_SYSPARAM, null, null, null, null);
        if (c.moveToFirst())
        {
            long id = c.getLong(c.getColumnIndex(DbConstants._ID));
            mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_SYSPARAM, id), cv, null, null);
        }
        else
        {
            mContentResolver.insert(DbConstants.CONTENTURI_SYSPARAM, cv);
        }
        c.close();

        if (sysParam.netConn != null)
        {
            cv = new ContentValues();
            String mode = sysParam.netConn.get("mode");
            String ip = sysParam.netConn.get("ip");
            String gateway = sysParam.netConn.get("gateway");
            String mask = sysParam.netConn.get("mask");
            String dns1 = sysParam.netConn.get("dns1");
            String dns2 = sysParam.netConn.get("dns2");
            String module = sysParam.netConn.get("module");
            String time = sysParam.netConn.get("time");

            if (!TextUtils.isEmpty(mode))
            {
                cv.put(DbConstants.NCT_MODE, mode);
            }
            if (!TextUtils.isEmpty(ip))
            {
                cv.put(DbConstants.NCT_IP, ip);
            }
            if (!TextUtils.isEmpty(gateway))
            {
                cv.put(DbConstants.NCT_GATEWAY, gateway);
            }
            if (!TextUtils.isEmpty(mask))
            {
                cv.put(DbConstants.NCT_MASK, mask);
            }
            if (!TextUtils.isEmpty(dns1))
            {
                cv.put(DbConstants.NCT_DNS1, dns1);
            }
            if (!TextUtils.isEmpty(dns2))
            {
                cv.put(DbConstants.NCT_DNS2, dns2);
            }
            if (!TextUtils.isEmpty(module))
            {
                cv.put(DbConstants.NCT_MODULE, module);
            }
            if (!TextUtils.isEmpty(time))
            {
                cv.put(DbConstants.NCT_TIME, time);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_NETCONN, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_NETCONN, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_NETCONN, cv);
                }
                c1.close();
            }
        }

        if (sysParam.serverSet != null)
        {
            cv = new ContentValues();
            String webUrl = sysParam.serverSet.get("weburl");
            String ftpIp = sysParam.serverSet.get("ftpip");
            String ftpName = sysParam.serverSet.get("ftpname");
            String ftpPort = sysParam.serverSet.get("ftpport");
            String ftpPwd = sysParam.serverSet.get("ftppasswd");
            String ntpIp = sysParam.serverSet.get("ntpip");
            String ntpPort = sysParam.serverSet.get("ntpport");

            if (!TextUtils.isEmpty(webUrl))
            {
                cv.put(DbConstants.SIF_URL, webUrl);
            }
            if (!TextUtils.isEmpty(ftpIp))
            {
                cv.put(DbConstants.SIF_FTPIP, ftpIp);
            }
            if (!TextUtils.isEmpty(ftpName))
            {
                cv.put(DbConstants.SIF_FTPNAME, ftpName);
            }
            if (!TextUtils.isEmpty(ftpPort))
            {
                cv.put(DbConstants.SIF_FTPPORT, ftpPort);
            }
            if (!TextUtils.isEmpty(ftpPwd))
            {
                cv.put(DbConstants.SIF_FTPPWD, ftpPwd);
            }
            if (!TextUtils.isEmpty(ntpIp))
            {
                cv.put(DbConstants.SIF_NTPIP, ntpIp);
            }
            if (!TextUtils.isEmpty(ntpPort))
            {
                cv.put(DbConstants.SIF_NTPPORT, ntpPort);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_SERVINFO, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_SERVINFO, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_SERVINFO, cv);
                }
                c1.close();
            }
        }

        if (sysParam.wifiSet != null)
        {
            cv = new ContentValues();
            String ssid = sysParam.wifiSet.get("ssid");
            String wpapsk = sysParam.wifiSet.get("wpapsk");
            String auth = sysParam.wifiSet.get("authmode");
            String type = sysParam.wifiSet.get("encryptype");

            if (!TextUtils.isEmpty(ssid))
            {
                cv.put(DbConstants.WCG_SSID, ssid);
            }
            if (!TextUtils.isEmpty(wpapsk))
            {
                cv.put(DbConstants.WCG_WPAPSK, wpapsk);
            }
            if (!TextUtils.isEmpty(auth))
            {
                cv.put(DbConstants.WCG_AUTHMODE, auth);
            }
            if (!TextUtils.isEmpty(type))
            {
                cv.put(DbConstants.WCG_EPT, type);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_WIFICFG, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_WIFICFG, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_WIFICFG, cv);
                }
                c1.close();
            }
        }

        if (sysParam.sigOutSet != null)
        {
            cv = new ContentValues();
            String mode = sysParam.sigOutSet.get("mode");
            String value = sysParam.sigOutSet.get("value");
            String rpt = sysParam.sigOutSet.get("repratio");

            if (!TextUtils.isEmpty(mode))
            {
                cv.put(DbConstants.SOT_MODE, mode);
            }
            if (!TextUtils.isEmpty(value))
            {
                cv.put(DbConstants.SOT_VALUE, value);
            }
            if (!TextUtils.isEmpty(rpt))
            {
                cv.put(DbConstants.SOT_RPT, rpt);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_SIGOUT, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_SIGOUT, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_SIGOUT, cv);
                }
                c1.close();
            }
        }

        if (sysParam.onOffTime != null)
        {
            String week = null;
            String onTime = null;
            String offTime = null;
            mContentResolver.delete(DbConstants.CONTENTURI_ONOFFTIME, null, null);
            int nGroup = Integer.parseInt(sysParam.onOffTime.get("group"));
            for (int i = 1; i <= nGroup; i++)
            {
                cv = new ContentValues();
                week = sysParam.onOffTime.get("week" + i);
                onTime = sysParam.onOffTime.get("on_time" + i);
                offTime = sysParam.onOffTime.get("off_time" + i);

                if (!TextUtils.isEmpty(week))
                {
                    cv.put(DbConstants.OFT_WEEK, week);
                }
                if (!TextUtils.isEmpty(onTime))
                {
                    cv.put(DbConstants.OFT_ONTIME, onTime);
                }
                if (!TextUtils.isEmpty(offTime))
                {
                    cv.put(DbConstants.OFT_OFFTIME, offTime);
                }

                if (cv.size() > 0)
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_ONOFFTIME, cv);
                }
            }
        }

        if (sysParam.offdlTime != null)
        {
            String week = null;
            String onTime = null;
            String offTime = null;
            mContentResolver.delete(DbConstants.CONTENTURI_OFFDLTIME, null, null);
            int nGroup = Integer.parseInt(sysParam.offdlTime.get("group"));
            for (int i = 1; i <= nGroup; i++)
            {
                cv = new ContentValues();
                week = sysParam.offdlTime.get("week" + i);
                onTime = sysParam.offdlTime.get("on_time" + i);
                offTime = sysParam.offdlTime.get("off_time" + i);

                if (!TextUtils.isEmpty(week))
                {
                    cv.put(DbConstants.ODL_WEEK, week);
                }
                if (!TextUtils.isEmpty(onTime))
                {
                    cv.put(DbConstants.ODL_BTIME, onTime);
                }
                if (!TextUtils.isEmpty(offTime))
                {
                    cv.put(DbConstants.ODL_ETIME, offTime);
                }

                if (cv.size() > 0)
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_OFFDLTIME, cv);
                }
            }
        }
    }

    public void updateSetBit(int setBit)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.SETBIT, setBit);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateOsdLang(int osdLang)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.OSDLANGSET, osdLang);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateGetTaskPeriodtime(int time)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.GETTASKPERIOD, time);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateDelFilePeriodtime(int time)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.DELFILEPERIOD, time);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateTimeZone(String zone)
    {
        if (TextUtils.isEmpty(zone))
        {
            Logger.i("Time zone is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.TIMEZONE, zone);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateScrnRotate(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.SCRNROTATE, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updatePasswd(String pwd)
    {
        if (TextUtils.isEmpty(pwd))
        {
            Logger.i("Password is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.PASSWD, pwd);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateSysPasswd(String pwd)
    {
        if (TextUtils.isEmpty(pwd))
        {
            Logger.i("Sys Password is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.SYSPASSWD, pwd);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateDevSel(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.DEVSEL, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateBrightness(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.BRIGHTNESS, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateVolume(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.VOLUME, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateSwVer(String version)
    {
        if (TextUtils.isEmpty(version))
        {
            Logger.i("Software version is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.SWVER, version);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateHwVer(String version)
    {
        if (TextUtils.isEmpty(version))
        {
            Logger.i("Hardware version is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.HWVER, version);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateKernelVer(String version)
    {
        if (TextUtils.isEmpty(version))
        {
            Logger.i("Kernel version is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.KERNELVER, version);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateCfeVer(String version)
    {
        if (TextUtils.isEmpty(version))
        {
            Logger.i("CFE version is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.CFEVER, version);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateCertNum(String cernum)
    {
        if (TextUtils.isEmpty(cernum))
        {
            Logger.i("Cert number is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.CERTNUM, cernum);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateTermMode(String mode)
    {
        if (TextUtils.isEmpty(mode))
        {
            Logger.i("Term Mode is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.TERMMDL, mode);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateTermName(String name)
    {
        if (TextUtils.isEmpty(name))
        {
            Logger.i("Term Name is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.TERM, name);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateTermGrp(String grp)
    {
        if (TextUtils.isEmpty(grp))
        {
            Logger.i("Term Group is null.");
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.TERMGRP, grp);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateDwnLdrSpd(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.DWNLDRSPD, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateCycleTime(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.CYCLETIME, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateDispScale(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.DISPSCALE, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateAutoUpgrade(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.AUTOGRADE, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateRunMode(int value)
    {
        ContentValues cv = new ContentValues();
        cv.put(XmlCmdInfoRef.RUNMODER, value);
        mContentResolver.update(ContentUris.withAppendedId(
                DbConstants.CONTENTURI_SYSPARAM, DEFAULT_SYSPARAM_DBID), cv, null, null);
    }

    public void updateNetConn(final ConcurrentHashMap<String, String> netcon)
    {
        if (netcon != null)
        {
            ContentValues cv = new ContentValues();
            String mode = netcon.get("mode");
            String ip = netcon.get("ip");
            String gateway = netcon.get("gateway");
            String mask = netcon.get("mask");
            String dns1 = netcon.get("dns1");
            String dns2 = netcon.get("dns2");
            String module = netcon.get("module");
            String time = netcon.get("time");
            if (!TextUtils.isEmpty(mode))
            {
                cv.put(DbConstants.NCT_MODE, mode);
            }
            if (!TextUtils.isEmpty(ip))
            {
                cv.put(DbConstants.NCT_IP, ip);
            }
            if (!TextUtils.isEmpty(gateway))
            {
                cv.put(DbConstants.NCT_GATEWAY, gateway);
            }
            if (!TextUtils.isEmpty(mask))
            {
                cv.put(DbConstants.NCT_MASK, mask);
            }
            if (!TextUtils.isEmpty(dns1))
            {
                cv.put(DbConstants.NCT_DNS1, dns1);
            }
            if (!TextUtils.isEmpty(dns2))
            {
                cv.put(DbConstants.NCT_DNS2, dns2);
            }
            if (!TextUtils.isEmpty(module))
            {
                cv.put(DbConstants.NCT_MODULE, module);
            }
            if (!TextUtils.isEmpty(time))
            {
                cv.put(DbConstants.NCT_TIME, time);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_NETCONN, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_NETCONN, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_NETCONN, cv);
                }
                c1.close();
            }
        }
    }

    public void updateServerSet(final ConcurrentHashMap<String, String> serverSet)
    {
        if (serverSet != null)
        {
            ContentValues cv = new ContentValues();
            String webUrl = serverSet.get("weburl");
            String ftpIp = serverSet.get("ftpip");
            String ftpName = serverSet.get("ftpname");
            String ftpPort = serverSet.get("ftpport");
            String ftpPwd = serverSet.get("ftppasswd");
            String ntpIp = serverSet.get("ntpip");
            String ntpPort = serverSet.get("ntpport");

            if (!TextUtils.isEmpty(webUrl))
            {
                cv.put(DbConstants.SIF_URL, webUrl);
            }
            if (!TextUtils.isEmpty(ftpIp))
            {
                cv.put(DbConstants.SIF_FTPIP, ftpIp);
            }
            if (!TextUtils.isEmpty(ftpName))
            {
                cv.put(DbConstants.SIF_FTPNAME, ftpName);
            }
            if (!TextUtils.isEmpty(ftpPort))
            {
                cv.put(DbConstants.SIF_FTPPORT, ftpPort);
            }
            if (!TextUtils.isEmpty(ftpPwd))
            {
                cv.put(DbConstants.SIF_FTPPWD, ftpPwd);
            }
            if (!TextUtils.isEmpty(ntpIp))
            {
                cv.put(DbConstants.SIF_NTPIP, ntpIp);
            }
            if (!TextUtils.isEmpty(ntpPort))
            {
                cv.put(DbConstants.SIF_NTPPORT, ntpPort);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_SERVINFO, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_SERVINFO, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_SERVINFO, cv);
                }
                c1.close();
            }
        }
    }

    public void updateSigOutSet(final ConcurrentHashMap<String, String> sigOutSet)
    {
        if (sigOutSet != null)
        {
            ContentValues cv = new ContentValues();
            String mode = sigOutSet.get("mode");
            String value = sigOutSet.get("value");
            String rpt = sigOutSet.get("repratio");
            if (!TextUtils.isEmpty(mode))
            {
                cv.put(DbConstants.SOT_MODE, mode);
            }
            if (!TextUtils.isEmpty(value))
            {
                cv.put(DbConstants.SOT_VALUE, value);
            }
            if (!TextUtils.isEmpty(rpt))
            {
                cv.put(DbConstants.SOT_RPT, rpt);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_SIGOUT, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_SIGOUT, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_SIGOUT, cv);
                }
                c1.close();
            }
        }
    }

    public void updateWifiSet(final ConcurrentHashMap<String, String> wifiSet)
    {
        if (wifiSet != null)
        {
            ContentValues cv = new ContentValues();
            String ssid = wifiSet.get("ssid");
            String wpapsk = wifiSet.get("wpapsk");
            String auth = wifiSet.get("authmode");
            String type = wifiSet.get("encryptype");

            if (!TextUtils.isEmpty(ssid))
            {
                cv.put(DbConstants.WCG_SSID, ssid);
            }
            if (!TextUtils.isEmpty(wpapsk))
            {
                cv.put(DbConstants.WCG_WPAPSK, wpapsk);
            }
            if (!TextUtils.isEmpty(auth))
            {
                cv.put(DbConstants.WCG_AUTHMODE, auth);
            }
            if (!TextUtils.isEmpty(type))
            {
                cv.put(DbConstants.WCG_EPT, type);
            }

            if (cv.size() > 0)
            {
                Cursor c1 = mContentResolver.query(DbConstants.CONTENTURI_WIFICFG, null, null, null, null);
                if (c1.moveToFirst())
                {
                    long id = c1.getLong(c1.getColumnIndex(DbConstants._ID));
                    mContentResolver.update(ContentUris.withAppendedId(DbConstants.CONTENTURI_WIFICFG, id), cv, null, null);
                }
                else
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_WIFICFG, cv);
                }
                c1.close();
            }
        }
    }

    public void updateOnOffTime(final ConcurrentHashMap<String, String> onOffTime)
    {
        Log.i("jialei","updateOnOffTime");
        if (onOffTime != null)
        {
            ContentValues cv = null;
            String week = null;
            String onTime = null;
            String offTime = null;
            mContentResolver.delete(DbConstants.CONTENTURI_ONOFFTIME, null, null);
            int nGroup = Integer.parseInt(onOffTime.get("group"));
            for (int i = 1; i <= nGroup; i++)
            {
                cv = new ContentValues();
                week = onOffTime.get("week" + i);
                onTime = onOffTime.get("on_time" + i);
                offTime = onOffTime.get("off_time" + i);
                if (!TextUtils.isEmpty(week))
                {
                    cv.put(DbConstants.OFT_WEEK, week);
                }
                if (!TextUtils.isEmpty(onTime))
                {
                    cv.put(DbConstants.OFT_ONTIME, onTime);
                }
                if (!TextUtils.isEmpty(offTime))
                {
                    cv.put(DbConstants.OFT_OFFTIME, offTime);
                }

                if (cv.size() > 0)
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_ONOFFTIME, cv);
                }
            }
        }
    }

    public void updateOffdlTime(final ConcurrentHashMap<String, String> offdlTime)
    {
        if (offdlTime != null)
        {
            ContentValues cv = null;
            String week = null;
            String onTime = null;
            String offTime = null;
            mContentResolver.delete(DbConstants.CONTENTURI_OFFDLTIME, null, null);
            int nGroup = Integer.parseInt(offdlTime.get("group"));
            for (int i = 1; i <= nGroup; i++)
            {
                cv = new ContentValues();
                week = offdlTime.get("week" + i);
                onTime = offdlTime.get("on_time" + i);
                offTime = offdlTime.get("off_time" + i);
                if (!TextUtils.isEmpty(week))
                {
                    cv.put(DbConstants.ODL_WEEK, week);
                }
                if (!TextUtils.isEmpty(onTime))
                {
                    cv.put(DbConstants.ODL_BTIME, onTime);
                }
                if (!TextUtils.isEmpty(offTime))
                {
                    cv.put(DbConstants.ODL_ETIME, offTime);
                }

                if (cv.size() > 0)
                {
                    mContentResolver.insert(DbConstants.CONTENTURI_OFFDLTIME, cv);
                }
            }
        }
    }
}
