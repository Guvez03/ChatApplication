package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText input_group_message;
    private ScrollView mScrollView;
    private TextView displayTextMessage;

    private String groupName;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference UserRef,GroupNameRef,GroupMessageKeyRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String currentGroupName , currentUserID ,currentUserName;
    private String currentDate,currentTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        mToolbar = findViewById(R.id.group_chat_bar_layout);
        input_group_message = findViewById(R.id.input_group_message);
        mScrollView = findViewById(R.id.scroll_view);
        displayTextMessage = findViewById(R.id.group_chat_text);
        sendMessageButton = findViewById(R.id.send_message);



        groupName= getIntent().getExtras().get("groupName").toString();

        mToolbar.setTitle(groupName);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        currentUserName = mAuth.getCurrentUser().toString();// userin id sini getirdik
        // Veritabanı oluşturduk ve bunun referansını tanımladık.
        firebaseDatabase = FirebaseDatabase.getInstance();
        UserRef = firebaseDatabase.getReference().child("Profiles"); // veritabanından profiles bölümünü almak için
        GroupNameRef = firebaseDatabase.getReference().child("Groups").child(groupName);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 String mesaj_gonder = input_group_message.getText().toString();

                FirebaseUser user = mAuth.getCurrentUser(); // o anki kullanıcıyı aldık kullanıcı karışıklığı olursa id ile işlem yap
                String email = user.getEmail();
                 // String kullanici_adi = user.get    kullanıcı adını al !!!!!!!!!!!!!!!
                //databaseReference.child("Groups").child("Chats").child("Username").setValue(user);

                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                currentDate = currentDateFormat.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm, a");
                currentTime = currentTimeFormat.format(calForTime.getTime());


               /* GroupNameRef.child("Groups").child("Chats").child("Usermessage").setValue(mesaj_gonder);
                GroupNameRef.child("Groups").child("Chats").child("Useremail").setValue(email);
                GroupNameRef.child("Groups").child("Chats").child("UserMassageTıme").setValue(ServerValue.TIMESTAMP);*/

                String message_key = GroupNameRef.push().getKey();

                HashMap<String,Object> groupMessageKey = new HashMap<>();
                GroupNameRef.updateChildren(groupMessageKey);

                GroupMessageKeyRef = GroupNameRef.child(message_key);

                HashMap<String,Object> messageInfoMap = new HashMap<>();
                messageInfoMap.put("name",currentUserName);
                messageInfoMap.put("message",mesaj_gonder);
                messageInfoMap.put("date",currentDate);
                messageInfoMap.put("time",currentTime);
                GroupMessageKeyRef.updateChildren(messageInfoMap);

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();



        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){   // veri var ise görüntüle methoduna gider
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){   // veri var ise görüntüle methoduna gider
                    DisplayMessages(dataSnapshot);
                }

                }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getData() {

      //  GroupNameRef = firebaseDatabase.getReference("Groups");



   /*     Query query = GroupNameRef.orderByChild("UserMassageTıme");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                arrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    // HashMap<String,String> hashMap =  (HashMap<String, String>) ds.getValue();
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();


                    String user_email = hashMap.get("Useremail");
                    String user_message = hashMap.get("Usermessage");

                    arrayList.add(user_email + " " + user_message);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG);


            }});
*/


    }

    public void DisplayMessages(DataSnapshot dataSnapshot){
       /* Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){

            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName + " " + chatMessage + " \n  " + chatTime + " " + chatDate);

        }*/

        // VERİLERİ İTERATOR İLE ALAMADIM HASHMAP KULLANDIM BURASI ÇOKOMELLİİİ
        // -------------------------------------------
/*


        for(DataSnapshot ds : dataSnapshot.getChildren()){


            HashMap<String,String>  hashMap =  (HashMap<String, String>) dataSnapshot.getValue();

            String user_date = hashMap.get("date");
            String user_message = hashMap.get("message");
            String user_name = hashMap.get("name");
            String user_time = hashMap.get("time");




            displayTextMessage.append(user_name + " \n" + user_message + " \n" + user_time + " " + user_date);

            input_group_message.setText(" ");

            // -------------------------------------------

        }
*/

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String user_date = (String)((DataSnapshot)iterator.next()).getValue();
            String user_message  = (String)((DataSnapshot)iterator.next()).getValue();
            String user_name = (String)((DataSnapshot)iterator.next()).getValue();
            String user_time = (String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(user_name + " \n" + user_message + " \n" + user_time + " " + user_date);

            input_group_message.setText(" ");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);


        }

    }

}
