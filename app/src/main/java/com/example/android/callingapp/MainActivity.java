package com.example.android.callingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.android.callingapp.doctorLogin.LoginActivity;
import com.example.android.callingapp.utils.CustomAnimationUtils;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CardView doctorContainerView=(CardView) findViewById(R.id.doctor_btn_container);
        CardView patientContainerView=(CardView) findViewById(R.id.patient_btn_container);
        TextView doctorTextView=(TextView) findViewById(R.id.doctor_text_view);
        TextView patientTextView=(TextView) findViewById(R.id.patient_text_view);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        doctorContainerView.startAnimation(CustomAnimationUtils.getSlideDownAnimation(this));
        patientContainerView.startAnimation(CustomAnimationUtils.getSlideUpAnimation(this));
        doctorTextView.startAnimation(CustomAnimationUtils.getSlideLeftAnimation(this));
        patientTextView.startAnimation(CustomAnimationUtils.getSlideRightAnimation(this));

        doctorContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        patientContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,DoctorsActivity.class);
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
    }
}