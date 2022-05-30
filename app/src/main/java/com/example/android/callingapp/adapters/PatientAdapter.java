package com.example.android.callingapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.android.callingapp.PatientActivity;
import com.example.android.callingapp.R;
import com.example.android.callingapp.DataObjects.Patient;

import java.util.List;

public class PatientAdapter extends ArrayAdapter<Patient> {
    private static final String TAG = PatientAdapter.class.getSimpleName();


    public PatientAdapter(@NonNull Context context, int resource, @NonNull List<Patient> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView=((Activity)getContext()).getLayoutInflater().inflate(R.layout.patient_list_item,parent,false);
        }
        Patient patient=getItem(position);
        TextView patientNameView =(TextView)convertView.findViewById(R.id.patient_name_view);
        TextView patientStatusView=(TextView) convertView.findViewById(R.id.patient_status_view);
//        TextView patientPhoneView=(TextView) convertView.findViewById(R.id.patient_phone_view);
        TextView patientGenderView=(TextView) convertView.findViewById(R.id.patient_gender_view);

        patientNameView.setText(patient.getName());
        patientStatusView.setText(patient.getStatus());
//        patientPhoneView.setText(patient.getPhone());
        String sex=(patient.getSex().equalsIgnoreCase("M"))?"Male":"Female";
        patientGenderView.setText(sex);

        int photoid=0;
        if(sex.equals("Male"))
            photoid=R.drawable.male_patient;
        else
            photoid=R.drawable.female_patient;

        Glide.with(convertView.findViewById(R.id.photoImageView).getContext())
                .load(photoid)
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
//                    .error(R.drawable.still_loading)

                .circleCrop()
//                .sizeMultiplier(0.2f)
                .into((ImageView) convertView.findViewById(R.id.photoImageView));

        return convertView;
    }
}
