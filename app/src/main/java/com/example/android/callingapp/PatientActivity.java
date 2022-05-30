package com.example.android.callingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.callingapp.adapters.PatientAdapter;
import com.example.android.callingapp.doctorLogin.LoginActivity;
import com.example.android.callingapp.firebase.FirebaseHelper;
import com.example.android.callingapp.DataObjects.Doctor;
import com.example.android.callingapp.DataObjects.Patient;
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

public class PatientActivity extends AppCompatActivity {
    private static final String TAG = PatientActivity.class.getSimpleName();
    DatabaseReference patientDatabaseRefernece;
    ProgressBar progressBar;
    private ListView patientsListView;
    private ChildEventListener childEventListener;
    private FirebaseHelper helper;
    private String doctorContact ="Anonymous";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        if(getIntent().hasExtra(getResources().getString(R.string.user_contact_no_extra_tag)))
        doctorContact =getIntent().getStringExtra(getResources().getString(R.string.user_contact_no_extra_tag));
        ArrayList<Patient> patientsList=new ArrayList<>();
        getSupportActionBar().setTitle("Patients");

        helper = new FirebaseHelper();
        patientDatabaseRefernece=helper.getPatientDatabaseReference();
        progressBar= (ProgressBar) findViewById(R.id.progress_Bar);
        patientsListView=(ListView) findViewById(R.id.patient_list_view);


        PatientAdapter adapter=new PatientAdapter(this,R.layout.patient_list_item,patientsList);
        patientsListView.setAdapter(adapter);


        findViewById(R.id.add_dummy_pateint_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyPatients();
            }
        });

        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Patient currentPatient=snapshot.getValue(Patient.class);
                if(currentPatient.getDoctor().getTelephone().equals(doctorContact)) {
                    adapter.add(currentPatient);

                    patientsListView.smoothScrollToPosition(adapter.getCount());
                }
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

        patientDatabaseRefernece.addChildEventListener(childEventListener);

        getPatientsList();


        patientsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Patient patient=adapter.getItem(position);
                String patientDescription=patient.getDescription();
                makeSnackbar(patient,view);
                return false;
            }
        });


    }

    private void makeSnackbar(Patient patient, View v) {
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
        descriptionView.setText(patient.getDescription());


        // now handle the same button with onClickListener
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhoneNumber(patient.getPhone());
                Toast.makeText(getApplicationContext(), "Calling patient", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PatientActivity.this,MessagingActivity.class);
                intent.putExtra(getResources().getString(R.string.DOCTOR_NAME_EXTRA_TAG),patient.getDoctor().getName());
                intent.putExtra(getResources().getString(R.string.DOCTOR_ID_EXTRA_TAG),patient.getDoctor().getTelephone());
                intent.putExtra(getResources().getString(R.string.PATIENT_NAME_EXTRA_TAG),patient.getName());
                intent.putExtra(getResources().getString(R.string.PATIENT_ID_EXTRA_TAG),patient.getPhone());
                intent.putExtra(getResources().getString(R.string.SENDER_TYPE_EXTRA_TAG),0);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(PatientActivity.this).toBundle());
                Toast.makeText(getApplicationContext(), "Chatting with patient", Toast.LENGTH_SHORT).show();
                snackbar.dismiss();
            }
        });

        // add the custom snack bar layout to snackbar layout
        snackbarLayout.addView(customSnackView, 0);

        snackbar.show();
    }


    public void getPatientsList(){
        progressBar.setVisibility(View.VISIBLE);
        patientDatabaseRefernece.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    Log.e(TAG,"Error in retrieving data ",task.getException());
                else{
                    int count= (int) task.getResult().getChildrenCount();
                    if(count==0){

                        Snackbar.make(findViewById(android.R.id.content),
                                "List Of Patients not added",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    public void dummyPatients(){
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
        Patient patients[]={
                new Patient("Ankit Kumar","8765351470","M","Normal","Mild Fever and cough",doctors[0]),
                new Patient("Deeksha Sharma","8765351471","F","Critical","Severe bone damage and Cranial damage",doctors[2]),
                new Patient("Nitya Patyal","8765351472","F","Critical","Mental Disorder",doctors[0]),
                new Patient("Priyanshu Ladha","8765351473","M","Undergoing treatment","Stiff Joints and Bones",doctors[2]),
                new Patient("Kritika Gupta","8765351474","F","Under Observation","High fever and cough",doctors[3]),
                new Patient("Ayush Kumar Singh","8765351475","M","Critical","Severe Stomach Ache",doctors[5]),
                new Patient("Anmol Garg","8765351476","M","Ongoing Treatment","Hairfall and Dead Skin",doctors[4]),
                new Patient("Gauransh Madan","87656351477","M","Recovered","Pain in ear canal",doctors[7])
        };

        for(Patient patient:patients){
            helper.addPatient(patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Snackbar.make(findViewById(android.R.id.content),"Patients added", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(findViewById(android.R.id.content),"Patient not added "+e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });
        }

    }
//
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhoneNumber("");
            }
        }

    }
    public void callPhoneNumber(String contact) {
//        if(TextUtils.isEmpty(contact))
//            return;
        try {
            if (Build.VERSION.SDK_INT > 22) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PatientActivity.this, new String[]{
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