package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.InsertChildDialog;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.InsertNotificationDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChildProfileActivity extends AppCompatActivity {

    private TextView textViewChildName, textViewParentName, textViewRelation, textViewParentPhoneNumber, textViewChildBirthday;
    private Button buttonTakeTemp, buttonAddNotification, buttonTempHistory, buttonMedRecord;

    private String childId, childName,  parentId, birthday;

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
        birthday = intent.getStringExtra("birthday");

        Log.d(TAG, "Child ID of item clicked is: " + childId);
        Log.d(TAG, "Child Name of item clicked is: " + childName);
        Log.d(TAG, "Parent ID of item clicked is: " + parentId);
        Log.d(TAG, "Birthday of child item clicked is: " + birthday);

        setUpUI();

    }

    private void setUpUI() {

        // Connections
        buttonTakeTemp = findViewById(R.id.buttonTakeTemp_childProfileActivity);
        buttonTempHistory = findViewById(R.id.button2);
        buttonMedRecord = findViewById(R.id.buttonMedicalRecord);
        textViewChildName = findViewById(R.id.textViewChildName_childProfile);
        textViewParentName = findViewById(R.id.textViewParentName_childProfile);
        textViewParentPhoneNumber = findViewById(R.id.textViewParentPhoneNumber_childProfile);
        buttonAddNotification = findViewById(R.id.buttonAddANotification_childProfile);
        textViewChildBirthday  = findViewById(R.id.textViewChildBirthday_childProfile);

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

        buttonTempHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TemperatureHistoryActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", childName);
                Log.d(TAG, "TempHistory clicked");
                startActivity(intent);
            }
        });

        buttonAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "User accessing insertNotificationDialog");

                // Now we want to open a dialog fragment, requires two things
                // The layout which defines what we will see
                // and A class for it that expands from dialogFragment.
                InsertNotificationDialog dialog = new InsertNotificationDialog();


                // Use a bundle to send data to the fragment
                Bundle notificationBundle = new Bundle();
                notificationBundle.putString("childId", childId);
                notificationBundle.putString("childName", childName);
                dialog.setArguments(notificationBundle);

                // Now we want to show the fragment
                dialog.show(getSupportFragmentManager(),"Create Notification");

            }
        });


        buttonMedRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MedicalRecordsActivity.class);
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

        textViewChildBirthday.setText(birthday);
        textViewChildName.setText(childName);

        // end of setup.
    }
}