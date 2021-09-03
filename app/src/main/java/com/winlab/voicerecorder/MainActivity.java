package com.winlab.voicerecorder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.winlab.voicerecorder.utils.CallBackUtil;
import com.winlab.voicerecorder.utils.OkhttpUtil;

import java.util.HashMap;

import okhttp3.Call;

public class MainActivity extends WearableActivity {
    private static final String LOG_TAG = "MainActivity";
    private Chronometer mChronometer;
    private Switch switchRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        switchRecorder = (Switch) findViewById(R.id.switch_recorder);

        // Enables Always-on
        setAmbientEnabled();
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                    10);
        }
        switchRecorder.setOnCheckedChangeListener(swicthRecorderListener);

    }


    private CompoundButton.OnCheckedChangeListener swicthRecorderListener= new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Intent intent = new Intent(getBaseContext(), RecordingService.class);
            if(isChecked){
                //start Chronometer
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();
                //start RecordingService
                Log.d(LOG_TAG, "start recording service");
                startService(intent);
                //keep screen on while recording
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                updateRecord(true);
            }else{

                //1. stop timer
                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime());

                Log.d(LOG_TAG, "stop recording service");
                // stop recording
                stopService(intent);
                //allow the screen to turn off again once recording is finished
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                updateRecord(false);
            }

        }
    };

    public void updateRecord(boolean record){

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("recording", record? "1": "0");
        parameters.put("machine", "wearable");
        OkhttpUtil.okHttpPost("http://"+Static.HOST+":5000/syncrecording", parameters, new CallBackUtil.CallBackString() {

            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(getBaseContext(), "Fail to connect to Server", Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "fail to updating record");
            }

            @Override
            public void onResponse(String response) {
                Log.e(LOG_TAG, "success to updating record");
            }
        });
    }

}

