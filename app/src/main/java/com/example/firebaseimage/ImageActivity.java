package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private List<DB_Model> userList;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Gating Touch into XML...
        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); //Recycler View Will Work More Efficiently.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    DB_Model db_model = dataSnapshot1.getValue(DB_Model.class);
                    userList.add(db_model);
                }
                progressBar.setVisibility(View.INVISIBLE);
                MyAdapter myAdapter = new MyAdapter(ImageActivity.this,userList);
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImageActivity.this, "Error :"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}