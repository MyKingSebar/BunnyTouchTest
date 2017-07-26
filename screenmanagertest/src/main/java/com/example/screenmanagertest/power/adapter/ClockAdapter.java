package com.example.screenmanagertest.power.adapter;

import android.content.Context;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.power.entity.SysOnOffTimeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yanwei on 2017/4/18.
 */

public class ClockAdapter extends BaseAdapter {

    private Context mContext = null;
    private List<ClockItem> mItemList = null;

    private final int DEFAULT_ONOFF_MINUTE = 1;
    private final int ONOFF_MINIMUM_INTERVAL = DEFAULT_ONOFF_MINUTE*60;

    private final int ONEDAYSECONDS = 24*3600;

    public ClockAdapter(Context context){
        mContext = context;
        mItemList = new ArrayList<ClockItem>();
    }

    public ClockAdapter(Context context, List<ClockItem> items)
    {
        mContext = context;
        mItemList = new ArrayList<ClockItem>(items);

    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        if(mItemList.size() <= 0){
            return null;
        }

        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(ClockItem item){
        mItemList.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        mItemList.remove(position);
        notifyDataSetChanged();
    }

    public void removeAllItem(){
        mItemList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView  = View.inflate(mContext, R.layout.item_clock,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }else {
            holder =(ViewHolder) convertView.getTag();
        }
        holder.setTag(position);
        setView(position,holder);
        return convertView;
    }

    private void setView(final int position, ViewHolder holder){
        int week = mItemList.get(position).getmWeek();
        String on_time = mItemList.get(position).getmOntime();
        String off_time = mItemList.get(position).getmOffTime();
        holder.tv_onOffTime.setText(on_time + "--" + off_time);
        if("00:00:00".equals(on_time) && "00:00:00".equals(off_time)){
            holder.tv_onOffTime.setTextColor(mContext.getResources().getColor(R.color.gray));
        }else {
            holder.tv_onOffTime.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        holder.cbx_Sunday.setChecked((week & 0x01) == 0x01);
        holder.cbx_Monday.setChecked((week & 0x02) == 0x02);
        holder.cbx_Tuesday.setChecked((week & 0x04) == 0x04);
        holder.cbx_Wednesday.setChecked((week & 0x08) == 0x08);
        holder.cbx_Thursday.setChecked((week & 0x10) == 0x10);
        holder.cbx_Friday.setChecked((week & 0x20) == 0x20);
        holder.cbx_Saturday.setChecked((week & 0x40) == 0x40);

        holder.cbx_Sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x01)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x01))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x01);
                }else {
                    mItemList.get(position).setmWeek(week &0xFE);
                }

            }
        });

        holder.cbx_Monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x02)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x02))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x02);
                }else {
                    mItemList.get(position).setmWeek(week &0xFD);
                }

            }
        });

        holder.cbx_Tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x04)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x04))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x04);
                }else {
                    mItemList.get(position).setmWeek(week &0xFB);
                }

            }
        });

        holder.cbx_Wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x08)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x08))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x08);
                }else {
                    mItemList.get(position).setmWeek(week &0xF7);
                }

            }
        });

        holder.cbx_Thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x10)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x10))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x10);
                }else {
                    mItemList.get(position).setmWeek(week &0xEF);
                }

            }
        });

        holder.cbx_Friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x20)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x20))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x20);
                }else {
                    mItemList.get(position).setmWeek(week &0xDF);
                }

            }
        });

        holder.cbx_Saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int week  = mItemList.get(position).getmWeek();
                if (cb.isChecked()){
                    if(isTimeConflict(position, 0x40)){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),mContext.getResources().getString(R.string.clock_dialog_warn_timeconfilct),Toast.LENGTH_SHORT).show();
                        return;
                    }else if (!(checkOnOffTimeValid(position, 0x40))){
                        cb.setChecked(false);
                        Toast.makeText(v.getContext(),String.format(v.getResources().getString(R.string.clock_dialog_warn_timeinvalid),DEFAULT_ONOFF_MINUTE),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mItemList.get(position).setmWeek(week | 0x40);
                }else {
                    mItemList.get(position).setmWeek(week &0xBF);
                }

            }
        });
    }


    public boolean isTimeConflict(int position, int weekmask){
        int group = mItemList.size();
        ClockItem poscm = (ClockItem) mItemList.get(position);
        int week = poscm.getmWeek() | weekmask;
        String ontime = poscm.getmOntime();
        String offtime = poscm.getmOffTime();

        for (int i = 0; i < group; i++){
            if(i == position){
                continue;
            }
            ClockItem cm = (ClockItem) mItemList.get(i);

            for (int j = 0; j < 7; j++){
                if(((week&(1<<j)) != 0) &&((cm.getmWeek()&(1 <<j ))!=0)){
                    if(compareTwoTime(ontime, cm.getmOntime()) >= 0 && compareTwoTime(ontime, cm.getmOffTime() )<=0
                            ||compareTwoTime(offtime, cm.getmOntime())>=0 && compareTwoTime(offtime, cm.getmOffTime())<=0
                            ||compareTwoTime(ontime, cm.getmOntime()) <0 && compareTwoTime(offtime,cm.getmOntime())>0)
                    {
                        return true;
                    }
                }

            }
        }

        return false;

    }

    private boolean checkOnOffTimeValid(int position, int weekmask) {
        ClockItem poscm = (ClockItem) mItemList.get(position);
        int week = poscm.getmWeek() | weekmask;
        String[] strOnTime = poscm.getmOntime().split(":");
        String[] strOffTime = poscm.getmOffTime().split(":");

        SysOnOffTimeInfo currInfo = new SysOnOffTimeInfo();
        currInfo.week = week;
        currInfo.onhour = Integer.parseInt(strOnTime[0]);
        currInfo.onminute = Integer.parseInt(strOnTime[1]);
        currInfo.onsecond = Integer.parseInt(strOnTime[2]);
        currInfo.offhour = Integer.parseInt(strOffTime[0]);
        currInfo.offminute = Integer.parseInt(strOffTime[1]);
        currInfo.offsecond = Integer.parseInt(strOffTime[2]);

        int currOnSeconds = currInfo.onhour*3600+currInfo.onminute*60+currInfo.onsecond;
        int currOffSeconds = currInfo.offhour*3600+currInfo.offminute*60+currInfo.offsecond;
        if ((currOffSeconds-currOnSeconds) <= ONOFF_MINIMUM_INTERVAL) {
            return false;
        }

        if ((24*3600-(currOffSeconds-currOnSeconds)) <= ONOFF_MINIMUM_INTERVAL) {
            for (int i = 0; i < 7; i++) {
                if (i != 6) {
                    if (((currInfo.week&(1<<i)) != 0) && ((currInfo.week&(1<<(i+1))) != 0)) {
                        return false;
                    }
                } else {
                    if (((currInfo.week&(1<<6)) != 0) && ((currInfo.week&1) != 0)) {
                        return false;
                    }
                }
            }
        }

        int group = mItemList.size();
        if (group > 0) {
            SysOnOffTimeInfo[] sysInfo = new SysOnOffTimeInfo[group];
            for (int i = 0; i < group; i++) {
                ClockItem cm = (ClockItem) mItemList.get(i);
                sysInfo[i] = new SysOnOffTimeInfo();
                sysInfo[i].week = cm.getmWeek();
                String[] onTimeStr = cm.getmOntime().split(":");
                sysInfo[i].onhour = Integer.parseInt(onTimeStr[0]);
                sysInfo[i].onminute = Integer.parseInt(onTimeStr[1]);
                sysInfo[i].onsecond = Integer.parseInt(onTimeStr[2]);
                String[] offTimeStr = cm.getmOffTime().split(":");
                sysInfo[i].offhour = Integer.parseInt(offTimeStr[0]);
                sysInfo[i].offminute = Integer.parseInt(offTimeStr[1]);
                sysInfo[i].offsecond = Integer.parseInt(offTimeStr[2]);
            }

            for (int i = 0; i < 7; i++) {
                if ((currInfo.week & (1 << i)) != 0) {
                    for (int j = 0; j < group; j++) {
                        if (j != position) {
                            int dayIdx;
                            int tmpCurrOnSeconds, tmpCurrOffSeconds;
                            int onSeconds, offSeconds;

                            // Check on time
                            dayIdx = i;
                            for (int k = 0; k < 2; k++) {
                                if ((sysInfo[j].week & (1 << dayIdx)) != 0) {
                                    if (k != 0) {
                                        tmpCurrOnSeconds = ONEDAYSECONDS*k+currOnSeconds;
                                    } else {
                                        tmpCurrOnSeconds = currOnSeconds;
                                    }
                                    offSeconds = sysInfo[j].offhour*3600
                                            +sysInfo[j].offminute*60
                                            +sysInfo[j].offsecond;
                                    if (tmpCurrOnSeconds > offSeconds) {
                                        if ((tmpCurrOnSeconds-offSeconds) <= ONOFF_MINIMUM_INTERVAL) {
                                            return false;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                dayIdx = (dayIdx != 6) ? dayIdx + 1 : 0;
                            }

                            // Check off time
                            dayIdx = i;
                            for (int k = 0; k < 2; k++) {
                                if ((sysInfo[j].week & (1 << dayIdx)) != 0) {
                                    tmpCurrOffSeconds = currOffSeconds;
                                    if (k != 0) {
                                        onSeconds = ONEDAYSECONDS*k
                                                +sysInfo[j].onhour*3600
                                                +sysInfo[j].onminute*60
                                                +sysInfo[j].onsecond;
                                    } else {
                                        onSeconds = sysInfo[j].onhour*3600
                                                +sysInfo[j].onminute*60
                                                +sysInfo[j].onsecond;
                                    }

                                    if (onSeconds > tmpCurrOffSeconds) {
                                        if ((onSeconds-tmpCurrOffSeconds) <= ONOFF_MINIMUM_INTERVAL) {
                                            return false;
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                dayIdx = (dayIdx != 0) ? dayIdx - 1 : 6;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }


    public int compareTwoTime(String t1, String t2){
        String strTime1[] = t1.split(":");
        String strTime2[] = t1.split(":");
        if (strTime1.length < 3 || strTime2.length <3){
            Logger.i("The given time format is invalid");
            return -1;
        }

        Time currentTime = new Time();
        currentTime.setToNow();

        Time time1 = new Time();
        time1.set(Integer.parseInt(strTime1[2]), Integer.parseInt(strTime1[1]), Integer.parseInt(strTime1[0]), currentTime.monthDay, currentTime.month, currentTime.year);

        Time time2 = new Time();
        time2.set(Integer.parseInt(strTime2[2]), Integer.parseInt(strTime2[1]), Integer.parseInt(strTime2[0]), currentTime.monthDay, currentTime.month, currentTime.year);

        return Time.compare(time1, time2);
    }

    private class ViewHolder{
        public TextView tv_onOffTime;
        public CheckBox cbx_Sunday;
        public CheckBox cbx_Monday;
        public CheckBox cbx_Tuesday;
        public CheckBox cbx_Wednesday;
        public CheckBox cbx_Thursday;
        public CheckBox cbx_Friday;
        public CheckBox cbx_Saturday;

        private Object tag;

        public Object getTag(){
            return tag;
        }

        public void setTag(Object tag){
            this.tag = tag;
        }

        ViewHolder(View view){
            tv_onOffTime = (TextView) view.findViewById(R.id.clock_onOffTime);
            cbx_Sunday = (CheckBox) view.findViewById(R.id.check_sunday);
            cbx_Monday = (CheckBox) view.findViewById(R.id.check_monday);
            cbx_Tuesday = (CheckBox) view.findViewById(R.id.check_tuesday);
            cbx_Wednesday = (CheckBox) view.findViewById(R.id.check_wedsday);
            cbx_Thursday = (CheckBox) view.findViewById(R.id.check_thirsday);
            cbx_Friday = (CheckBox) view.findViewById(R.id.check_friday);
            cbx_Saturday = (CheckBox) view.findViewById(R.id.check_staday);
        }

    }
}
