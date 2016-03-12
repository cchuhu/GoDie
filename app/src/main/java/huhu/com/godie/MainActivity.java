package huhu.com.godie;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements SensorEventListener {
    //传感器管理器
    private SensorManager sensorManager;
    //加速度传感器实例
    private Sensor accSensor;
    //时间记录类
    private TimeLog timelog = new TimeLog();
    private static double height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
    }

    /**
     * 初始化传感器控件
     */
    private void initWidget() {
        //获取系统服务
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        //获取传感器实例
        //Sensor.TYPE_ACCELEROMETER获取的加速度实际上是手机运动的加速度与重力加速度的合加速度。
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 在resume中注册加速度监听器
     */
    @Override
    protected void onResume() {
        super.onResume();
        //设置监听器，最后rate参数指的是传感器的反应速度
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    /**
     * 注销传感器对象
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float z = sensorEvent.values[2];
        /*假设手机屏幕向上，静态下数值为10.几
        * 加速度开始大于10，是因为手机被上抛，处于超重状态
        * 在最高点的时候，处于完全失重状态，加速度显示为0，由此可用来判断抛起的开始与结束时间点*/
        if (timelog.getStartTime() == 0 && z > 11) {
            timelog.setStartTime(System.currentTimeMillis());
            Log.e("startTime", "" + timelog.getStartTime());
        }
        if (timelog.getStopTime() == 0 && z < 1) {
            timelog.setStopTime(System.currentTimeMillis());
            Log.e("stopTime", "" + timelog.getStopTime());
            //抛完一次，注销掉感应器
            sensorManager.unregisterListener(this, accSensor);
            //计算时间差：毫秒
            long time = timelog.getStopTime() - timelog.getStartTime();
            //将毫秒转换为double格式，不然精度不对
            double times = time / 1000.0;
            //应用物理学公式h=1/2 *g*t*t计算高度
            height = (times) * (times) * 4.9;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class TimeLog {
        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getStopTime() {
            return stopTime;
        }

        public void setStopTime(long stopTime) {
            this.stopTime = stopTime;
        }

        //保存开始时间和结束时间
        private long startTime = 0;
        private long stopTime = 0;
    }
}
