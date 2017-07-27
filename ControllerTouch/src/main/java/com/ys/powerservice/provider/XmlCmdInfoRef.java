/*
 * Copyright (C) 2013 poster PCE YoungSee Inc.
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.ys.powerservice.provider;

public class XmlCmdInfoRef
{
    /* Define the CMD serial id */
    public static final int    CMD_PTL_NULL                   = 0;
    public static final int    CMD_PTL_CERTI                  = 1;
    public static final int    CMD_PTL_CYC                    = 2;
    public static final int    CMD_PTL_SETNAME                = 3;
    public static final int    CMD_PTL_SETOUT                 = 4;
    public static final int    CMD_PTL_SETSCALE               = 5;
    public static final int    CMD_PTL_SETPW                  = 6;
    public static final int    CMD_PTL_SETONOFF               = 7;
    public static final int    CMD_PTL_SETCYCTIME             = 8;
    public static final int    CMD_PTL_SETVOL                 = 9;
    public static final int    CMD_PTL_SETBRI                 = 10;
    public static final int    CMD_PTL_SETBNET                = 11;
    public static final int    CMD_PTL_SETSERVER              = 12;
    public static final int    CMD_PTL_SETDLRATE              = 13;
    public static final int    CMD_PTL_SETTIME                = 14;
    public static final int    CMD_PTL_GETBINFO               = 15;
    public static final int    CMD_PTL_GETPWINFO              = 16;
    public static final int    CMD_PTL_GETNETINFO             = 17;
    public static final int    CMD_PTL_PLAYCTR                = 18;
    public static final int    CMD_PTL_PLAYFILE               = 19;
    public static final int    CMD_PTL_PLAYLIST               = 20;
    public static final int    CMD_PTL_PLAYTASK               = 21;
    public static final int    CMD_PTL_PLAYPLATE              = 22;
    public static final int    CMD_PTL_FILELIST               = 23;
    public static final int    CMD_PTL_FILEDEL                = 24;
    public static final int    CMD_PTL_FILEDELT               = 25;
    public static final int    CMD_PTL_SYSUPDATE              = 26;
    public static final int    CMD_PTL_SYSDATE                = 27;
    public static final int    CMD_PTL_SYSHD                  = 28;
    public static final int    CMD_PTL_WARN                   = 29;
    public static final int    CMD_PTL_PUTFILE                = 30;
    public static final int    CMD_PTL_DOWNPLAYLIST           = 31;
    public static final int    CMD_PTL_SETOSD                 = 32;
    public static final int    CMD_PTL_SYNCSYSDATA            = 33;
    public static final int    CMD_PTL_CPESYSLOGFTPUP         = 34;
    public static final int    CMD_PTL_CPEPLAYLOGFTPUP        = 35;
    public static final int    CMD_PTL_CPEPRSCRN              = 36;
    public static final int    CMD_PTL_SETSYSPASSWORD         = 37;
    public static final int    CMD_PTL_GETDTVCONFIG           = 38;
    public static final int    CMD_PTL_AUTOUPGRADE            = 39;
    public static final int    CMD_PTL_FORBIDDOWNLOADTIME     = 40;
    public static final int    CMD_PTL_MULTICASTMANAGER       = 41;
    
    public static final int    CMD_PTL_AUTHKEYUPDATE          = 50;
    
    public static final int    CMD_PTL_NOCMD                  = 99;
    
    /* Define CMD Tag */
    public static final String CMD_BEGIN_END_TAG              = ("PTL");
    public static final String CMD_KEYWORDS_CMD               = ("CMD");
    public static final String CMD_KEYWORDS_ID                = ("ID");
    public static final String CMD_KEYWORDS_CPENAME           = ("CPENAME");
    public static final String CMD_KEYWORDS_CPEGRP            = ("CPEGRP");
    public static final String CMD_KEYWORDS_VIDEOTYPE         = ("VIDEOTYPE");
    public static final String CMD_KEYWORDS_PIX               = ("PIX");
    public static final String CMD_KEYWORDS_REPRATIO          = ("REPRATIO");
    public static final String CMD_KEYWORDS_SCALE             = ("SCALE");
    public static final String CMD_KEYWORDS_POWER             = ("POWER");
    public static final String CMD_KEYWORDS_GRPNUM            = ("GRPNUM");
    public static final String CMD_KEYWORDS_ON                = ("ON");
    public static final String CMD_KEYWORDS_OFF               = ("OFF");
    public static final String CMD_KEYWORDS_WEEK              = ("WEEK");
    public static final String CMD_KEYWORDS_VOL               = ("VOL");
    public static final String CMD_KEYWORDS_BRI               = ("BRI");
    public static final String CMD_KEYWORDS_MODE              = ("MODE");
    public static final String CMD_KEYWORDS_IP                = ("IP");
    public static final String CMD_KEYWORDS_MASK              = ("MASK");
    public static final String CMD_KEYWORDS_GATEWAY           = ("GATEWAY");
    public static final String CMD_KEYWORDS_DNS1              = ("DNS1");
    public static final String CMD_KEYWORDS_DNS2              = ("DNS2");
    public static final String CMD_KEYWORDS_PPPOENAME         = ("PPPOENAME");
    public static final String CMD_KEYWORDS_PPPOEPW           = ("PPPOEPW");
    public static final String CMD_KEYWORDS_3GMODULE          = ("MODULE");
    public static final String CMD_KEYWORDS_3GSPACETIME       = ("TIME");
    public static final String CMD_KEYWORDS_AOTUUPGRADE       = ("UPGRADE");
    public static final String CMD_KEYWORDS_WEBURL            = ("WEBURL");
    public static final String CMD_KEYWORDS_NTPIP             = ("NTPIP");
    public static final String CMD_KEYWORDS_NTPPORT           = ("NTPPORT");
    public static final String CMD_KEYWORDS_FTPIP             = ("FTPIP");
    public static final String CMD_KEYWORDS_FTPPORT           = ("FTPPORT");
    public static final String CMD_KEYWORDS_FTPNAME           = ("FTPNAME");
    public static final String CMD_KEYWORDS_FTPPW             = ("FTPPW");
    public static final String CMD_KEYWORDS_DLRATE            = ("DLRATE");
    public static final String CMD_KEYWORDS_TIME              = ("TIME");
    public static final String CMD_KEYWORDS_TIMEZONE          = ("TIMEZONE");
    public static final String CMD_KEYWORDS_WIN               = ("WIN");
    public static final String CMD_KEYWORDS_PLAYCTR           = ("PLAYCTR");
    public static final String CMD_KEYWORDS_FILE              = ("FILE");
    public static final String CMD_KEYWORDS_VERCODE           = ("VERCODE");
    public static final String CMD_KEYWORDS_FILESIZE          = ("SIZE");
    public static final String CMD_KEYWORDS_FILETYPE          = ("TYPE");
    public static final String CMD_KEYWORDS_FILESPECIALMD5    = ("SPECIALMD5");
    public static final String CMD_KEYWORDS_PLAYNUM           = ("PLAYNUM");
    public static final String CMD_KEYWORDS_PLAYMODE          = ("PLAYMODE");
    public static final String CMD_KEYWORDS_FONTDEFAULT       = ("FONTDEFAULT");
    public static final String CMD_KEYWORDS_FONTNMAE          = ("FONTNMAE");
    public static final String CMD_KEYWORDS_FONTPRO           = ("FONTPRO");
    public static final String CMD_KEYWORDS_FONTSIZE          = ("FONTSIZE");
    public static final String CMD_KEYWORDS_FONTCOLOR         = ("FONTCOLOR");
    public static final String CMD_KEYWORDS_BACKCOLOR         = ("BACKCOLOR");
    public static final String CMD_KEYWORDS_PLAYPAGE          = ("PLAYPAGE");
    public static final String CMD_KEYWORDS_PLAYSPEED         = ("PLAYSPEED");
    public static final String CMD_KEYWORDS_FILETIME          = ("FILETIME");
    public static final String CMD_KEYWORDS_UPDATE            = ("UPDATE");
    public static final String CMD_KEYWORDS_PROTYPE           = ("PROTYPE");
    public static final String CMD_KEYWORDS_GROUPNAME          = ("GROUPNAME");
    public static final String CMD_KEYWORDS_HDVER             = ("HDVER");
    public static final String CMD_KEYWORDS_SFVER             = ("SFVER");
    public static final String CMD_KEYWORDS_WARNLEVEL         = ("WARNLEVEL");
    public static final String CMD_KEYWORDS_MESSAGE           = ("MESSAGE");
    public static final String CMD_KEYWORDS_MAC               = ("MAC");
    public static final String CMD_KEYWORDS_DISK              = ("DISK");
    public static final String CMD_KEYWORDS_DLFILE            = ("DLFILE");
    public static final String CMD_KEYWORDS_UPFILE            = ("UPFILE");
    public static final String CMD_KEYWORDS_TASK              = ("TASK");
    public static final String CMD_KEYWORDS_PLATE             = ("PLATE");
    public static final String CMD_KEYWORDS_OSDLANG           = ("LANG");
    public static final String CMD_KEYWORDS_OSDPASS           = ("PASSWD");
    public static final String CMD_KEYWORDS_CPUINFO           = ("CPU");
    public static final String CMD_KEYWORDS_MEMINFO           = ("MEM");
    public static final String CMD_KEYWORDS_PLAYLIST          = ("LIST");
    public static final String CMD_KEYWORDS_PLAYLISTE         = ("LISTE");
    public static final String CMD_KEYWORDS_SOCKETSPEED_DOWM  = ("SOCKETS_DOWN");
    public static final String CMD_KEYWORDS_SOCKETSPEED_UP    = ("SOCKETS_UP");
    public static final String CMD_KEYWORDS_CPEPRSCRN_WIDTH   = ("WIDTH");
    public static final String CMD_KEYWORDS_CPEPRSCRN_HEIGHT  = ("HEIGHT");
    public static final String CMD_KEYWORDS_CPEPRSCRN_FTPPATH = ("FILE");
    public static final String CMD_KEYWORDS_CPEPRSCRN_VERCODE = ("VERCODE");
    public static final String CMD_KEYWORDS_MULTICAST_PROGSYNC   = ("PROGSYNC"); //BEGIN multicast 
    public static final String CMD_KEYWORDS_MULTICAST_BCASTIP    = ("BCASTIP");
    public static final String CMD_KEYWORDS_MULTICAST_BCASTPORT  = ("BCASTPORT");
    public static final String CMD_KEYWORDS_MULTICAST_BLOCALPORT = ("BLOCALPORT");
    public static final String CMD_KEYWORDS_MULTICAST_FOLLOWDELT = ("FOLLOWDELT");//END multicast
    
    /* Defined exception strings */
    public static final String IS_STR_OK                      = ("OK");
    public static final String IS_STR_INVALIDTERMINAL         = ("InvalidTerminal");
    public static final String IS_STR_UNKNOWNTERMINAL         = ("UnknownTerminal");
    public static final String IS_STR_AUTHFAIL                = ("AuthFail");
    public static final String IS_STR_UNKOWNCOMMAND           = ("UnkownCommand");
    public static final String IS_STR_BADCOMMAND              = ("BadCommand");
    public static final String IS_STR_SERVEREXCEPTION         = ("ServerException");
    
    /* Defined sysFileData Tag */
    public static final String SYSSETDATAFILE                 = "sysSetDataFile";
    public static final String SETBIT                         = "setBit";
    public static final String NETCONN                        = "netConn";
    public static final String SRVERSET                       = "serverSet";
    public static final String SIGOUTSET                      = "sigOutSet";
    public static final String OSDLANGSET                     = "osdLangSet";
    public static final String GETTASKPERIOD                  = "getTaskPeriod";
    public static final String DELFILEPERIOD                  = "delFilePeriod";
    public static final String TIMEZONE                       = "timeZone";
    public static final String SCRNROTATE                     = "scrnRotate";
    public static final String PASSWD                         = "passwd";
    public static final String SYSPASSWD                      = "syspasswd";
    public static final String ONOFFTIME                      = "onOffTime";
    public static final String DEVSEL                         = "devSel";
    public static final String BRIGHTNESS                     = "brightness";
    public static final String VOLUME                         = "volume";
    public static final String SWVER                          = "swVer";
    public static final String KERNELVER                      = "kernelver";
    public static final String CFEVER                         = "cfever";
    public static final String HWVER                          = "hwVer";
    public static final String CERTNUM                        = "certNum";
    public static final String TERMMDL                        = "termmodel";
    public static final String TERM                           = "term";
    public static final String TERMGRP                        = "termGrp";
    public static final String DWNLDRSPD                      = "dwnLdrSpd";
    public static final String CYCLETIME                      = "cycleTime";
    public static final String DISPSCALE                      = "dispScale";
    public static final String AUTOGRADE                      = "autoupgrade";
    public static final String RUNMODER                       = "runmode";
    public static final String OFFDLTIME                      = "offdlTime";
    public static final String WIFISET                        = "wifiSet";
    
    public static final String LANG                           = "LANG";
    public static final String ID                             = "ID";
    public static final String PWD                            = "PASSWD";
    
    /* sysFileData Attribute Macro */
    public static final String TIME                           = "time";
    public static final String VALUE                          = "value";
    public static final String NAME                           = "name";
    public static final String OSDLANG                        = "osd_lang";
    
    /* Define the CMD content value */
    public static final int    CMD_STAND_BY                   = 0;
    public static final int    CMD_POWER_ON                   = 1;
    public static final int    CMD_REBOOT                     = 2;
}
