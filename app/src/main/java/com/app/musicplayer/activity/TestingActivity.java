package com.app.musicplayer.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.app.musicplayer.R;
import com.app.musicplayer.databinding.ActivityTestingBinding;

public class TestingActivity extends AppCompatActivity {

    ActivityTestingBinding bindingHome;
    String TAG = TestingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        bindingHome = ActivityTestingBinding.inflate(getLayoutInflater());
        setContentView(bindingHome.getRoot());
        bindingHome.toolbar.setTitle("");
        setSupportActionBar(bindingHome.toolbar);

//        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(5000);
//        rotate.setRepeatCount(ValueAnimator.INFINITE);
//        rotate.setInterpolator(new LinearInterpolator());
//        bindingHome.imageViewRotate.startAnimation(rotate);

//        RotateAnimation rotateAnimation = new RotateAnimation(TestingActivity.this, bindingHome.imageViewRotate);
//        rotateAnimation.setDuration(5000);
//        rotateAnimation.startAnimation();

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bindingHome.imageViewRotate, View.ROTATION, 0f, 360f).setDuration(5000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);


        bindingHome.imageViewRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectAnimator.isPaused()) {
                    objectAnimator.resume();
                } else if (objectAnimator.isRunning()) {
                    objectAnimator.pause();
                } else {
                    objectAnimator.start();
                }
            }
        });
    }
}