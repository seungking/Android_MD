package kiman.androidmd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tt.whorlviewlibrary.WhorlView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import kiman.androidmd.service.ManagePref;
import kiman.androidmd.ui.HowTODialog;
import stream.custombutton.CustomButton;

public class Gyro_Acc extends AppCompatActivity {

    String packageName = "";
    String appName ="";
    String appIcon = "";

    String packageName1 = "";
    ProgressBar progressBar;
    WhorlView onProgress;

    //센서들
    private SensorManager mSensorManager = null;
    private SensorEventListener mGyroLis;
    private Sensor mGgyroSensor = null;

    //모션 저장할 공간
    private ArrayList<String> Store_a = new ArrayList<>();
    //LCS 계산 배열
    private int[][] NIL = null;

    //모션 저장 변수
    String temp_a;
    ArrayList<ArrayList<String>> patterns = new ArrayList<>();

    // 몇 번째 모션인지 체크하는 변수
    int check = 0;

    //조건들
    private  int Arrsize = 5;
    private  int threshold = 2;
    private  int Arrsize_catch = 10;

    //셀프 매칭률
    private int self_matching_rate = 35;

    /*Used for Accelometer & Gyroscoper*/
    private UserSensorListner userSensorListner;
    private Sensor mGyroscopeSensor = null;
    private Sensor mAccelerometer = null;

    /*Sensor variables*/
    private float[] mGyroValues = new float[3];
    private float[] mAccValues = new float[3];
    private double mAccPitch, mAccRoll;

