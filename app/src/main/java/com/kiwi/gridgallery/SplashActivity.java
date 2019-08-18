package com.kiwi.gridgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    Handler mDelayHandler = null;
    Long splashDelay = 3000L;
    String shownSplash = "shownSplashScreen";
    String prefsFiles = "com.kiwi.shopseek.prefs";
    SharedPreferences prefs = null;

    private Runnable mRunnable = new Runnable(){
        @Override
        public void run() {
            if(!isFinishing()){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(shownSplash, true);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = getSharedPreferences(prefsFiles, Context.MODE_PRIVATE);
        Boolean shownAlready = prefs.getBoolean(shownSplash, false);
        if(shownAlready){
            splashDelay = 500L;
        }
        mDelayHandler = new Handler();
        //Navigate with delay
        mDelayHandler.postDelayed(mRunnable, splashDelay);
    }

    @Override
    protected void onDestroy(){
        if(mDelayHandler != null){
            mDelayHandler.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }
}
