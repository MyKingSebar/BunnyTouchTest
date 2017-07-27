package com.example.screenmanagertest.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.screenmanagertest.PosterApplication;
import com.example.screenmanagertest.R;
import com.example.screenmanagertest.common.DialogUtil;
import com.example.screenmanagertest.common.Logger;
import com.example.screenmanagertest.power.PowerOnOffManager;
import com.example.screenmanagertest.power.SysParamManager;
import com.example.screenmanagertest.power.adapter.ClockAdapter;
import com.example.screenmanagertest.power.adapter.ClockItem;
import com.example.screenmanagertest.power.entity.SysOnOffTimeInfo;
import com.example.screenmanagertest.view.PosterBaseView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by admin on 2017-07-19.
 */

public class PowerOnOffFragment extends SupportFragment {
    private  Context context=null;

    //=============================Clock===========================================
    private View mEditClockView = null;
    private EditText mOnTimeEditText = null;
    private EditText mOffTimeEditText = null;
    private CheckBox[] mWeekCheckBoxs = new CheckBox[7];
    private ClockAdapter onOffTimeAdapter = null;
    private Button mAddTimeBtn = null;
    private int mSelectedItemIdx = -1;
    private static final int CONTEXTMENU_REFRESH = 0;
    private static final int CONTEXTMENU_ADDITEM = 1;
    private static final int CONTEXTMENU_EDITITEM = 2;
    private static final int CONTEXTMENU_DELETEITEM = 3;
    private static final int CONTEXTMENU_CLEANITEMS = 4;
    // ==============================Dialog=======================================
    private Dialog mOnOffAlertDialog = null;
    private boolean mIsKeptAlertDialog = false;

    //===============================Constans=============================================
    private final int DEFAULT_ONOFF_MINUTE = 5;
    private final int ONOFF_MINIMUM_INTERVAL = DEFAULT_ONOFF_MINUTE * 60;
    private final int ONEDAYSECONDS = 24 * 3600;

    private List<ClockItem> mOldClockItemList = new ArrayList<ClockItem>();

    private final Handler mHandler = new Handler();


