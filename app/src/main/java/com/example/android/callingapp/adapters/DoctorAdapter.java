package com.example.android.callingapp.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.callingapp.DoctorsActivity;
import com.example.android.callingapp.R;
import com.example.android.callingapp.DataObjects.Doctor;
import com.example.android.callingapp.utils.CallUtils;

import java.util.List;

public class DoctorAdapter extends ArrayAdapter<Doctor> {
    private static final String TAG = DoctorAdapter.class.getSimpleName();

    public DoctorAdapter(@NonNull Context context, int resource, @NonNull List<Doctor> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=((Activity)getContext()).getLayoutInflater().inflate(R.layout.doctor_list_item,parent,false);
        }
        Doctor doctor=getItem(position);
        TextView doctorNameView=(TextView)convertView.findViewById(R.id.doctor_name_view);
        TextView doctorSpecialityView=(TextView) convertView.findViewById(R.id.speciality_text_view);
        TextView doctorPhoneView=(TextView) convertView.findViewById(R.id.doctor_phone_view);
//        View phoneCallDoctorImageView=convertView.findViewById(R.id.phone_call_doctor_imageview);
        doctorNameView.setText(doctor.getName());
        doctorSpecialityView.setText(doctor.getSpecialty());
        doctorPhoneView.setText(doctor.getTelephone());
        Glide.with(convertView.findViewById(R.id.photoDoctorImageView).getContext())
                .load(R.drawable.male_doctor2)
//                .transform(new CircleCrop())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG,"Image can't be loaded "+e.getMessage());
                        Toast.makeText(getContext(),"Still loading.... ",Toast.LENGTH_SHORT).show();
                        return false;

                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        imageProgressBar.setVisibility(View.GONE);

                        return false;
                    }
                })
                .circleCrop()
                .into((ImageView) convertView.findViewById(R.id.photoDoctorImageView));

        Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.slide_left);
        animation.setDuration(500);
        convertView.startAnimation(animation);



        return convertView;
    }
}
