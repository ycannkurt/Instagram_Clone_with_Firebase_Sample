package com.can.example.instagramclonefirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class feedActivity extends AppCompatActivity {

    ListView listView;
    PostClass adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    ArrayList<String> useremailFromFB;
    ArrayList<String> userimageFromFB;
    ArrayList<String> usercommentFromFB;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_post,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_post) {
            Intent intent = new Intent(getApplicationContext(), uploadActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        listView = findViewById(R.id.listView);

        useremailFromFB = new ArrayList<String>();
        usercommentFromFB = new ArrayList<String>();
        userimageFromFB = new ArrayList<String>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        adapter = new PostClass(useremailFromFB,usercommentFromFB,userimageFromFB,this);

        listView.setAdapter(adapter);


        //FIRESTORE

        getDataFromFirestore();

        //REALTIME DATABASE
        //getDataFromFirebase();
    }

    public void getDataFromFirestore() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Posts");

        collectionReference
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                            Map<String, Object> data = snapshot.getData();

                            String comment = (String) data.get("comment");
                            String useremail = (String) data.get("useremail");
                            String downloadurl = (String) data.get("downloadurl");

                            useremailFromFB.add(useremail);
                            userimageFromFB.add(downloadurl);
                            usercommentFromFB.add(comment);

                            adapter.notifyDataSetChanged();

                        }
                    }
                });


    }

    public void getDataFromFirebase() {

        DatabaseReference newReference = firebaseDatabase.getReference("Posts");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //System.out.println("FBV children: " + dataSnapshot.getChildren() );
                //System.out.println("FBV key: " + dataSnapshot.getKey() );
                //System.out.println("FBV value: " + dataSnapshot.getValue() );
                //System.out.println("FBV priority: " + dataSnapshot.getPriority() );

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    //System.out.println("FBV ds value: " + ds.getValue());

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                    //System.out.println("FBV useremail:" + hashMap.get("useremail"));

                    useremailFromFB.add(hashMap.get("useremail"));
                    usercommentFromFB.add(hashMap.get("comment"));
                    userimageFromFB.add(hashMap.get("downloadurl"));
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
