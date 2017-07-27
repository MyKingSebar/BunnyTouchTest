package com.ys.powerservice.provider;

import android.net.Uri;

public class DbConstants {
    public static final String AUTHORITY              = "com.ys.poweronoff.provider";
    public static final String DATABASE_NAME          = "poweronoff.db";
    public static final int    DATABASE_VERSION       = 2;
    
    public static final String TABLE_SYSPARAM      = "sysparam";
    public static final String TABLE_NETCONN       = "netconnect";
    public static final String TABLE_SERVINFO      = "servinfo";
    public static final String TABLE_SIGOUT        = "sigout";
    public static final String TABLE_WIFICFG       = "wificfg";
    public static final String TABLE_ONOFFTIME     = "onofftime";
    public static final String TABLE_OFFDLTIME     = "offdltime";
    public static final String TABLE_AUTHINFO      = "authinfo";
    public static final String TABLE_PGM_PATH      = "pgmpath";
    public static final String TABLE_MULTICAST     = "multicast";
    
    public static final String _ID                 = "_id";
    
    public static final String NCT_MODE             = "mode";
    public static final String NCT_IP               = "ip";
    public static final String NCT_MASK             = "mask";
    public static final String NCT_GATEWAY          = "gateway";
    public static final String NCT_DNS1             = "dns1";
    public static final String NCT_DNS2             = "dns2";
    public static final String NCT_MODULE           = "module";
    public static final String NCT_TIME             = "time";
    
    public static final String SIF_URL             = "weburl";
    public static final String SIF_FTPNAME         = "ftpname";
    public static final String SIF_FTPPORT         = "ftpport";
    public static final String SIF_FTPIP           = "ftpip";
    public static final String SIF_FTPPWD          = "ftppwd";
    public static final String SIF_NTPIP           = "ntpip";
    public static final String SIF_NTPPORT         = "ntpport";
    
    public static final String SOT_MODE            = "mode";
    public static final String SOT_VALUE           = "value";
    public static final String SOT_RPT             = "repratio";
    
    public static final String WCG_SSID            = "ssid";
    public static final String WCG_WPAPSK          = "wpapsk";
    public static final String WCG_AUTHMODE        = "authmode";
    public static final String WCG_EPT             = "encryptype";
    
    public static final String OFT_WEEK            = "week";
    public static final String OFT_ONTIME          = "ontime";
    public static final String OFT_OFFTIME         = "offtime";
    
    public static final String ODL_WEEK            = "week";
    public static final String ODL_BTIME           = "begaintime";
    public static final String ODL_ETIME           = "endtime";
    
    public static final String AIT_MACADDR            = "mac";
    public static final String AIT_KEY                = "key";
    public static final String AIT_AUTHCODE           = "code";
    
    public static final String PGM_PATH           = "path";
    
    public static final String MULT_SYNC_FLAG     = "pgmsyncflag";
    public static final String MULT_IP            = "groupip";
    public static final String MULT_PORT          = "groupport";
    public static final String MULT_LOCAL_PORT    = "localport";
    public static final String MULT_FOLLOWDELT    = "followdelt";

	public static final Uri CONTENTURI_SYSPARAM = Uri.parse("content://"
			+ AUTHORITY + "/sysparam");
	public static final Uri CONTENTURI_NETCONN = Uri.parse("content://"
            + AUTHORITY + "/netconnect");
	public static final Uri CONTENTURI_SERVINFO = Uri.parse("content://"
            + AUTHORITY + "/servinfo");
	public static final Uri CONTENTURI_SIGOUT = Uri.parse("content://"
            + AUTHORITY + "/sigout");
	public static final Uri CONTENTURI_WIFICFG = Uri.parse("content://"
            + AUTHORITY + "/wificfg");
	public static final Uri CONTENTURI_ONOFFTIME = Uri.parse("content://"
            + AUTHORITY + "/onofftime");
	public static final Uri CONTENTURI_OFFDLTIME = Uri.parse("content://"
            + AUTHORITY + "/offdltime");
	public static final Uri CONTENTURI_AUTHINFO = Uri.parse("content://"
			+ AUTHORITY + "/authinfo");
	public static final Uri CONTENTURI_PGMPATH = Uri.parse("content://"
	        + AUTHORITY + "/pgmpath");
	public static final Uri CONTENTURI_MULTICAST = Uri.parse("content://"
	        + AUTHORITY + "/multicast");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
			+ AUTHORITY + ".type";
	public static final String CONTENT_TYPE_ITME = "vnd.android.cursor.item/"
			+ AUTHORITY + ".item";
}
