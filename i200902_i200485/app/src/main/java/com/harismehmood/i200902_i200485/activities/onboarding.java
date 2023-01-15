package com.harismehmood.i200902_i200485.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harismehmood.i200902_i200485.R;
import com.harismehmood.i200902_i200485.adapters.OnBoardSliderAdapter;

public class onboarding extends AppCompatActivity {
ViewPager viewPager;
LinearLayout dotsLayout;
TextView dots[],skipText;
ImageButton nextBtn;
Button getStartedBtn;
OnBoardSliderAdapter onBoardSliderAdapter;
Animation animation;

public static int SKIP_BUTTON_VISIBLE_SLIDE_LIMIT = 2;
public static int TOTAL_ON_BOARDING_SLIDES = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.onBoardViewPager);
        dotsLayout = findViewById(R.id.onBoardingDots);
        nextBtn = findViewById(R.id.onBoardNextBtn);
        getStartedBtn = findViewById(R.id.OnBoardGetStartBtn);
        //getStartedBtn.setVisibility(View.INVISIBLE);
        skipText = findViewById(R.id.skipText);
        //skipText.setVisibility(View.INVISIBLE);
        onBoardSliderAdapter = new OnBoardSliderAdapter(this);
        viewPager.setAdapter(onBoardSliderAdapter);

        //setting dotes on the bottom
        dotAdd(0);
        viewPager.addOnPageChangeListener(viewListener);

        //skip text on click to skip all the slides
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(onboarding.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                viewPager.setCurrentItem(TOTAL_ON_BOARDING_SLIDES-1);
            }
        });

    }
    private void dotAdd(int position){
        dots = new TextView[TOTAL_ON_BOARDING_SLIDES];
        dotsLayout.removeAllViews();
        for(int i = 0; i < TOTAL_ON_BOARDING_SLIDES; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

           dotsLayout.addView(dots[i]);
        }
        //set the color of the current dot
        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.purple_500));
        }


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(position+1);
            }
        });

    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {


            if(position >= SKIP_BUTTON_VISIBLE_SLIDE_LIMIT-1){
                skipText.setVisibility(View.VISIBLE);
            }
            else{
                skipText.setVisibility(View.INVISIBLE);
            }
            //if the slide is the last one then change the visibility of get started button and on click listener to open the main activities
            if(position == TOTAL_ON_BOARDING_SLIDES-1){
                skipText.setVisibility(View.INVISIBLE);
                nextBtn.setVisibility(View.INVISIBLE);
                dotsLayout.setVisibility(View.INVISIBLE);
                //setting animation of the get started button
                animation = AnimationUtils.loadAnimation(onboarding.this,R.anim.onboard_getstartedbtn_anim);
                getStartedBtn.setAnimation(animation);
                getStartedBtn.setVisibility(View.VISIBLE);
                getStartedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(onboarding.this, main_login_signup_activity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            else{
                nextBtn.setVisibility(View.VISIBLE);
                dotsLayout.setVisibility(View.VISIBLE);
                getStartedBtn.setVisibility(View.INVISIBLE);
            }

            dotAdd(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}