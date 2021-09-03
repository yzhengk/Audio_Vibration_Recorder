package com.winlab.voicerecorder;

import android.app.Service;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.winlab.voicerecorder.utils.CallBackUtil;
import com.winlab.voicerecorder.utils.OkhttpUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * Created by Daniel on 12/28/2014.
 */
public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;


    private long mStartingTimeMillis = 0;
    private Long now = null;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }

    public void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(16000);
        setFileNameAndPath();
        mRecorder.setOutputFile(mFilePath);

        try {
            mRecorder.prepare();
            mRecorder.start();
            Toast.makeText(this, getString(R.string.toast_recording_start) + " " + mFilePath, Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "start recording");
            mStartingTimeMillis = System.currentTimeMillis();

            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed"+e.toString());
        }
    }

    public void setFileNameAndPath(){
        int count = 0;
        File f;
        now = System.currentTimeMillis();
        do{
            count++;
            //expName_time_count.mp4
            mFileName = "record_" + now + ".mp4";
//            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            mFilePath += "/SoundRecorder/" + mFileName;
//            f = new File(mFilePath);
            f = new File(getBaseContext().getExternalFilesDir(
                    Environment.DIRECTORY_MUSIC), mFileName);
            mFilePath = f.getAbsolutePath();
            Log.d(LOG_TAG, "create file" + mFilePath);

        }while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        Toast.makeText(this, getString(R.string.toast_recording_finish), Toast.LENGTH_LONG).show();
        mRecorder = null;

        // upload file to server

        Log.d(LOG_TAG, "upload audio: " +  mFilePath);
        File file = new File( mFilePath);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("machine", "wearable");
        parameters.put("name", mFileName);

        Log.d(LOG_TAG, "device: " + parameters.get("machine"));


        OkhttpUtil.okHttpUploadFile("http://"+Static.HOST+":5000/audio", file, "audio", OkhttpUtil.FILE_TYPE_AUDIO, parameters, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.d(LOG_TAG, "upload fail");
                Log.d(LOG_TAG, e.toString());
            }

            @Override
            public void onResponse(String response) {
                Log.d(LOG_TAG, "upload done");
            }
        });
    }

}