    /*for unsing complementary fliter*/
    private float a = 0.2f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private double pitch = 0, roll = 0, yaw = 0;
    private double prev_pitch = 0, prev_roll = 0, prev_yaw = 0;
    private double r=0, p=0;
    private double timestamp;
    private double dt;
    private double temp;
    private boolean gyroRunning;
    private boolean accRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyro__acc);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        showHowTo();
        Intent getintent = getIntent();
        packageName = getintent.getStringExtra("packagename");
        appName = getintent.getStringExtra("appname");
        appIcon = getintent.getStringExtra("appicon");

        progressBar = (ProgressBar)findViewById(R.id.add_motion_progress);
        onProgress = (WhorlView)findViewById(R.id.onprogress);

        findViewById(R.id.gyro_back).setOnClickListener(v->{
            finish();
        });

        //센서 켬 (여긴 왜켯지)
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroLis = new Gyro_Acc.AccelerometerListener();

        userSensorListner = new UserSensorListner();
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        final TextView textViewMotionPercent = (TextView) findViewById(R.id.textView_motion_percent);
        final TextView gyroCompleteText = (TextView) findViewById(R.id.gyroCompleteText);

        //클리어
        final CustomButton addclear = (CustomButton) findViewById(R.id.add_clear_ga);
        addclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View myView) {
                patterns.clear();
                Store_a.clear();
                check=0;
                gyroCompleteText.setText("0/5");
                progressBar.setProgress(0);
                textViewMotionPercent.setText("0%");
                Toasty.info(Gyro_Acc.this, "reset", Toast.LENGTH_SHORT).show();
            }
        });

        //애드 버튼 눌렀을때
        findViewById(R.id.add_motion_progress).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        //센서켬
                        mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);
                        mSensorManager.registerListener(userSensorListner, mGyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
                        mSensorManager.registerListener(userSensorListner, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

                        onProgress.setVisibility(View.VISIBLE);
                        onProgress.start();

                        break;

                    case MotionEvent.ACTION_UP:

                        //센서끄고 패턴에 저장
                        mSensorManager.unregisterListener(mGyroLis);
                        mSensorManager.unregisterListener(userSensorListner);

                        onProgress.stop();
                        onProgress.setVisibility(View.INVISIBLE);

                        //들어와있는거 청소
                        int self_matching_count = 0;
                        for (int i = 0; i < patterns.size(); i++) {//LCS돌려서 값 넘어야 변수에 ++
                            int temp_result = (int) (((float) (downtop(Store_a, patterns.get(i), Store_a.size(), patterns.get(i).size(), NIL)) / (float) Math.min(Store_a.size(), patterns.get(i).size())) * 100);
                            if (temp_result > self_matching_rate) self_matching_count++;
                        }
                        if ((Store_a.size()) > 2 && (self_matching_count == patterns.size())) {//전부 조건 만족하고 길이 2 이상이면 저장
                            check += 1;
                            ArrayList<String> temp = new ArrayList<>();
                            for(int i=0; i<Store_a.size(); i++) temp.add(Store_a.get(i));
                            patterns.add(temp);
                            Store_a.clear();
                            if (check == 1) {//한번 클릭마다 다음으로 넘어가면서 저장
                                textViewMotionPercent.setText("20%");
                                gyroCompleteText.setText("1/5");
                                progressBar.setProgress(20,true);
                                Toasty.success(Gyro_Acc.this, "not bad1", Toast.LENGTH_SHORT).show();
                            } else if (check == 2) {
                                textViewMotionPercent.setText("40%");
                                gyroCompleteText.setText("2/5");
                                progressBar.setProgress(40,true);
                                Toasty.success(Gyro_Acc.this, "good2", Toast.LENGTH_SHORT).show();
                            } else if (check == 3) {
                                textViewMotionPercent.setText("60%");
                                gyroCompleteText.setText("3/5");
                                progressBar.setProgress(60,true);
                                Toasty.success(Gyro_Acc.this, "great3", Toast.LENGTH_SHORT).show();
                            } else if (check == 4) {
                                textViewMotionPercent.setText("80%");
                                gyroCompleteText.setText("4/5");
                                progressBar.setProgress(80,true);
                                Toasty.success(Gyro_Acc.this, "perfect4", Toast.LENGTH_SHORT).show();
                            } else if (check == 5) {
                                textViewMotionPercent.setText("100%");
                                gyroCompleteText.setText("5/5");
                                progressBar.setProgress(100,true);
                                Toasty.success(Gyro_Acc.this, "wonderful5", Toast.LENGTH_SHORT).show();

                                String checksave = "";
                                for(int i=0; i<patterns.size(); i++) checksave+=patterns.get(i).toString();

                                long now = System.currentTimeMillis();
                                // 현재시간을 date 변수에 저장한다.
                                Date date1 = new Date(now);
                                // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                                SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                // nowDate 변수에 값을 저장한다.
                                String formatDate = sdfNow.format(date1);


                                Intent topaint = new Intent(Gyro_Acc.this,PaintingActivity.class);
                                topaint.putExtra("appname",appName);
                                topaint.putExtra("packagename",packageName);
                                topaint.putExtra("appicon",appIcon);
                                topaint.putExtra("date",formatDate);
                                topaint.putExtra("patterns",checksave);
                                startActivity(topaint);
                                finish();

                            } else {
                                Toast.makeText(Gyro_Acc.this, "full", Toast.LENGTH_SHORT).show();
                            }
                        } else {//만족못하면 다시하라고함
                            Toasty.error(Gyro_Acc.this, "retry", Toast.LENGTH_SHORT).show();
                        }

                        temp_a = "";
                        Store_a.clear();
                        break;
                }

                return false;
            }
        });

    }

    private class AccelerometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            //센서 바뀔때마다
            String Check_sum = "";
            int check_sum = 0;

            //임계값 하나라도 넘어가면 실행
            if (Math.abs(event.values[0]) >= threshold | Math.abs(event.values[1]) >= threshold | Math.abs(event.values[2]) >= threshold) {

                //값들 저장하고 정렬
                ArrayList<Float> eventvalues = new ArrayList<Float>();
                eventvalues.add(Math.abs(event.values[0]));
                eventvalues.add(Math.abs(event.values[1]));
                eventvalues.add(Math.abs(event.values[2]));
                Collections.sort(eventvalues);

                //값에 따라 라벨링
                if ((eventvalues.get(2) - (Math.abs(event.values[0])) < 0.01 || (eventvalues.get(1) - Math.abs(event.values[0])) < 0.01)) {
                    if (event.values[0] > 0) check_sum += 10000;
                    else check_sum += 20000;
                }
                if ((eventvalues.get(2) - (Math.abs(event.values[1])) < 0.01 || (eventvalues.get(1) - Math.abs(event.values[1])) < 0.01)) {
                    if (event.values[1] > 0) check_sum += 1000;
                    else check_sum += 2000;
                }
                if ((eventvalues.get(2) - (Math.abs(event.values[2])) < 0.01 || (eventvalues.get(1) - Math.abs(event.values[2])) < 0.01)) {
                    if (event.values[2] > 0) check_sum += 100;
                    else check_sum += 200;
                }

                if(roll>=0)check_sum+=10;
                else check_sum +=20;
                if(pitch>=0)check_sum+=1;
                else check_sum +=2;

                //라벨링 값 받아옴
                Check_sum = String.valueOf(check_sum);

//                Log.e("LOG1", " [X]:" + String.format("%.4f", event.values[0]) + "    [Y]:" + String.format("%.4f", event.values[1]) + "    [Z]:" + String.format("%.4f", event.values[2]) + "   roll : " + roll + "  r : " + r + "   pitch : " + pitch + "   p : " + p + "   Check_sum : " + Check_sum);

                //전에 값과 같거나 아무것도 안들어오면 거른다
                if ((Store_a.size() > 0) && !Store_a.get(Store_a.size() - 1).equals(Check_sum)) {

                    //사이즈는 사용자가 설정한 사이즈
                    int arrsize_temp = Arrsize;

                    if (Store_a.size() >= arrsize_temp) {//전에값 같으면 맨앞에꺼 삭제하고 삽입
                        Store_a.remove(0);
                        Store_a.add(Check_sum);
                    } else Store_a.add(Check_sum);//아니면 그냥삽입
                } else if (Store_a.size() == 0) Store_a.add(Check_sum);

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    int downtop(ArrayList<String> first, ArrayList<String> second, int m, int n, int[][] NIL)
    {
        // allocate memory
        NIL =new int[m+1][n+1];

        // calculate table
        for (int i = 0; i <= m; i++) // first
        {
            for (int j = 0; j <= n; j++) // second
            {
                if (i == 0 || j == 0) NIL[i][j] = 0; // initaliztion
                    // if elements are same, insert previous value + 1
                else if (first.get(i - 1).equals(second.get(j - 1))) NIL[i][j] = NIL[i - 1][j - 1] + 1;
                else {// insert max value into table
                    int temp = 0;
                    if (NIL[i - 1][j] > NIL[i][j - 1]) temp = NIL[i - 1][j];
                    else temp = NIL[i][j - 1];
                    NIL[i][j] = temp;
                }
            }
        }

        return NIL[m][n];// return last value
    }

    /**
     * 1차 상보필터 적용 메서드 */
    private void complementaty(double new_ts) {

        /* 자이로랑 가속 해제 */
        gyroRunning = false;
        accRunning = false;

        /*센서 값 첫 출력시 dt(=timestamp - event.timestamp)에 오차가 생기므로 처음엔 break */
        if (timestamp == 0) {
            timestamp = new_ts;
            return;
        }
        dt = (new_ts - timestamp) * NS2S; // ns->s 변환
        timestamp = new_ts;

        /* degree measure for accelerometer */
        mAccPitch = -Math.atan2(mAccValues[0], mAccValues[2]) * 180.0 / Math.PI; // Y 축 기준
        mAccRoll = Math.atan2(mAccValues[1], mAccValues[2]) * 180.0 / Math.PI; // X 축 기준

        /**
         * 1st complementary filter.
         *  mGyroValuess : 각속도 성분.
         *  mAccPitch : 가속도계를 통해 얻어낸 회전각.
         */
        temp = (1 / a) * (mAccPitch - pitch) + mGyroValues[1];
        pitch = pitch + (temp * dt);

        temp = (1 / a) * (mAccRoll - roll) + mGyroValues[0];
        roll = roll + (temp * dt);

        r = Math.abs(prev_roll - roll);
        p = Math.abs(prev_pitch - pitch);

        prev_roll = roll;
        prev_pitch = pitch;

        Log.d("gyroacc11", roll + "    " + pitch);
    }

    public class UserSensorListner implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {

                /** GYROSCOPE */
                case Sensor.TYPE_GYROSCOPE:

                    /*센서 값을 mGyroValues에 저장*/
                    mGyroValues = event.values;

                    if (!gyroRunning)
                        gyroRunning = true;

                    break;

                /** ACCELEROMETER */
                case Sensor.TYPE_ACCELEROMETER:

                    /*센서 값을 mAccValues에 저장*/
                    mAccValues = event.values;

                    if (!accRunning)
                        accRunning = true;

                    break;
            }

            /**두 센서 새로운 값을 받으면 상보필터 적용*/
            if (gyroRunning && accRunning) {
                complementaty(event.timestamp);
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public void showHowTo(){
        findViewById(R.id.add_motion_progress).setClickable(false);
        findViewById(R.id.add_clear_ga).setClickable(false);

        HowTODialog mDialog1 = new HowTODialog(Gyro_Acc.this, R.layout.layout_howto1);

        mDialog1.show();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = mDialog1.getWindow();

        window.setLayout(size.x, size.y);

        mDialog1.findViewById(R.id.howto_layout).setOnClickListener(v->{
//            mDialog1.findViewById(R.id.howto_layout).setVisibility(View.INVISIBLE);
//            mDialog2.show();
            mDialog1.dismiss();
        });
//        mDialog2.findViewById(R.id.howto_layout).setOnClickListener(v->{
//            mDialog2.findViewById(R.id.howto_layout).setVisibility(View.INVISIBLE);
//            mDialog3.show();
//            mDialog2.dismiss();
//        });
//        mDialog3.findViewById(R.id.howto_layout).setOnClickListener(v->{
//            mDialog3.dismiss();
//        });

        findViewById(R.id.add_motion_progress).setClickable(true);
        findViewById(R.id.add_clear_ga).setClickable(true);
    }
}