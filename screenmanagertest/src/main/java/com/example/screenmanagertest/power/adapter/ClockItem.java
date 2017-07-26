package com.example.screenmanagertest.power.adapter;


/**
 * Created by ld019890217 on 2017/4/18.
 */

public class ClockItem  {

    private String mOntime = null;
    private String mOffTime = null;
    private int mWeek = 0;

    public ClockItem(){

    }
    public ClockItem(String on_time, String off_time, int week){
        mOntime = on_time;
        mOffTime = off_time;
        mWeek = week;
    }

    public String getmOntime() {
        return mOntime;
    }

    public void setmOntime(String mOntime) {
        this.mOntime = mOntime;
    }

    public String getmOffTime() {
        return mOffTime;
    }

    public void setmOffTime(String mOffTime) {
        this.mOffTime = mOffTime;
    }

    public int getmWeek() {
        return mWeek;
    }

    public void setmWeek(int mWeek) {
        this.mWeek = mWeek;
    }

}
