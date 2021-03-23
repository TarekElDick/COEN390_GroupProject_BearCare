package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChildProfileActivity extends AppCompatActivity {

    private TextView textViewChildName, textViewParentName, textViewRelation, textViewParentPhoneNumber;
    private Button buttonTakeTemp;

    private String childId;
    private String childName;
    private String parentId;

    private FirebaseFirestore fStore;

    String TAG = "debug_childprofile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        // Back Button takes you back to child directory
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialization
        fStore = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        childId = intent.getStringExtra("childId");
        childName = intent.getStringExtra("childName");
        parentId = intent.getStringExtra("parentId");

        Log.d(TAG, "Child ID of item clicked is: " + childId);
        Log.d(TAG, "Child Name of item clicked is: " + childName);
        Log.d(TAG, "Parent ID of item clicked is: " + parentId);

        setUpUI();

    }

    private void setUpUI() {

        // Connections
        buttonTakeTemp = findViewById(R.id.buttonTakeTemp_childProfileActivity);
        textViewChildName = findViewById(R.id.textViewChildName_childProfile);
        textViewParentName = findViewById(R.id.textViewParentName_childProfile);
        textViewParentPhoneNumber = findViewById(R.id.textViewParentPhoneNumber_childProfile);


        buttonTakeTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go to takeTempActivity
                Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", childName);
                Log.d(TAG, "Child ID of item clicked is: " + childId);
                Log.d(TAG, "Child Name of item clicked is: " + childName);
                startActivity(intent);

            }
        });

        // Search for the document with the parentsID and get the parents info
        DocumentReference userRef = fStore.collection("Users").document(parentId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String firstName = document.getString("firstName");
                        String lastName = document.getString("lastName");
                        String phoneNumber = document.getString("phoneNumber");
                        String name = firstName + " " + lastName;

                        textViewParentName.setText(name);
                        textViewParentPhoneNumber.setText(phoneNumber);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        textViewChildName.setText(childName);

        // end of setup.
    }
}