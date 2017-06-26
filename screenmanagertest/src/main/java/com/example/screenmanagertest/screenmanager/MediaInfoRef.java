/*
 * Copyright (C) 2013 poster PCE YoungSee Inc. 
 * All Rights Reserved Proprietary and Confidential.
 * 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.screenmanager;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MediaInfoRef implements Serializable
{
    public String mediaType = null;   // �ز����ͣ�Video, Image, Text, Audio, WebPage)
    public String source = null;      // �ز���Դ��(File, Url, Rss) �����ļ��������ļ�
    public String filePath = null;    // �ز�λ�ã��ļ�·����Link Address)
    public String verifyCode = null;  // �ز�У����
    public String remotePath = null;  // �������ϵ�λ��
    public String mid = null;
    public String vType = null;
    public int md5Key = 0;
    public int duration = 0;
    public int times = 0;
    public int mode = 0;
    public int aspect = 0;
    public int speed = 0;
    public int vol = 0;
    public int durationPerPage = 0;
    public String format = null;
    public String fontName = null;
    public String fontColor = null;
    public String fontSize = null;
    public String playlistmode = null;
    public String timetype = null;
    public String starttime = null;
    public String endtime = null;
    public String deadline = null;
    public int containerwidth = 0;
    public int containerheight = 0;
    public int playedtimes = 0; // �Ѿ����Ŷ��ٴ�
    public boolean isIgnoreDlLimit = false;  // �Ƿ������������

    @Override
    public String toString() {
        return "MediaInfoRef{" +
                "mediaType='" + mediaType + '\'' +
                ", source='" + source + '\'' +
                ", filePath='" + filePath + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", remotePath='" + remotePath + '\'' +
                ", mid='" + mid + '\'' +
                ", vType='" + vType + '\'' +
                ", md5Key=" + md5Key +
                ", duration=" + duration +
                ", times=" + times +
                ", mode=" + mode +
                ", aspect=" + aspect +
                ", speed=" + speed +
                ", vol=" + vol +
                ", durationPerPage=" + durationPerPage +
                ", format='" + format + '\'' +
                ", fontName='" + fontName + '\'' +
                ", fontColor='" + fontColor + '\'' +
                ", fontSize='" + fontSize + '\'' +
                ", playlistmode='" + playlistmode + '\'' +
                ", timetype='" + timetype + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", deadline='" + deadline + '\'' +
                ", containerwidth=" + containerwidth +
                ", containerheight=" + containerheight +
                ", playedtimes=" + playedtimes +
                ", isIgnoreDlLimit=" + isIgnoreDlLimit +
                '}';
    }
}
