package com.example.screenmanagertest.power.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ld019890217 on 2017/4/19.
 */

public class AdDbHelper extends SQLiteOpenHelper {

    public AdDbHelper(Context context){
        super(context, DbConstants.DATABASE_NAME, null, DbConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSysParamTable(db);
        createNetConInfoTable(db);
        createServerInfoTable(db);
        createSigOutInfoTable(db);
        createWifiInfoTable(db);
        createOnOffTimeTable(db);
        createOffDLTable(db);
        createAuthInfoTable(db);
        createPgmPathTable(db);
        createMutilcastTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_SYSPARAM);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_NETCONN);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_SERVINFO);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_SIGOUT);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_WIFICFG);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_ONOFFTIME);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_OFFDLTIME);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_AUTHINFO);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_PGM_PATH);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_MULTICAST);

        onCreate(db);
    }

    private void createSysParamTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_SYSPARAM + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + XmlCmdInfoRef.SETBIT + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.OSDLANGSET + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.GETTASKPERIOD + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.DELFILEPERIOD + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.TIMEZONE + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.SCRNROTATE + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.PASSWD + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.SYSPASSWD + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.DEVSEL + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.BRIGHTNESS + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.VOLUME + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.SWVER + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.HWVER + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.KERNELVER + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.CFEVER + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.CERTNUM + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.TERMMDL + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.TERM + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.TERMGRP + " TEXT DEFAULT NULL,"
                + XmlCmdInfoRef.DWNLDRSPD + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.CYCLETIME + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.DISPSCALE + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.AUTOGRADE + " INTEGER DEFAULT '-1',"
                + XmlCmdInfoRef.RUNMODER + " INTEGER DEFAULT '-1');");
    }

    private void createNetConInfoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_NETCONN + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.NCT_MODE + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_IP + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_GATEWAY + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_MASK + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_DNS1 + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_DNS2 + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_MODULE + " TEXT DEFAULT NULL,"
                + DbConstants.NCT_TIME + " TEXT DEFAULT NULL);");
    }

    private void createServerInfoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_SERVINFO + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.SIF_FTPIP + " TEXT DEFAULT NULL,"
                + DbConstants.SIF_FTPNAME + " TEXT DEFAULT NULL,"
                + DbConstants.SIF_FTPPORT + " TEXT DEFAULT NULL,"
                + DbConstants.SIF_FTPPWD + " TEXT DEFAULT NULL,"
                + DbConstants.SIF_NTPIP + " TEXT DEFAULT NULL,"
                + DbConstants.SIF_NTPPORT + " TEXT DEFAULT NULL,"
                + DbConstants.SIF_URL + " TEXT DEFAULT NULL);");
    }

    private void createSigOutInfoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_SIGOUT + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.SOT_MODE + " TEXT DEFAULT NULL,"
                + DbConstants.SOT_RPT + " TEXT DEFAULT NULL,"
                + DbConstants.SOT_VALUE + " TEXT DEFAULT NULL);");
    }

    private void createWifiInfoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_WIFICFG + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.WCG_SSID + " TEXT DEFAULT NULL,"
                + DbConstants.WCG_WPAPSK + " TEXT DEFAULT NULL,"
                + DbConstants.WCG_EPT + " TEXT DEFAULT NULL,"
                + DbConstants.WCG_AUTHMODE + " TEXT DEFAULT NULL);");
    }

    private void createOnOffTimeTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_ONOFFTIME + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.OFT_WEEK + " TEXT DEFAULT NULL,"
                + DbConstants.OFT_ONTIME + " TEXT DEFAULT NULL,"
                + DbConstants.OFT_OFFTIME + " TEXT DEFAULT NULL);");
    }

    private void createOffDLTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_OFFDLTIME + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.ODL_WEEK + " TEXT DEFAULT NULL,"
                + DbConstants.ODL_BTIME + " TEXT DEFAULT NULL,"
                + DbConstants.ODL_ETIME + " TEXT DEFAULT NULL);");
    }

    private void createAuthInfoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_AUTHINFO + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.AIT_MACADDR + " BLOB DEFAULT NULL,"
                + DbConstants.AIT_KEY + " BLOB DEFAULT NULL,"
                + DbConstants.AIT_AUTHCODE + " BLOB DEFAULT NULL);");
    }

    private void createPgmPathTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_PGM_PATH + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.PGM_PATH + " BLOB DEFAULT NULL);");
    }

    private void createMutilcastTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbConstants.TABLE_MULTICAST + " ("
                + DbConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConstants.MULT_SYNC_FLAG + " INTEGER DEFAULT '0',"
                + DbConstants.MULT_IP + " TEXT DEFAULT NULL,"
                + DbConstants.MULT_PORT + " INTEGER DEFAULT '0',"
                + DbConstants.MULT_LOCAL_PORT + " INTEGER DEFAULT '0',"
                + DbConstants.MULT_FOLLOWDELT + " INTEGER DEFAULT '0');");
    }
}
