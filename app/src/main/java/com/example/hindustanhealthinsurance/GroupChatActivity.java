package com.example.hindustanhealthinsurance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hindustanhealthinsurance.Adapters.MessageAdapter;
import com.example.hindustanhealthinsurance.Models.AllMethods;
import com.example.hindustanhealthinsurance.Models.Message;
import com.example.hindustanhealthinsurance.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference messageDb;

    MessageAdapter messageAdapter;
    User user;

    List<Message> messageList;

    RecyclerView rvMesssage;
    EditText eMessage;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_group_chat);

        initObjects();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GroupChatActivity.this, UserDashboard.class));
        finish();
    }

    private void initObjects() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        user = new User();

        rvMesssage = findViewById(R.id.rvMessage);
        eMessage = findViewById(R.id.eMessage);
        imageButton = findViewById(R.id.btnSend);
        imageButton.setOnClickListener(this);

        messageList = new ArrayList<>();
    }

    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(eMessage.getText().toString())) {
            Message message = new Message(eMessage.getText().toString(), user.getFirst_name());
            eMessage.setText("");
            messageDb.push().setValue(message);
        }
        else {
            Toast.makeText(GroupChatActivity.this, "You cannot send blank message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currentUser = auth.getCurrentUser();

        user.setUid(currentUser.getUid());
        user.setEmail(currentUser.getEmail());

        database.getReference("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    user.setUid(currentUser.getUid());

                    AllMethods.name = user.getFirst_name();
                }
                else {
                    onStart();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageDb = database.getReference("Messages");
        messageDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());

                messageList.add(message);

                displayMessages(messageList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());

                List<Message> newMessages = new ArrayList<>();
                for (Message m : messageList) {
                    if (m.getKey().equals(message.getKey()))
                        newMessages.add(message);
                    else
                        newMessages.add(m);
                }

                messageList = newMessages;
                displayMessages(messageList);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());

                List<Message> newMessages = new ArrayList<>();
                for (Message m : messageList) {
                    if (!m.getKey().equals(message.getKey()))
                        newMessages.add(m);
                }

                messageList = newMessages;
                displayMessages(messageList);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageList = new ArrayList<>();
    }

    private void displayMessages(List<Message> messageList) {
        rvMesssage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        messageAdapter = new MessageAdapter(GroupChatActivity.this, messageList, messageDb);
        rvMesssage.setAdapter(messageAdapter);
    }
}