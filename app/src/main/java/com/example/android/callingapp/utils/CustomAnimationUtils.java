package com.example.android.callingapp.utils;

import android.content.Context;
import android.view.animation.Animation;

import com.example.android.callingapp.R;

public class CustomAnimationUtils {

    public static Animation getSlideDownAnimation(Context context){
        Animation animation= android.view.animation.AnimationUtils.loadAnimation(context, R.anim.slide_down);
        animation.setDuration(2000);
        return animation;
    }

    public static Animation getSlideUpAnimation(Context context){
        Animation animation= android.view.animation.AnimationUtils.loadAnimation(context, R.anim.slide_up);
        animation.setDuration(2000);
        return animation;
    }
    public static Animation getSlideRightAnimation(Context context){
        Animation animation= android.view.animation.AnimationUtils.loadAnimation(context, R.anim.slide_right);
        animation.setDuration(2000);
        return animation;
    }
    public static Animation getSlideLeftAnimation(Context context){
        Animation animation= android.view.animation.AnimationUtils.loadAnimation(context, R.anim.slide_left);
        animation.setDuration(2000);
        return animation;
    }

    public static Animation getSlideLeftOutAnimation(Context context){
        Animation animation= android.view.animation.AnimationUtils.loadAnimation(context, R.anim.slide_left_out);
        animation.setDuration(2000);
        return animation;
    }

    public static Animation getSlideRightOutAnimation(Context context){
        Animation animation= android.view.animation.AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
        animation.setDuration(2000);
        return animation;
    }

}
