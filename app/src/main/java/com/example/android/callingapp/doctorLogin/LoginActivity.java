package com.example.android.callingapp.doctorLogin;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.android.callingapp.PatientActivity;
import com.example.android.callingapp.R;
import com.example.android.callingapp.firebase.FirebaseHelper;
import com.example.android.callingapp.DataObjects.LoginCredential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextInputEditText userContactInputTextBox;
    private TextInputEditText userPswdInputTextBox;
    private Button loginSubmitButton;
    private ProgressBar progressBar;
    private ChildEventListener childEventListener;
    private ArrayList<LoginCredential> credentialList;
    private DatabaseReference loginDatabaseReference;
    private FirebaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        userContactInputTextBox =(TextInputEditText) findViewById(R.id.phone_login_textbox);
        userPswdInputTextBox=(TextInputEditText) findViewById(R.id.userpswd_login_textbox);
        loginSubmitButton=(Button)findViewById(R.id.login_submit_button);

        progressBar=(ProgressBar) findViewById(R.id.progress_bar_login);
        credentialList = new ArrayList<>();
        helper=new FirebaseHelper();
        loginDatabaseReference =helper.getLoginDatabaseReference();
        loadAllCredentials();



        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LoginCredential credential=snapshot.getValue(LoginCredential.class);
                credentialList.add(credential);
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


        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userContact=userContactInputTextBox.getText().toString().trim();
                String userPswd=userPswdInputTextBox.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if(TextUtils.isEmpty(userContact)&& TextUtils.isEmpty(userPswd)){
                    Toast.makeText(getApplicationContext(),"Enter credentials",Toast.LENGTH_SHORT).show();
                }else{
                    boolean found=false;
                    for(LoginCredential credential:credentialList){
                        if(userContact.equals(credential.getPhoneNo()) && userPswd.equals(credential.getPassword())){
                            found=true;
                            Snackbar.make(findViewById(android.R.id.content),"Login successful", BaseTransientBottomBar.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this, PatientActivity.class);
                            intent.putExtra(getString(R.string.user_contact_no_extra_tag) ,credential.getPhoneNo());
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                        }
                    }
                    if(!found){
                        userContactInputTextBox.setText("");
                        userPswdInputTextBox.setText("");
                        Snackbar.make(findViewById(android.R.id.content),"Wrong Credentials", BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

            }
        });
        loginDatabaseReference.addChildEventListener(childEventListener);
        findViewById(R.id.add_dummy_login_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dummyLoginInfo();
            }
        });
    }

    private void loadAllCredentials() {

        loginDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    Log.e(TAG,"Error in retrieving data ",task.getException());
                else{
                    int count= (int) task.getResult().getChildrenCount();
                    if(count==0){

                        Snackbar.make(findViewById(android.R.id.content),
                                "No doctors found",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    public void dummyLoginInfo(){
        progressBar.setVisibility(View.VISIBLE);
        LoginCredential credentials[]={
                new LoginCredential("8382922600","123456"),
                new LoginCredential("8318158803","123456")

        };

        for(LoginCredential credential:credentials){
            helper.addLoginCredential(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Snackbar.make(findViewById(android.R.id.content),"Credentials added", BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(findViewById(android.R.id.content),"Credentials not added "+e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            });
        }

    }
}