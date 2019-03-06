package com.immomo.videosdk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.IntRange;

/**
 * Created by XiongFangyu on 2017/4/5.
 *
 * 屏幕旋转工具
 * 通过加速传感器获取相对手机垂直的角度[0, 359]
 */
public class ScreenOrientationManager {
    private static final int X_INDEX = 0;
    private static final int Y_INDEX = 1;
    private static final int Z_INDEX = 2;
    private static final double RADIAN_TO_DEGREE = 180 / Math.PI;

    private static final int KEY_ANGLE = 0X11;

    private static volatile ScreenOrientationManager instance;

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener listener;
    private H h;
    private AngleChangedListener angleChangedListener;
    private boolean listening = false;

    public static ScreenOrientationManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ScreenOrientationManager.class) {
                if (instance == null) {
                    instance = new ScreenOrientationManager(context);
                }
            }
        }
        return instance;
    }

    private ScreenOrientationManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null)
            return;
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null)
            return;
        h = new H();
        listener = new SensorListener();
    }

    public void setAngleChangedListener(AngleChangedListener angleChangedListener) {
        if (sensor == null || sensorManager == null)
            return;
        this.angleChangedListener = angleChangedListener;
    }

    public void start() {
        if (sensor == null || sensorManager == null)
            return;
        listening = true;
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        if (sensor == null || sensorManager == null)
            return;
        listening = false;
        sensorManager.unregisterListener(listener);
    }

    public boolean isListening() {
        return listening;
    }

    public synchronized void releaseInternal() {
        stop();
        sensorManager = null;
        listener = null;
        sensor = null;
        h = null;
        angleChangedListener = null;
        instance = null;
    }

    public static void release() {
        if (instance != null)
            instance.releaseInternal();
    }

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == KEY_ANGLE) {
                if (angleChangedListener != null) {
                    angleChangedListener.onAngleChanged(msg.arg1);
                }
            }
        }
    }

    private class SensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            float x = -values[X_INDEX];
            float y = -values[Y_INDEX];
            float z = -values[Z_INDEX];
            int oangle = 0;
            float magnitude = x * x + y * y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= z * z) {
                double angle = Math.atan2(-y, x) * RADIAN_TO_DEGREE;
                oangle = (int) (90 - Math.round(angle));
                while (oangle >= 360) {
                    oangle -= 360;
                }
                while (oangle < 0) {
                    oangle += 360;
                }
            }
            if (h != null) {
                h.obtainMessage(KEY_ANGLE, oangle, 0).sendToTarget();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    }

    public interface AngleChangedListener {
        void onAngleChanged(@IntRange(from = 0, to = 359) int angle);
    }
}
