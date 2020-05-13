package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
  private  EditText register_email;
  private  EditText register_password;
  private  EditText register_tel;
  private FirebaseAuth mAuth;
  private ProgressDialog LoadingBar;
  private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_tel= findViewById(R.id.register_tel);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        LoadingBar = new ProgressDialog(this); // bir uyarı mesajı olluşturmak için kullanıyoruz
    }

    public void register_button(View view){

        String email= register_email.getText().toString();
        String password = register_password.getText().toString();

        if(TextUtils.isEmpty(email)){

            Toast.makeText(this,"Lütfen email giriniz",Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(password)){

            Toast.makeText(this,"Lütfen şifrenizi giriniz",Toast.LENGTH_LONG).show();
        }
        else{

            LoadingBar.setTitle("Hesabınız oluşturuluyor");
            LoadingBar.setMessage("Lütfen Bekleyin,Hesabınızı oluşturuyoruz");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();

            mAuth.createUserWithEmailAndPassword(register_email.getText().toString(),register_password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override

                        // onComplete methodu başarı veya başarısızlıklık durumunda tek sefer çalışır.

                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                // task dediğimiz o andaki görevdir.
                                // eğer giriş başarılı ise kullanıcının email bilgisini görebiliriz.
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                String userId = mAuth.getCurrentUser().getUid(); // kayıt olduğumuz verinin idsini aldık
                                databaseReference.child("Users").child(userId).setValue(""); // ID yi veritabanına kaydettik

                                databaseReference.child("Users").child(userId).child("device_token")
                                        .setValue(deviceToken);

                                FirebaseUser user = mAuth.getCurrentUser();
                                String usermail = user.getEmail().toString();
                                System.out.println("user e mail " + usermail);

                                Toast.makeText(RegisterActivity.this,"Kayıt Olma Başarılı",Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoadingBar.dismiss(); // mesajı kapattık
                            }
                            else{
                                String hata_message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Kayıt Yapılamadı "+hata_message,Toast.LENGTH_SHORT).show();
                                LoadingBar.dismiss();
                            }
                        }
                    });

        }

    }

    public void register_already(View view){

        Intent already_intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(already_intent);


    }

}
