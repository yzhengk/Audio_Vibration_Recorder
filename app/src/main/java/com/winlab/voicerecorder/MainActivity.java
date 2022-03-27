package com.winlab.voicerecorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends WearableActivity {
    private static final String LOG_TAG = "MainActivity";
    private Chronometer mChronometer;
    private Switch switchRecorder;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private StringBuffer sensorLog = new StringBuffer();
    private int samplingPeriodUs = 10000; // 100Hz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        switchRecorder = (Switch) findViewById(R.id.switch_recorder);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
                sensorManager.registerListener(sensorEventListener, accelerometer, samplingPeriodUs);
                //keep screen on while recording
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }else{

                //1. stop timer
                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                Log.d(LOG_TAG, "stop recording service");
                // stop recording
                stopService(intent);
                // stop listen
                sensorManager.unregisterListener(sensorEventListener);
                try{

                    FileWriter writer_acc = new FileWriter(getBaseContext().getExternalFilesDir(
                            Environment.DIRECTORY_MUSIC)+ "/sensors_acc_" + System.currentTimeMillis() + ".csv");
                    writer_acc.write(sensorLog.toString());
                    sensorLog = new StringBuffer();
                    writer_acc.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //allow the screen to turn off again once recording is finished
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }

        }
    };


    // sensor listener
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            try {
                switch(event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        String str = String.format("%d,%f,%f,%f\n",
                                event.timestamp,
                                event.values[0],
                                event.values[1],
                                event.values[2]);
                        sensorLog.append(str);
//                        Log.d(LOG_TAG, str);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}

