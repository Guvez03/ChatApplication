package com.example.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profil extends AppCompatActivity
{
    private Button kaydet_buton;
    private EditText username, profile_durum;
    private CircleImageView profile_image;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;

    private Toolbar settings_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("images/");


        InitializeFields();


        username.setVisibility(View.VISIBLE);


        kaydet_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                UpdateSettings();
            }
        });


        RetrieveUserInfo();


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }



    private void InitializeFields()
    {
        kaydet_buton = (Button) findViewById(R.id.kaydet_buton);
        username = (EditText) findViewById(R.id.username);
        profile_durum = (EditText) findViewById(R.id.profile_durum);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        loadingBar = new ProgressDialog(this);

        settings_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(settings_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("PROFİL");
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profil Resmini Ayarla");
                loadingBar.setMessage("Lütfen bekleyin profil resminiz güncelleniyor");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                final Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                RootRef.child("Profiles").child(currentUserID).child("userimageurl").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(Profil.this, "Profil resmi başarıyla eklendi.",
                                                            Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(Profil.this, "Hata Oluştu" + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });

            }
        }
    }


    private void UpdateSettings()
    {
        String setUserName = username.getText().toString();
        String setStatus = profile_durum.getText().toString();

        if (TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Lütfen önce kullanıcı adınızı yazınız", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus))
        {
            Toast.makeText(this, "Lütfen durumunuzu boş bırakmayınız.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("useruid", currentUserID);
            profileMap.put("username", setUserName);
            profileMap.put("userstatus", setStatus);
            RootRef.child("Profiles").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(Profil.this, "Profil başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(Profil.this, "Hata: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    private void RetrieveUserInfo()
    {
        RootRef.child("Profiles").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username") && (dataSnapshot.hasChild("userimageurl"))))
                        {
                            String retrieveUserName = dataSnapshot.child("username").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("userstatus").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("userimageurl").getValue().toString();

                            username.setText(retrieveUserName);
                            profile_durum.setText(retrievesStatus);
                            Picasso.get().load(retrieveProfileImage).into(profile_image);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username")))
                        {
                            String retrieveUserName = dataSnapshot.child("username").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("userstatus").getValue().toString();

                            username.setText(retrieveUserName);
                            profile_durum.setText(retrievesStatus);
                        }
                        else
                        {
                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(Profil.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(Profil.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}

