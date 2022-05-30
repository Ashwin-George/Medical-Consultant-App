package com.example.android.callingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.android.callingapp.DataObjects.Message;
import com.example.android.callingapp.adapters.MessageAdapter;
import com.example.android.callingapp.firebase.FirebaseHelper;
import com.example.android.callingapp.utils.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    private static final String TAG = MessagingActivity.class.getSimpleName();
    private static final int MAX_WORD_LIMIT = 1000;

    private ListView listView;
    private FloatingActionButton sendButton;
    private EditText messageEditTextBox;
    private ProgressBar progressBar;

    private int senderType;
    private String patientId;
    private String patientName;
    private String docId;
    private String docName;
    private String username;

    private FirebaseHelper helper;
    private DatabaseReference chatsDatabaseReference;
    private List<Message> messages;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);


        patientId =getIntent().getStringExtra(getString(R.string.PATIENT_ID_EXTRA_TAG));
        patientName=getIntent().getStringExtra(getString(R.string.PATIENT_NAME_EXTRA_TAG));
        docId =getIntent().getStringExtra(getString(R.string.DOCTOR_ID_EXTRA_TAG));
        docName =getIntent().getStringExtra(getString(R.string.DOCTOR_NAME_EXTRA_TAG));
        senderType=getIntent().getIntExtra(getString(R.string.SENDER_TYPE_EXTRA_TAG),0);
        helper=new FirebaseHelper();


        listView=(ListView) findViewById(R.id.message_list_view);
        messageEditTextBox=(EditText) findViewById(R.id.message_box);
        sendButton=(FloatingActionButton) findViewById(R.id.send_button);
        progressBar=(ProgressBar)  findViewById(R.id.progress_Bar);


        chatsDatabaseReference=helper.getSpecificChatDatabseReference(docId,patientId);
        messages=new ArrayList<>();

        if(senderType==1)
            username=docName;
        else if (senderType==0)
            username=patientName;
        getSupportActionBar().setTitle(username);
        getSupportActionBar().setIcon(android.R.drawable.sym_action_chat);
        loadAllMessages();
        MessageAdapter adapter=new MessageAdapter(this,R.layout.item_message,messages);
        listView.setAdapter(adapter);


        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message currentMsg=snapshot.getValue(Message.class);
                adapter.add(currentMsg);
//                createNotification(currentMsg.getAuthor());
                listView.smoothScrollToPosition(adapter.getCount());
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

        messageEditTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()>0)
                    sendButton.setEnabled(true);
                else
                    sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        messageEditTextBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_WORD_LIMIT)});

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                username=sharedPreferences.getString(getString(R.string.my_profile_username_key),"USER");
//                String username;
//                username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                String timeStamp= DateUtils.getCurrentTimestamp();
                Message message=new Message(messageEditTextBox.getText().toString(),username,timeStamp);
                helper.addMessage(message,chatsDatabaseReference).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        createNotification(message.getAuthor());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content),"Message Not Sent "+e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });

                messageEditTextBox.setText("");
            }
        });
        chatsDatabaseReference.addChildEventListener(childEventListener);


    }


    private void loadAllMessages(){
        chatsDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    Log.e(TAG,"Error in retrieving data ",task.getException());
                else{
                    int count= (int) task.getResult().getChildrenCount();
                    if(count==0){

                        Snackbar.make(findViewById(android.R.id.content),
                                "Let's start chatting",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}