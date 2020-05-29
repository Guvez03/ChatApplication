package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilToChatActivity extends AppCompatActivity {

     private String receiverId,current_state,currentUserId;
     private TextView user_profil_status,user_profil_name;
     private Button send_message_request_button,decline_message_request_button;
     private CircleImageView visit_profile_image;
     private DatabaseReference UserRef,ChatRequestRef,ContactsRef,NotificationRef;
     private FirebaseAuth mAuth;
     Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_to_chat);


        receiverId = getIntent().getExtras().get("visituserıd").toString();

        toolbar = (Toolbar) findViewById(R.id.toolbar);



        user_profil_status = findViewById(R.id.user_profil_status);
        user_profil_name = findViewById(R.id.user_profil_name);
        send_message_request_button = findViewById(R.id.send_message_request_button);
        visit_profile_image = findViewById(R.id.visit_profile_image);
        decline_message_request_button = findViewById(R.id.decline_message_request_button);

        current_state = "new";

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        System.out.println(" \n \n \n");
        System.out.println(currentUserId + " \n \n \n");
        System.out.println(receiverId);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Profiles");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        ContactsRef =  FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef =  FirebaseDatabase.getInstance().getReference().child("Notifications");

        KullanıcıBilgileriniAl();
    }
    private void KullanıcıBilgileriniAl(){ //

        UserRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.hasChild("userimageurl")){ // eğer kullanıcının resmi varsa

                    String userImage = dataSnapshot.child("userimageurl").getValue().toString();
                    String userName = dataSnapshot.child("username").getValue().toString();
                    String userStatus = dataSnapshot.child("userstatus").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(visit_profile_image);
                    user_profil_status.setText(userStatus);
                    user_profil_name.setText(userName);

                    ChatIstegiYönet();

                }
                else{   // yoksa eğer
                    String userName = dataSnapshot.child("username").getValue().toString();
                    String userStatus = dataSnapshot.child("userstatus").getValue().toString();

                    user_profil_status.setText(userStatus);
                    user_profil_name.setText(userName);

                    ChatIstegiYönet();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void ChatIstegiYönet() {

        ChatRequestRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receiverId)){
                    String request_type = dataSnapshot.child(receiverId).child("request_type").getValue().toString();

                    if(request_type.equals("gönderilen")){
                        current_state = "request_sent";
                        send_message_request_button.setText("Chat İsteğini Reddet ");
                    }
                    if(request_type.equals("alinan")){
                        current_state = "request_received";
                        send_message_request_button.setText("Chat İsteğini Kabul Et");
                        decline_message_request_button.setVisibility(View.VISIBLE);
                        decline_message_request_button.setEnabled(true);

                        decline_message_request_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelChatRequest();
                            }
                        });
                    }
                }
                else{
                    ContactsRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiverId)){
                                current_state = "friends";
                                send_message_request_button.setText("Etkileşimi Kaldır");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!currentUserId.equals(receiverId)){  // Eğer tıkladığımız kullanıcı ile şuanki kullanıcı aynı değil ise
            send_message_request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    send_message_request_button.setEnabled(false); // önce bir butonun tıklanma aktifliği kapatılır

                    if(current_state.equals("new")){  // Kullanıcın şuanki durumu yeni durum ile eşitlenirse ?????

                        SendChatRequest();
                    }
                    if(current_state.equals("request_sent")){  // istek gönderildiyse
                        CancelChatRequest();
                    }
                    if(current_state.equals("request_received")){  // istek gönderildiyse
                        AcceptChatRequest();
                    }
                    if(current_state.equals("friends")){  // istek gönderildiyse
                        RemoveSpecificContact();
                    }





                }
            });
        }
        else{    // aynı ise

            send_message_request_button.setVisibility(View.INVISIBLE);

        }
    }

    private void RemoveSpecificContact() {
        ContactsRef.child(currentUserId).child(receiverId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverId).child(currentUserId) // şuanki kullanıcı ve istek yolladığı kullanıcı ıd
                                    .removeValue() // kaydedildiği alandan alıp sildik ve butonları değiştirdik
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                send_message_request_button.setEnabled(true);
                                                current_state = "new";
                                                send_message_request_button.setText("Send Message");

                                                decline_message_request_button.setVisibility(View.INVISIBLE);
                                                decline_message_request_button.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactsRef.child(currentUserId).child(receiverId)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ContactsRef.child(receiverId).child(currentUserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ChatRequestRef.child(currentUserId).child(receiverId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    ChatRequestRef.child(receiverId).child(currentUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    send_message_request_button.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    send_message_request_button.setText("Remove this Contact");

                                                                                    decline_message_request_button.setVisibility(View.INVISIBLE);
                                                                                    decline_message_request_button.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {  // chat isteğini iptal etmek istersek hem veri tabanından sileriz hemde uygulamdan
        ChatRequestRef.child(currentUserId).child(receiverId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ChatRequestRef.child(receiverId).child(currentUserId) // şuanki kullanıcı ve istek yolladığı kullanıcı ıd
                                    .removeValue() // kaydedildiği alandan alıp sildik ve butonları değiştirdik
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                send_message_request_button.setEnabled(true);
                                                current_state = "new";
                                                send_message_request_button.setText("Send Message");

                                                decline_message_request_button.setVisibility(View.INVISIBLE);
                                                decline_message_request_button.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {

        ChatRequestRef.child(currentUserId).child(receiverId).child("request_type").setValue("gönderilen")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    ChatRequestRef.child(receiverId).child(currentUserId).child("request_type").setValue("alinan")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        HashMap<String, String> chatNotificationMap = new HashMap<>();
                                        chatNotificationMap.put("from", currentUserId);
                                        chatNotificationMap.put("type", "request");

                                        NotificationRef.child(receiverId).push()
                                                .setValue(chatNotificationMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            send_message_request_button.setEnabled(true);
                                                            current_state = "request_sent";
                                                            send_message_request_button.setText("Cancel Chat Request");
                                                        }
                                                    }
                                                });

                                    }
                                }
                            });

                }
            }
        });

    }
}