    public static PowerOnOffFragment newInstance(Context context) {
//        this.context=context;
        PowerOnOffFragment fragment = new PowerOnOffFragment();
        Bundle bundle = new Bundle();
        ArrayList list = new ArrayList(); //这个list用于在budnle中传递 需要传递的ArrayList<Object>
        list.add(context);
        bundle.putParcelableArrayList("bundleKey", list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList list = bundle.getParcelableArrayList("bundleKey");
            if (list.size() > 0) {
                this.context = (Context) list.get(0);
            } else {
                Log.i("jialei", "loadList.size=0");
            }
        }
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        saveOsdParam();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveOsdParam();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.poweronpff_main, container, false);
        if(context!=null){

            initView(view);
        }
        return view;
    }

    private void initView(View view) {


        mAddTimeBtn = (Button) view.findViewById(R.id.osd_clock_addbtn);
        ListView clock_listview = (ListView) view.findViewById(R.id.clock_listview);
        onOffTimeAdapter = new ClockAdapter(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        mEditClockView = layoutInflater.inflate(R.layout.view_edit_clock, null);
        mOnTimeEditText = (EditText) mEditClockView.findViewById(R.id.et_clock_onTime);
        mOffTimeEditText = (EditText) mEditClockView.findViewById(R.id.et_clock_offTime);
        mWeekCheckBoxs[1] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_mon_dgcbox);
        mWeekCheckBoxs[2] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_tue_dgcbox);
        mWeekCheckBoxs[3] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_wed_dgcbox);
        mWeekCheckBoxs[4] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_thu_dgcbox);
        mWeekCheckBoxs[5] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_fri_dgcbox);
        mWeekCheckBoxs[6] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_sat_dgcbox);
        mWeekCheckBoxs[0] = (CheckBox) mEditClockView.findViewById(R.id.osd_onoffTime_sun_dgcbox);

        ConcurrentHashMap<String, String> onOffTime = SysParamManager.getInstance().getOnOffTimeParam();
        if (onOffTime != null) {
            ClockItem item = null;
            int week = 0;
            String onTime = null;
            String offTime = null;
            int nGroup = Integer.parseInt(onOffTime.get("group"));
            for (int i = 1; i <= nGroup; i++) {
                item = new ClockItem();
                onTime = (onOffTime.get("on_time" + i) != null) ? onOffTime.get("on_time" + i) : "";
                offTime = (onOffTime.get("off_time" + i) != null) ? onOffTime.get("off_time" + i) : "";
                week = (onOffTime.get("week" + i) != null) ? Integer.parseInt(onOffTime.get("week" + i)) : 0;
                item.setmOntime(onTime);
                item.setmOffTime(offTime);
                item.setmWeek(week);
                onOffTimeAdapter.addItem(item);
                mOldClockItemList.add(new ClockItem(item.getmOntime(), item.getmOffTime(), item.getmWeek()));
            }
        }

        clock_listview.setAdapter(onOffTimeAdapter);
        clock_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                mSelectedItemIdx = position;
                return false;
            }
        });

        clock_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (onOffTimeAdapter.getCount() == 0) {
                            mHandler.postDelayed(rPopupDelay, 2000);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (onOffTimeAdapter.getCount() == 0) {
                            mHandler.removeCallbacks(rPopupDelay);
                        }
                        break;

                }
                return false;
            }
        });

        mAddTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOnOffTimeItem();
            }
        });

        registerForContextMenu(clock_listview);


    }
    private Runnable rPopupDelay = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(rPopupDelay);
            addOnOffTimeItem();
        }
    };

    private void addOnOffTimeItem() {
        if (mEditClockView == null) {
            return;
        }

        if (onOffTimeAdapter.getCount() > 10) {
            Toast.makeText(context, R.string.clock_dialog_warn_timenumtransfinite, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEditClockView.getParent() != null) {
            ((ViewGroup) mEditClockView.getParent()).removeView(mEditClockView);
        }

        mOnTimeEditText.setText("00:00:00");
        mOffTimeEditText.setText("00:00:00");

        for (int i = 0; i < 7; i++) {
            mWeekCheckBoxs[i].setChecked(false);
        }
        mOnOffAlertDialog = DialogUtil.showTipsDialog(context, getString(R.string.clock_dialog_add), mEditClockView, getString(R.string.enter), getString(R.string.cancel), new DialogUtil.DialogDoubleButtonListener() {

            @Override
            public void onLeftClick(Context context, View v, int which) {
                int week = 0;
                for (int i = 0; i < 7; i++) {
                    if (mWeekCheckBoxs[i].isChecked()) {
                        week |= (1 << i);
                    }
                }
                if (!isValidTime(mOnTimeEditText.getText().toString()) || !isValidTime(mOffTimeEditText.getText().toString())) {
                    Toast.makeText(context, R.string.clock_dialog_warn_invalidtime, Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else if (PosterApplication.compareTwoTime(mOnTimeEditText.getText().toString(), mOffTimeEditText.getText().toString()) >= 0) {
                    Toast.makeText(context, R.string.clock_dialog_warn_format, Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else if (isTimeConfict(mOnTimeEditText.getText().toString(), mOffTimeEditText.getText().toString(), week, -1)) {
                    Toast.makeText(context, R.string.clock_dialog_warn_timeconfilct, Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else if (!checkOnOffTimeValid(mOnTimeEditText.getText().toString(), mOffTimeEditText.getText().toString(), week, -1)) {
                    Toast.makeText(context, String.format(getResources().getString(R.string.clock_dialog_warn_timeinvalid), DEFAULT_ONOFF_MINUTE), Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else {
                    ClockItem item = new ClockItem();
                    item.setmOntime(mOnTimeEditText.getText().toString());
                    item.setmOffTime(mOffTimeEditText.getText().toString());
                    item.setmWeek(week);
                    onOffTimeAdapter.addItem(item);
                    saveOsdParam();
                }

                if (mOnOffAlertDialog != null) {
                    DialogUtil.hideInputMethod(context, mEditClockView, mOnOffAlertDialog);
                    mOnOffAlertDialog.dismiss();
                    mOnOffAlertDialog = null;
                }

            }

            public void onRightClick(Context context, View v, int which) {

                if (mOnOffAlertDialog != null) {
                    DialogUtil.hideInputMethod(context, mEditClockView, mOnOffAlertDialog);
                    mOnOffAlertDialog.dismiss();
                    mOnOffAlertDialog = null;
                }

            }

        }, false);

        mOnOffAlertDialog.show();

        DialogUtil.dialogTimeOff(mOnOffAlertDialog, mEditClockView, 90000);
    }
    private boolean isValidTime(final String timeStr) {
        Pattern pattern = Pattern.compile("^([0-2]?[0-9]{1}):([0-5]?[0-9]{1}):([0-5]?[0-9]{1})$");
        Matcher matcher = pattern.matcher(timeStr);
        if (matcher.find()) {
            if ((Integer.parseInt(matcher.group(1)) > 23) || (Integer.parseInt(matcher.group(2)) > 59) || (Integer.parseInt(matcher.group(3)) > 59)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void keepDialogShowing(Dialog dialog) {
        try {
            Field field = dialog.getClass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIsKeptAlertDialog = true;
    }

    public boolean isTimeConfict(String ontime, String offtime, int week, int modifiedindex) {
        int group = onOffTimeAdapter.getCount();

        for (int i = 0; i < group; i++) {
            if (i == modifiedindex) {
                continue;
            }
            ClockItem cm = (ClockItem) onOffTimeAdapter.getItem(i);

            for (int j = 0; j < 7; j++) {
                if (((week & (1 << j)) != 0) && ((cm.getmWeek() & (1 << j)) != 0)) {
                    if ((PosterApplication.compareTwoTime(ontime, cm.getmOntime()) >= 0 && PosterApplication.compareTwoTime(ontime, cm.getmOffTime()) <= 0) || (PosterApplication.compareTwoTime(offtime, cm.getmOntime()) >= 0 && PosterApplication.compareTwoTime(offtime, cm.getmOffTime()) <= 0) || (PosterApplication.compareTwoTime(ontime, cm.getmOntime()) < 0 && PosterApplication.compareTwoTime(offtime, cm.getmOntime()) > 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkOnOffTimeValid(String ontime, String offtime, int week, int modifiedindex) {
        String[] strOnTime = ontime.split(":");
        String[] strOffTime = offtime.split(":");
        if (strOnTime.length < 3 || strOffTime.length < 3) {
            Logger.i("The given time format is invaild.");
            return false;
        }

        SysOnOffTimeInfo currInfo = new SysOnOffTimeInfo();
        currInfo.week = week;
        currInfo.onhour = Integer.parseInt(strOnTime[0]);
        currInfo.onminute = Integer.parseInt(strOnTime[1]);
        currInfo.onsecond = Integer.parseInt(strOnTime[2]);
        currInfo.offhour = Integer.parseInt(strOffTime[0]);
        currInfo.offminute = Integer.parseInt(strOffTime[1]);
        currInfo.offsecond = Integer.parseInt(strOffTime[2]);

        int currOnSeconds = currInfo.onhour * 3600 + currInfo.onminute * 60 + currInfo.onsecond;
        int currOffSeconds = currInfo.offhour * 3600 + currInfo.offminute * 60 + currInfo.offsecond;
        if ((currOffSeconds - currOnSeconds) <= ONOFF_MINIMUM_INTERVAL) {
            return false;
        }

        if ((24 * 3600 - (currOffSeconds - currOnSeconds)) <= ONOFF_MINIMUM_INTERVAL) {
            for (int i = 0; i < 7; i++) {
                if (i != 6) {
                    if (((currInfo.week & (1 << i)) != 0) && ((currInfo.week & (1 << (i + 1))) != 0)) {
                        return false;
                    }
                } else {
                    if (((currInfo.week & (1 << 6)) != 0) && ((currInfo.week & 1) != 0)) {
                        return false;
                    }
                }
            }
        }

        int group = onOffTimeAdapter.getCount();
        if (group > 0) {
            SysOnOffTimeInfo[] sysInfo = new SysOnOffTimeInfo[group];
            for (int i = 0; i < group; i++) {
                ClockItem cm = (ClockItem) onOffTimeAdapter.getItem(i);
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
                        if (j != modifiedindex) {
                            int dayIdx;
                            int tmpCurrOnSeconds, tmpCurrOffSeconds;
                            int onSeconds, offSeconds;

                            // Check on time
                            dayIdx = i;
                            for (int k = 0; k < 2; k++) {
                                if ((sysInfo[j].week & (1 << dayIdx)) != 0) {
                                    if (k != 0) {
                                        tmpCurrOnSeconds = ONEDAYSECONDS * k + currOnSeconds;
                                    } else {
                                        tmpCurrOnSeconds = currOnSeconds;
                                    }
                                    offSeconds = sysInfo[j].offhour * 3600 + sysInfo[j].offminute * 60 + sysInfo[j].offsecond;
                                    if (tmpCurrOnSeconds > offSeconds) {
                                        if ((tmpCurrOnSeconds - offSeconds) <= ONOFF_MINIMUM_INTERVAL) {
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
                                        onSeconds = ONEDAYSECONDS * k + sysInfo[j].onhour * 3600 + sysInfo[j].onminute * 60 + sysInfo[j].onsecond;
                                    } else {
                                        onSeconds = sysInfo[j].onhour * 3600 + sysInfo[j].onminute * 60 + sysInfo[j].onsecond;
                                    }

                                    if (onSeconds > tmpCurrOffSeconds) {
                                        if ((onSeconds - tmpCurrOffSeconds) <= ONOFF_MINIMUM_INTERVAL) {
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


    private void saveOsdParam() {
        Log.i("jialei","saveOsdParam");
        saveClockParam();
        PowerOnOffManager.getInstance().checkAndSetOnOffTime(PowerOnOffManager.AUTOSCREENOFF_COMMON);
//        context.sendBroadcast(new Intent().setAction("com.ys.powerservice.updatedatabase"));
    }


    private void saveClockParam() {
        Log.i("jialei","saveClockParam");
        if (onOffTimeAdapter != null) {
            ClockItem item = null;
            int nGroup = onOffTimeAdapter.getCount();
            ConcurrentHashMap<String, String> onOffTime = new ConcurrentHashMap<String, String>();
            onOffTime.put("group", String.valueOf(nGroup));
            for (int i = 0; i < nGroup; i++) {
                item = (ClockItem) onOffTimeAdapter.getItem(i);
                onOffTime.put("on_time" + (i + 1), item.getmOntime());
                onOffTime.put("off_time" + (i + 1), item.getmOffTime());
                onOffTime.put("week" + (i + 1), String.valueOf(item.getmWeek()));
            }

            Logger.i("onoffTimesize" + onOffTime.size());
            SysParamManager.getInstance().setOnOffTimeParam(onOffTime);
            onOffTimeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.clock_dialog_header);
        menu.add(0, CONTEXTMENU_REFRESH, 0, R.string.clock_dialog_refrech);
        menu.add(0, CONTEXTMENU_ADDITEM, 0, R.string.clock_dialog_add);
        menu.add(0, CONTEXTMENU_EDITITEM, 0, R.string.clock_dialog_modify);
        menu.add(0, CONTEXTMENU_DELETEITEM, 0, R.string.clock_dialog_del);
        menu.add(0, CONTEXTMENU_CLEANITEMS, 0, R.string.clock_dialog_delall);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (mSelectedItemIdx < 0) {
            return false;
        }

        boolean bRet = false;
        if (onOffTimeAdapter != null) {
            switch (item.getItemId()) {
                case CONTEXTMENU_REFRESH:
                    bRet = true;
                    onOffTimeAdapter.notifyDataSetChanged();
                    break;
                case CONTEXTMENU_ADDITEM:
                    bRet = true;
                    addOnOffTimeItem();
                    break;
                case CONTEXTMENU_EDITITEM:
                    bRet = true;
                    editOnOffTimeItem();
                    break;
                case CONTEXTMENU_DELETEITEM:
                    bRet = true;
                    removeItem(mSelectedItemIdx);
                    break;
                case CONTEXTMENU_CLEANITEMS:
                    bRet = true;
                    onOffTimeAdapter.removeAllItem();
                    saveOsdParam();
                    break;
            }
        }

        return bRet;
    }

    private void removeItem(int mSelectedItemIdx) {
        onOffTimeAdapter.removeItem(mSelectedItemIdx);
        saveOsdParam();
    }

    private void editOnOffTimeItem() {
        if (mEditClockView == null) {
            return;
        }

        if (mEditClockView.getParent() != null) {
            ((ViewGroup) mEditClockView.getParent()).removeView(mEditClockView);
        }

        ClockItem item = (ClockItem) onOffTimeAdapter.getItem(mSelectedItemIdx);
        mOnTimeEditText.setText(item.getmOntime());
        mOffTimeEditText.setText(item.getmOffTime());
        int week = item.getmWeek();
        for (int i = 0; i < 7; i++) {
            if ((week & (1 << i)) != 0) {
                mWeekCheckBoxs[i].setChecked(true);
            } else {
                mWeekCheckBoxs[i].setChecked(false);
            }
        }
        mOnOffAlertDialog = DialogUtil.showTipsDialog(context, getString(R.string.clock_dialog_modify), mEditClockView, getString(R.string.enter), getString(R.string.cancel), new DialogUtil.DialogDoubleButtonListener() {

            @Override
            public void onLeftClick(Context context, View v, int which) {
                int week = 0;
                for (int i = 0; i < 7; i++) {
                    if (mWeekCheckBoxs[i].isChecked()) {
                        week |= (1 << i);
                    }
                }
                if (!isValidTime(mOnTimeEditText.getText().toString()) || !isValidTime(mOffTimeEditText.getText().toString())) {
                    Toast.makeText(context, R.string.clock_dialog_warn_invalidtime, Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else if (PosterApplication.compareTwoTime(mOnTimeEditText.getText().toString(), mOffTimeEditText.getText().toString()) >= 0) {
                    Toast.makeText(context, R.string.clock_dialog_warn_format, Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else if (isTimeConfict(mOnTimeEditText.getText().toString(), mOffTimeEditText.getText().toString(), week, mSelectedItemIdx)) {
                    Toast.makeText(context, R.string.clock_dialog_warn_timeconfilct, Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else if (!checkOnOffTimeValid(mOnTimeEditText.getText().toString(), mOffTimeEditText.getText().toString(), week, mSelectedItemIdx)) {
                    Toast.makeText(context, String.format(getResources().getString(R.string.clock_dialog_warn_timeinvalid), DEFAULT_ONOFF_MINUTE), Toast.LENGTH_LONG).show();
                    keepDialogShowing(mOnOffAlertDialog);
                } else {
                    ClockItem editItem = (ClockItem) onOffTimeAdapter.getItem(mSelectedItemIdx);
                    editItem.setmOntime(mOnTimeEditText.getText().toString());
                    editItem.setmOffTime(mOffTimeEditText.getText().toString());
                    editItem.setmWeek(week);
                    onOffTimeAdapter.notifyDataSetChanged();
                    saveOsdParam();

                }

                if (mOnOffAlertDialog != null) {
                    DialogUtil.hideInputMethod(context, mEditClockView, mOnOffAlertDialog);
                    mOnOffAlertDialog.dismiss();
                    mOnOffAlertDialog = null;
                }

            }

            @Override
            public void onRightClick(Context context, View v, int which) {

                if (mOnOffAlertDialog != null) {
                    DialogUtil.hideInputMethod(context, mEditClockView, mOnOffAlertDialog);
                    mOnOffAlertDialog.dismiss();
                    mOnOffAlertDialog = null;
                }

            }
        }, false);

        mOnOffAlertDialog.show();
        saveOsdParam();
        DialogUtil.dialogTimeOff(mOnOffAlertDialog, mEditClockView, 90000);
    }




}
