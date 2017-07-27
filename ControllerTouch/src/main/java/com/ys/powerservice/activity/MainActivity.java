package com.ys.powerservice.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ys.powerservice.R;

/**
 * Created by ld019890217 on 2017/4/25.
 */

public class MainActivity extends Activity {

    private Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        initService();
        CloseActivity();
        super.onResume();
    }

    private void initService(){
        Intent service = new Intent();
        service.setAction("com.ys.powerservice.sysctrlservice");
        startService(service);
    }

    private void CloseActivity(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1000*5);
    }


}
