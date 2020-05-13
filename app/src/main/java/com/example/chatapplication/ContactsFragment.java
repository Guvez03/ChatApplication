package com.example.chatapplication;


import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView contacts_list;
    private DatabaseReference ContactsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        contacts_list = contactsView.findViewById(R.id.contacts_list);
        contacts_list.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth =  FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Profiles");

        return contactsView;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactsRef,Contacts.class)
                .build();


       final FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
               final String UserIDs = getRef(position).getKey();

                UsersRef.child(UserIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("userimageurl")){
                            String userImage=    dataSnapshot.child("userimageurl").getValue().toString();
                            String profileName=    dataSnapshot.child("username").getValue().toString();
                            String profileStatus=    dataSnapshot.child("userstatus").getValue().toString();

                            holder.username.setText(profileName);
                            holder.userstatus.setText(profileStatus);
                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                        }
                        else{
                            String profileName=    dataSnapshot.child("username").getValue().toString();
                            String profileStatus=    dataSnapshot.child("userstatus").getValue().toString();

                            holder.username.setText(profileName);
                            holder.userstatus.setText(profileStatus);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;

            }
        };
       contacts_list.setAdapter(adapter);
       adapter.startListening();

    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView username , userstatus;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profil_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);


        }
    }
}
