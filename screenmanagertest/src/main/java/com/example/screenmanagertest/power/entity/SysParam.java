package com.example.screenmanagertest.power.entity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ld019890217 on 2017/4/19.
 */

public class SysParam {

    public int                      setBit = 0;               /** 1为设置状态，0为读取状态 **/
    public ConcurrentHashMap<String, String>  netConn = null;            /** 网络登录方式参数**/
    public ConcurrentHashMap<String, String>  serverSet = null;          /**服务器设置参数**/
    public ConcurrentHashMap<String, String>  sigOutSet = null;          /** 信号输出  **/
    public ConcurrentHashMap<String, String>  wifiSet = null;            /** WIFI配置参数 **/
    public int                 osdLangSetosd_lang = 0;        /**OSD语言设置，0 为中文，1为英文，其它值另定**/
    public int                 getTaskPeriodtime = 0;         /**自动获取任务时间，1-30天**/
    public int                 delFilePeriodtime = 0;         /**自动删除文件时间，1-30天**/
    public String              timeZonevalue = null;           /**时区设置，如GMT+8**/
    public int                 scrnRotatevalue = 0;           /**屏幕旋转，0 屏幕不旋转，1屏幕要旋转**/
    public String              passwdvalue = null;             /**用户密码设置参数**/
    public String              syspasswdvalue = null;          /**系统登录密码参数***/
    public ConcurrentHashMap<String, String>  onOffTime = null;          /**开关机时间设置参数**/
    public int                 devSelvalue = 1;               /**设备选择参数，0 USB, 1 磁盘**/
    public int                 brightnessvalue = 0;           /**亮度设置参数**/
    public int                 volumevalue = 0;               /**音量设置参数**/
    public String              swVervalue = null;              /**软件版本号**/
    public String              hwVervalue = null;              /**硬件版本号**/
    public String              kernelvervalue = null;          /**kernel软件版本号**/
    public String              cfevervalue = null;             /**Android软件版本号**/
    public String              certNumvalue = null;            /**入网许可证号**/
    public String              termmodelvalue = null;          /***终端型号****/
    public String              termname = null;                /**终端名称**/
    public String              termGrpvalue = null;            /**终端组名称**/
    public int                 dwnLdrSpdvalue = 0;            /**下载速度**/
    public int                 cycleTimevalue = 0;            /**心跳时间**/
    public int                 dispScalevalue = 0;            /**显示比例 1: 4:3; 2: 16:9, 3: LetterBox, 4:Pan&Scan **/
    public int                 autoupgradevalue = 0;          /**自动升级标志 default 0;0:disable ;1,enable**/
    public int                 runmodevalue = 0;              /**播放模式 default 0; 0:网络播放 ;1,单机播放**/
    public ConcurrentHashMap<String, String>  offdlTime = null;          /**禁止下载的时间段 **/
}
