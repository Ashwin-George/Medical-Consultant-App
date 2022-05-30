package com.example.android.callingapp.firebase;

import com.example.android.callingapp.DataObjects.Doctor;
import com.example.android.callingapp.DataObjects.LoginCredential;
import com.example.android.callingapp.DataObjects.Message;
import com.example.android.callingapp.DataObjects.Patient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference doctorsDatabaseReference;
    private final DatabaseReference patientDatabaseReference;
    private final DatabaseReference loginDatabaseReference;
    private final DatabaseReference chatDatabaseReference;


    public FirebaseHelper() {
        firebaseDatabase =FirebaseDatabase.getInstance();

        doctorsDatabaseReference = firebaseDatabase.getReference().child("doctors");
        patientDatabaseReference = firebaseDatabase.getReference().child("patients");
        loginDatabaseReference = firebaseDatabase.getReference().child("login_info");
        chatDatabaseReference = firebaseDatabase.getReference().child("chats");
    }

    public Task<Void>  addDoctor(Doctor doc){
        return doctorsDatabaseReference.push().setValue(doc);
    }
    public Task<Void>  addPatient(Patient patient){
        return patientDatabaseReference.push().setValue(patient);
    }
    public Task<Void> addLoginCredential(LoginCredential credential){
        return loginDatabaseReference.push().setValue(credential);
    }
    public Task<Void> addMessage(Message message,DatabaseReference reference) {
        return reference.push().setValue(message);
    }


    public DatabaseReference getDoctorsDatabaseReference() {
        return doctorsDatabaseReference;
    }

    public DatabaseReference getPatientDatabaseReference() {
        return patientDatabaseReference;
    }

    public DatabaseReference getLoginDatabaseReference() {
        return loginDatabaseReference;
    }

    public DatabaseReference getChatDatabaseReference() {
        return chatDatabaseReference;
    }
    public DatabaseReference getSpecificChatDatabseReference(String docId,String patientId){
        return firebaseDatabase.getReference().child("chats/"+docId+"-"+patientId);
    }

}
