package com.example.chatapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View group_fragment_view; // bu değişkeni tekrar kullanmak için bu kullanımı seçtik
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private DatabaseReference databaseReference;
    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        group_fragment_view =  inflater.inflate(R.layout.fragment_groups, container, false);

        list_view = group_fragment_view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);
        // adapterı bağladık
        list_view.setAdapter(arrayAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        // veritabanından grup bölümünde bulunan verileri çektik

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();

                Iterator iterator = dataSnapshot.getChildren().iterator(); //tüm verileri alıp iteratora attık

                while(iterator.hasNext()){ // verileri teker teker hashsete attık
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                arrayList.clear();
                arrayList.addAll(set); // arrayliste ekledik

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Listedeki gruplardan herhangi birine tıklanıldığında
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String currentGroupName = adapterView.getItemAtPosition(i).toString();// tıkladığımız grubun ismini aldık indis yardımıyla

                Intent group_intent = new Intent(getContext(),GroupChatActivity.class);
                group_intent.putExtra("groupName",currentGroupName);
                startActivity(group_intent);

            }
        });

        return  group_fragment_view;

    }


}
