package com.project.safereturn;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        timerDecibel = new Timer();
        timerTaskDecibel = new TimerTask() {
            @Override
            public void run() {
                //handler.post(runnable);
                int decibel = getDecibel();
                if(60 <  decibel) {
                    Log.e("alarm", String.valueOf(decibel));
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
                Log.d("detect", "detack");
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
