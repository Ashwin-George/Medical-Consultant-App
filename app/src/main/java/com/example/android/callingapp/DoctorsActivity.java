package com.example.android.callingapp;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.android.callingapp.DataObjects.Patient;
import com.example.android.callingapp.adapters.DoctorAdapter;
import com.example.android.callingapp.firebase.FirebaseHelper;
import com.example.android.callingapp.DataObjects.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.DoubleConsumer;


public class DoctorsActivity extends AppCompatActivity {

    private static final String TAG = DoctorsActivity.class.getSimpleName();
    private DatabaseReference doctorsDatabaseReference;
    private ProgressBar progressBar;
    private ListView doctorsListView;
    private ChildEventListener childEventListener;
    private FirebaseHelper helper;
    private String patientContact;
    private String patientName;
    AlertDialog alertDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        ArrayList<Doctor> doctorsList=new ArrayList<>();

        getSupportActionBar().setTitle("Doctors");
        getSupportActionBar().setIcon(R.drawable.male_doctor);
        helper = new FirebaseHelper();
        doctorsDatabaseReference=helper.getDoctorsDatabaseReference();
        progressBar= (ProgressBar) findViewById(R.id.progress_Bar);
        doctorsListView=(ListView) findViewById(R.id.doctors_list_view);

        DoctorAdapter adapter=new DoctorAdapter(this,R.layout.doctor_list_item,doctorsList);
        doctorsListView.setAdapter(adapter);


        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Doctor currentDoc=snapshot.getValue(Doctor.class);
                adapter.add(currentDoc);
                doctorsListView.smoothScrollToPosition(adapter.getCount());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Snackbar.make(findViewById(android.R.id.content),"succesfully removed", BaseTransientBottomBar.LENGTH_SHORT).show();
                Log.v(TAG,"Removed : "+snapshot.getValue());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        doctorsDatabaseReference.addChildEventListener(childEventListener);

        getDoctorsList();
        doctorsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Doctor doctor=adapter.getItem(position);
//                String patientDescription=patient.getDescription();
                makeSnackbar(doctor,view);
                return false;
            }
        });
    }

    public void getDoctorsList(){
        doctorsDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    Log.e(TAG,"Error in retrieving data ",task.getException());
                else{
                    int count= (int) task.getResult().getChildrenCount();
                    if(count==0){

                        Snackbar.make(findViewById(android.R.id.content),
                                "List Of Doctors not added",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.add_dummy_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyDoctors();
            }
        });

    }


    private void makeSnackbar(Doctor doctor, View v) {
        final Snackbar snackbar = Snackbar.make(v, "", Snackbar.LENGTH_LONG);

        // inflate the custom_snackbar_view created previously
        View customSnackView = getLayoutInflater().inflate(R.layout.description_snackbar, null);

        // set the background of the default snackbar as transparent
//        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // now change the layout of the snackbar
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbar.setAnchorView(v);
        // set padding of the all corners as 0
        snackbarLayout.setPadding(0, 0, 0, 0);

        // register the button from the custom_snackbar_view layout file
        ImageButton callButton = customSnackView.findViewById(R.id.call_btn);
        ImageButton chatButton = customSnackView.findViewById(R.id.chat_btn);
        TextView descriptionView=customSnackView.findViewById(R.id.patient_description_view);

        callButton.setBackgroundColor(Color.TRANSPARENT);
        chatButton.setBackgroundColor(Color.TRANSPARENT);
        descriptionView.setVisibility(View.GONE);


        // now handle the same button with onClickListener
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber(doctor.getTelephone());
                Toast.makeText(getApplicationContext(), "Calling doctor", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPatientContact(doctor);
                Toast.makeText(getApplicationContext(), "Chatting with doctor", Toast.LENGTH_SHORT).show();
                if(!alertDialog.isShowing()) {
                    Intent intent = new Intent(DoctorsActivity.this, MessagingActivity.class);
                    intent.putExtra(getResources().getString(R.string.DOCTOR_NAME_EXTRA_TAG), doctor.getName());
                    intent.putExtra(getResources().getString(R.string.DOCTOR_ID_EXTRA_TAG), doctor.getTelephone());
                    intent.putExtra(getResources().getString(R.string.PATIENT_NAME_EXTRA_TAG), patientName);
                    intent.putExtra(getResources().getString(R.string.PATIENT_ID_EXTRA_TAG), patientContact);
                    intent.putExtra(getResources().getString(R.string.SENDER_TYPE_EXTRA_TAG), 1);
                    startActivity(intent);
                }
                snackbar.dismiss();
            }
        });

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();
    }

    private void getPatientContact(Doctor doctor) {
        final String[] resultText = {""};
        Context context=DoctorsActivity.this;
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput1 = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        final EditText userInput2 = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput2);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text]
                                patientContact =userInput1.getText().toString().trim();
                                patientName=userInput2.getText().toString().trim();

                                Intent intent = new Intent(DoctorsActivity.this, MessagingActivity.class);
                                intent.putExtra(getResources().getString(R.string.DOCTOR_NAME_EXTRA_TAG), doctor.getName());
                                intent.putExtra(getResources().getString(R.string.DOCTOR_ID_EXTRA_TAG), doctor.getTelephone());
                                intent.putExtra(getResources().getString(R.string.PATIENT_NAME_EXTRA_TAG), patientName);
                                intent.putExtra(getResources().getString(R.string.PATIENT_ID_EXTRA_TAG), patientContact);
                                intent.putExtra(getResources().getString(R.string.SENDER_TYPE_EXTRA_TAG), 1);
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(DoctorsActivity.this).toBundle());

                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if (TextUtils.isEmpty(userInput1.getText().toString().trim()) ||
                                        TextUtils.isEmpty(userInput2.getText().toString().trim())){
                                    Toast.makeText(getApplicationContext(), "Enter Data correctly", Toast.LENGTH_SHORT).show();

                                }else
                                    dialog.cancel();
                            }
                        });

        // create alert dialog
        alertDialogBuilder.setIcon(android.R.drawable.sym_action_chat);
        alertDialogBuilder.setTitle("Patient Details");
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void dummyDoctors(){
        Doctor doctors[]={
                new Doctor("Dr. Ashwin George","Neurologist","8382922601"),
                new Doctor("Dr. Deepak Khandelwal","Child Specialist","8382922602"),
                new Doctor("Dr. Anubhav Tripathi","Chiropractor","8382922603"),
                new Doctor("Dr. Farha Khan","Gynaceologist","8382922604"),
                new Doctor("Dr. Ayush Singh","Dermatalogist","8382922605"),
                new Doctor("Dr. Vasu Jain","Gastroenterologist","8382922606"),
                new Doctor("Dr. Raj Priyadarshi","Hepatologist","8382922607"),
                new Doctor("Dr. Nisha Singh","ENT Specialist","8382922608"),
                new Doctor("Dr. Ashutosh Gupta","Urologist","8382922609")
        };

        for(Doctor doc:doctors){
            helper.addDoctor(doc).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Snackbar.make(findViewById(android.R.id.content),"Doctors added", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(findViewById(android.R.id.content),"doctors  Not Sent "+e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void callPhoneNumber(String contact) {
//        if(TextUtils.isEmpty(contact))
//            return;
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DoctorsActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE}, 101);
//                    return;
                }

                Toast.makeText(this,"calling "+contact ,Toast.LENGTH_SHORT).show();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contact));
                startActivity(callIntent);
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +contact));
                startActivity(callIntent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this,ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
