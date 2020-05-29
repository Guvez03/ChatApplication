package com.example.chatapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
public class RequestFragment extends Fragment {
    private View RequestFragmentView;

    private RecyclerView chat_request_list;
    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public RequestFragment() {
// Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        RequestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Profiles");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");


        chat_request_list = RequestFragmentView.findViewById(R.id.chat_request_list);
        chat_request_list.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequestsRef.child(currentUserID), Contacts.class)
                        .build();


FirebaseRecyclerAdapter<Contacts, RequestsViewHolder> adapter =
    new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Contacts model) {
            holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);


            final String list_user_id = getRef(position).getKey();

            DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

            getTypeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String type = dataSnapshot.getValue().toString();

                        if (type.equals("alinan")) {
                            UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild("userimageurl")) {
                                        final String requestProfileImage = dataSnapshot.child("userimageurl").getValue().toString();
                                        Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                    }

                                    final String requestUserName = dataSnapshot.child("username").getValue().toString();
                                    final String requestUserStatus = dataSnapshot.child("userstatus").getValue().toString();

                                    holder.username.setText(requestUserName);
                                    holder.userstatus.setText("Seninle etkileşim kurmak istiyor!");


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CharSequence options[] = new CharSequence[]
                                                    {
                                                            "Kabul Et",
                                                            "Reddet"
                                                    };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle(requestUserName + "  Chat Request");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    ContactsRef.child(currentUserID).child(list_user_id).child("Contacts")
                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ContactsRef.child(list_user_id).child(currentUserID).child("Contacts")
                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
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
                                    });
        }
                            if (i == 1) {
                                ChatRequestsRef.child(currentUserID).child(list_user_id)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else if (type.equals("gönderilen")) {
                Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                request_sent_btn.setText("Req Sent");

                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("userimageurl")) {
                            final String requestProfileImage = dataSnapshot.child("userimageurl").getValue().toString();

                            Picasso.get().load(requestProfileImage).into(holder.profileImage);
                        }

                        final String requestUserName = dataSnapshot.child("username").getValue().toString();
                        final String requestUserStatus = dataSnapshot.child("userstatus").getValue().toString();

                        holder.username.setText(requestUserName);
                        holder.userstatus.setText(requestUserName + "istek gönderildi");


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "İsteği iptal Et"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Zaten İstek Gönderildi");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
                                ChatRequestsRef.child(currentUserID).child(list_user_id)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "İstek Reddedildi.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                                    }
                                });
                                builder.show();
                            }
                        });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @NonNull
                @Override
                public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                    RequestsViewHolder holder = new RequestsViewHolder(view);
                    return holder;
                }
            };

    chat_request_list.setAdapter(adapter);
    adapter.startListening();
}


    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        TextView username, userstatus;
        CircleImageView profileImage;
        Button AcceptButton, CancelButton;


        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.user_profil_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
