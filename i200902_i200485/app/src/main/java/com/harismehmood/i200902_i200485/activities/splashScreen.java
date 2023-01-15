package com.harismehmood.i200902_i200485.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.sharedPreferences.PreferencesManager;
import com.harismehmood.i200902_i200485.utilities.Constants;

public class splashScreen extends AppCompatActivity {
public static int SPLASH_TIME_OUT = 3200;
SharedPreferences onBoardingScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
                if (isFirstTime) {
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();
                    intent = new Intent(splashScreen.this, onboarding.class);

                } else {
                    if(preferencesManager.getBoolean(Constants.KEY_IS_SIGNED_IN))
                        intent = new Intent(splashScreen.this, MainActivity.class);
                    else
                        intent = new Intent(splashScreen.this, main_login_signup_activity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}