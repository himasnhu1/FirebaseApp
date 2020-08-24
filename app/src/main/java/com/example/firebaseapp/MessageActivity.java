package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.Adapter.MessageAdapter;
import com.example.firebaseapp.Model.Chat;
import com.example.firebaseapp.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    TextView username;
    ImageView imageView;

    RecyclerView recyclerView;
    EditText msg_editText;
    ImageButton sendbtn;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerViewy;
    String userid;

    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        imageView = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.usernamey);

        sendbtn = findViewById(R.id.btn_send);
        msg_editText = findViewById(R.id.text_send);


        //RecyclerView
        recyclerViewy = findViewById(R.id.recycleView);
        recyclerViewy.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);






//      //  Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar2);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        intent = getIntent();
      userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child("userid");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                username.setText(users.getUsername());
                if(users.getImageURL().equals("default")){
                   imageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    //Adding Glide Library
                    Glide.with(MessageActivity.this)
                            .load(users.getImageURL())
                            .into(imageView);
                }

                readMesagges(fuser.getUid(),userid, users.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msg_editText.getText().toString();
                if(!msg.equals(""))
                {
                    sendMessage(fuser.getUid(),userid,msg);

                }else{
                    Toast.makeText(MessageActivity.this, "Please send non empty message ", Toast.LENGTH_SHORT).show();
                }
                msg_editText.setText("");
            }
        });
        SeenMessage(userid)  ;
    }

    private void SeenMessage(String userid){

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           for(DataSnapshot dataSnapshot :snapshot.getChildren()){

               Chat chat = snapshot.getValue(Chat.class);
               if (chat.getReceiver().equals(fuser.getUid()) &&  chat.getSender().equals(userid)){

                   HashMap<String,Object> hashMap = new HashMap<>();

                   hashMap.put("isseen",true);
                   snapshot.getRef().updateChildren(hashMap);

               }
           }
        }
             @Override
             public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

     private void sendMessage(String sender,String receiver,String message){
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
      HashMap<String,Object> hashMap = new HashMap<>();
      hashMap.put("sender",sender);
      hashMap.put("receiver",receiver);
      hashMap.put("message",message);
      hashMap.put("isseen",false);

      reference.child("chats").push().setValue(hashMap);
         // add user to chat fragment
         final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                 .child(fuser.getUid())
                 .child(userid);

         chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (!dataSnapshot.exists()){
                     chatRef.child("id").setValue(userid);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

  }


    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();
      reference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              mchat.clear();
              for(DataSnapshot datasnapshot1:snapshot.getChildren()){
                  Chat chat = datasnapshot1.getValue(Chat.class);
                  if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                          chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                      mchat.add(chat);
                  }

                  messageAdapter =new MessageAdapter(MessageActivity.this,mchat,imageurl);
                  recyclerViewy.setAdapter(messageAdapter);

              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }

    private void Checkstatus(String status) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        reference.updateChildren(hashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Checkstatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        Checkstatus("offline");

    }

}