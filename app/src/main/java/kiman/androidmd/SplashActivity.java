package kiman.androidmd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstTime();
            }
        },1500);
    }


    private void isFirstTime() {
        //맨처음 시작했는지 확인
        SharedPreferences preferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime",true);

        if (isFirstTime){
            //처음
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime",false);
            editor.apply();

//            SharedPreferences sharedPreferences = getApplication().getSharedPreferences("switch_start_check", Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean("switch_background",true);
            editor1.apply();

            //액티비티 이동
            startActivity(new Intent(SplashActivity.this,OnBoardActivity.class));
            finish();
        }
        else{
            //처음 아님
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }
    }
}