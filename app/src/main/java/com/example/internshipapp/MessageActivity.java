package com.example.internshipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.internshipapp.Adapter.MessageAdapter;
import com.example.internshipapp.Adapter.UserAdapter;
import com.example.internshipapp.model.Chat;
import com.example.internshipapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_img;
    TextView Username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ImageButton send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat>mchat=new ArrayList<>();

    RecyclerView recyclerView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        profile_img = findViewById(R.id.Profile_image1);
        Username = findViewById(R.id.username_1);
        send = findViewById(R.id.send);
        text_send = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view1);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setAdapter();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        final String userid=intent.getStringExtra("userid");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }
                else {
                    Toast.makeText(MessageActivity.this, "You Cant send Empty Message!", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("User").child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                    url = user.getImgURL();
                Username.setText(user.getUsername());

                    if (user.getImgURL()==null) {
                        profile_img.setImageResource(R.mipmap.ic_launcher);
                    }
                    else {
                        Glide.with(MessageActivity.this).load(user.getImgURL()).into(profile_img);
                    }
                    //setAdapter();
                    Log.d("Above Function call!","is func running Line 119");
                    readMessages(firebaseUser.getUid(),userid,user.getImgURL());

                    //Toast.makeText(MessageActivity.this, ""+e, Toast.LENGTH_SHORT).show();


                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender,String reciever,String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void setAdapter() {
        messageAdapter = new MessageAdapter(MessageActivity.this,mchat,url);
        recyclerView.setAdapter(messageAdapter);
    }
    private void readMessages(final String myid, final String userid, final String imageurl){

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("sanp",dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    String phone = "" , name = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {

                        Chat chat = new Chat(childSnapshot.child("sender").getValue().toString(),childSnapshot.child("reciever").getValue().toString(),childSnapshot.child("message").getValue().toString());
                        mchat.add(chat);
                        Log.e("message",(childSnapshot.child("message").getValue().toString()));
                        messageAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
