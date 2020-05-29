package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public Toolbar mToolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccessorAdapter tabAccessorAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatApp");

        viewPager = findViewById(R.id.main_tabs_pager);
        tabLayout=findViewById(R.id.main_tabs);
        tabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAccessorAdapter);

        tabLayout.setupWithViewPager(viewPager);

        mAuth = FirebaseAuth.getInstance(); // firebase sınıfından yeni bir sınıf oluşturduk Verileri getirdik
        databaseReference = FirebaseDatabase.getInstance().getReference();

        updateUserStatus("online");
    }
    @Override
    protected void onStop()
    {
        super.onStop();

       FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.signOut){
            updateUserStatus("offline");
            mAuth.signOut();
            Intent logout = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(logout);

        }
        if (item.getItemId() == R.id.arkadas_bul){

            Intent profil_intent = new Intent(MainActivity.this,FindFriendsActivity.class);
            startActivity(profil_intent);

        }
        if (item.getItemId() == R.id.user_profil){

            Intent profil_intent = new Intent(MainActivity.this,Profil.class);
            startActivity(profil_intent);
        }
        if (item.getItemId() == R.id.grup_olustur){

            // Grup ekleye tıklandığında bir alertdiyalog çıkacak buradan grup ismi girilecek.
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
            builder.setTitle("Grup ismi giriniz !");

            final EditText groupNameField = new EditText(MainActivity.this); // edittext oluşturup alertdialoga ekleyeceğiz
            groupNameField.setHint("Grup İsmi");
            builder.setView(groupNameField); // ekledik

            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    String groupName = groupNameField.getText().toString();

                    if(TextUtils.isEmpty(groupName)){
                        Toast.makeText(MainActivity.this,"Lütfen grup ismi giriniz !",Toast.LENGTH_LONG).show();
                    }
                    else{
                        CreateNewGroup(groupName);
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.cancel();

                }
            });

            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void CreateNewGroup(final String groupName) {

        // VEritabanına groups diye bir bölüm daha oluşturduk ve grup ismi eklendiğinde burada gözükmesini sağlayacağız
        //veritabanına ekleme işlemi yapıldı
        databaseReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,groupName,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        databaseReference.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }


}
