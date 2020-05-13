package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {
    public EditText email;
    public EditText password;
    public Button sign__up_button,sign__in_button;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private FirebaseUser user;
    private DatabaseReference databaseReference,UsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= findViewById(R.id.email);
        password = findViewById(R.id.password);
        sign__in_button = findViewById(R.id.sign__up_button);
        sign__up_button = findViewById(R.id.sign__up_button);
        LoadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance(); // firebase sınıfından yeni bir sınıf oluşturduk Verileri getirdik
        user = mAuth.getCurrentUser(); // şuanki kullanıcı

        databaseReference = FirebaseDatabase.getInstance().getReference(); // veritabanında bu referans işe işlemler yapabiliriz
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Profiles"); // veritabanında bu referans işe işlemler yapabiliriz


        if(user != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    public void signin (View view) {

        LoadingBar.setTitle("Giriş Yapılıyor");
        LoadingBar.setMessage("Lütfen Bekleyin");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();

        mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String currentUserId = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    UsersRef.child(currentUserId).child("device_token")
                            .setValue(deviceToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(LoginActivity.this, "Giriş Başarılı...", Toast.LENGTH_SHORT).show();
                                        LoadingBar.dismiss();
                                    }
                                }
                            });

                    String userId = mAuth.getCurrentUser().getUid(); // kayıt olduğumuz verinin idsini aldık
                    databaseReference.child("Profil").child(userId).setValue(""); // ID yi veritabanına kaydettik

                    LoadingBar.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this,"E-mail veya Password hatalı lütfen kontrol ediniz!",Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                }
            }
        });

       // Intent ıntent = new Intent(LoginActivity.this, MainActivity.class);
       // startActivity(ıntent);
    }

    public void signup (View view) {
        // Kullanıcı oluşturmamız gerek
        // toString metodu bir objenin içeriğini string olarak almamızı sağlar

       /* mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override

                    // onComplete methodu başarı veya başarısızlıklık durumunda tek sefer çalışır.

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // task dediğimiz o andaki görevdir.
                            // eğer giriş başarılı ise kullanıcının email bilgisini görebiliriz.

                            FirebaseUser user = mAuth.getCurrentUser();
                            String usermail = user.getEmail().toString();
                            System.out.println("user e mail " + usermail);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Başarısız",Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

       Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
       startActivity(register);

    }

  /*  @Override
    public void onStart() {

        super.onStart();

        if (user != null){

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }*/

}
