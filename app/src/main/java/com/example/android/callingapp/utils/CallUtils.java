package com.example.android.callingapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.android.callingapp.DoctorsActivity;

public class CallUtils {

    public static void callPhoneNumber(String contact, Context context) {
//        if(TextUtils.isEmpty(contact))
//            return;
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{
                            Manifest.permission.CALL_PHONE}, 101);
//                    return;
                }

                Toast.makeText(context,"calling "+contact ,Toast.LENGTH_SHORT).show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contact));
                context.startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +contact));
                context.startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context,ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
