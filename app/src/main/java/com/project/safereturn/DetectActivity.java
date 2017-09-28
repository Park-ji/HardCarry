package com.project.safereturn;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DetectActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor sensor;

    private long shakeTime;

    private MediaRecorder recorder;
    private Runnable runnable = new Runnable(){

        public void run(){
            Log.e("df", String.valueOf(getDecibel()));
        }
    };
    private Timer timerDecibel;
    private TimerTask timerTaskDecibel;

    private double curLatitude = 0;
    private double curLongitude = 0;

    private SmsManager smsManager;

    private boolean isSendSMS = false;

    private ImageView imageViewCurLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        curLatitude = MainActivity.latitude;
        curLongitude = MainActivity.longitude;
        final double rangeDistance = Math.sqrt(0.001794 * 0.001794 + 0.002549 * 0.002549);


        timerDecibel = new Timer();
        timerTaskDecibel = new TimerTask() {
            @Override
            public void run() {
                //handler.post(runnable);
                int decibel = getDecibel();
                if(90 <  decibel) {
                    if(!isSendSMS && MyApplication.preferences.getBoolean("shouting", false)) {
                        sendSMS();
                        isSendSMS = true;
                    }
                }

                double dx = curLatitude - MainActivity.latitude;
                double dy = curLongitude - MainActivity.longitude;
                double pointToCurPositionDistance = Math.sqrt(dx * dx + dy * dy);

                if(rangeDistance < pointToCurPositionDistance) {
                    if(!isSendSMS && MyApplication.preferences.getBoolean("lock", false)) {
                        sendSMS();
                        isSendSMS = true;
                    }
                }
            }
        };

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(Environment.getExternalStorageDirectory() + "/Download/sample.mp4");

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        timerDecibel.schedule(timerTaskDecibel, 0, 500);

        smsManager = SmsManager.getDefault();

        imageViewCurLocation = (ImageView) findViewById(R.id.imageView_cur_location);
        imageViewCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
            }
        });
    }

    private int getAmplitude() {
        return  recorder.getMaxAmplitude();
    }

    private int getDecibel() {
        int amplitude = getAmplitude();

        if(!Double.isInfinite((20 * Math.log10(amplitude)))) {
            return (int)(20 * Math.log10(amplitude));
        }
        else {
            return -1;
        }
    }

    private void sendSMS() {
        String message = "위험한상황에 처했습니다 도와주세요\nhttps://www.google.co.kr/maps/@"+MainActivity.latitude+","+MainActivity.longitude;

        String phone01 = MyApplication.preferences.getString("phone01", null);
        String phone02 = MyApplication.preferences.getString("phone02", null);
        String phone03 = MyApplication.preferences.getString("phone03", null);

        try {
            if(phone01 != null)
                smsManager.sendTextMessage(phone01, null, message, null, null);
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(phone02 != null)
                smsManager.sendTextMessage(phone02, null, message, null, null);
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(phone03 != null)
                smsManager.sendTextMessage(phone03, null, message, null, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float axisX = sensorEvent.values[0];
            float axisY = sensorEvent.values[1];
            float axisZ = sensorEvent.values[2];

            float gravityX = axisX / SensorManager.GRAVITY_EARTH;
            float gravityY = axisY / SensorManager.GRAVITY_EARTH;
            float gravityZ = axisZ / SensorManager.GRAVITY_EARTH;

            float f= gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ;
            double squaredD = Math.sqrt(f);
            float gForce = (float) squaredD;
            if(gForce > 2.7f) {
                long currentTime = System.currentTimeMillis();
                if(shakeTime + 500 > currentTime) {
                    return;
                }

                shakeTime = currentTime;

                if(!isSendSMS && MyApplication.preferences.getBoolean("shake", false)) {
                    sendSMS();
                    isSendSMS = true;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(this);
        timerDecibel.cancel();
        recorder.stop();
        recorder.release();
        recorder = null;
        super.onDestroy();
    }
}
