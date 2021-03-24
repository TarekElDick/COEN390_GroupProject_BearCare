package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.ChildAdapter;
import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserMainPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private CollectionReference childrenRef;
    private ChildAdapter childAdapter;
    private TextView displayName, questionnaireLastReceived, questionnaireTimestamp, corner;
    private boolean isEmployee;
    private String userId;
    String TAG = "debug_dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        // get the intent
        isEmployee = getIntent().getBooleanExtra("isEmployeeD",false);
        Log.d(TAG, "Making sure if employee in onCreate " + isEmployee);

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing the user
        user = mAuth.getCurrentUser();

        // Initializing firestore
        fStore = FirebaseFirestore.getInstance();

        // Initializing the reference to the children database
        childrenRef = fStore.collection("Children");

        // get the user ID of the user currently logged in.
        userId = user.getUid();

        // SetUpUI function
        setUpUI();


        // end of onCreate()
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "adapter is starting");
        childAdapter.startListening();

        if (user != null){
            // user is signed in
            Log.d(TAG, "User is signed in");
            displayName.setText(user.getDisplayName());

        } else {
            Log.d(TAG, "User is signed out");
            startActivity(new Intent(getApplicationContext(),MainActivity.class ));
        }

        // end of on start
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "adapter is stopping");
        childAdapter.stopListening();
    }

    public void setUpUI() {

        // Connections

        corner = findViewById(R.id.textViewParentsCorner_dashboard);
        displayName = findViewById(R.id.textViewUserName_dashboard);
        TextView accessChildDirectory = findViewById(R.id.textViewAccessChildDirectory_dashboard);
        Button buttonFillQuestionnaire = findViewById(R.id.buttonFillDailyQuestionnaire_dashboard);
        questionnaireLastReceived = findViewById(R.id.textViewLastReceived_dashboard);
        questionnaireTimestamp = findViewById(R.id.textViewTimestamp_dashboard);

        // initially make everything invisible
        accessChildDirectory.setVisibility(View.INVISIBLE);
        buttonFillQuestionnaire.setVisibility(View.INVISIBLE);
        questionnaireTimestamp.setVisibility(View.INVISIBLE);
        questionnaireLastReceived.setVisibility(View.INVISIBLE);
        // onClickListeners
        accessChildDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "User accessing child directory");
                startActivity(new Intent(getApplicationContext(),ChildDirectoryActivity.class ));
            }
        });

        buttonFillQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO fill daily questionnaire activity , we can always get the user id we don't have to pass anything with the intent.
            }
        });

        runRecyclerView();
        // The following code is to double check if the user is an employee or not.
        String userId = user.getUid();
        // fix fStore reference
        DocumentReference docRef = fStore.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                isEmployee = user.isEmployee();
                runRecyclerView();
                // RecyclerView for children setup

                Log.d(TAG, "User id is: " + userId);

                if(isEmployee){
                    String cornerText = "Employee Corner";
                    corner.setText(cornerText);
                    accessChildDirectory.setVisibility(View.VISIBLE);
                    buttonFillQuestionnaire.setVisibility(View.INVISIBLE);
                    questionnaireTimestamp.setVisibility(View.INVISIBLE);
                    questionnaireLastReceived.setVisibility(View.INVISIBLE);
                }else{
                    String cornerText = "Parent Corner";
                    corner.setText(cornerText);
                    accessChildDirectory.setVisibility(View.INVISIBLE);
                    buttonFillQuestionnaire.setVisibility(View.VISIBLE);
                    questionnaireTimestamp.setVisibility(View.VISIBLE);
                    questionnaireLastReceived.setVisibility(View.VISIBLE);
                    // TODO disable sensor icon, on the item probably within adapter?
                }
            }
        });

        //end of setUpUI function
    }

    public void runRecyclerView() {

        Log.d(TAG, "Recycler View is initializing");
        Log.d(TAG, "Making sure if employee in runRecyclerView " + isEmployee);

        Query childQuery;
        if(isEmployee) {
            Log.d(TAG, "Query for employee is starting");
            // Query if user is employee
            childQuery = childrenRef.whereEqualTo("employeeId", userId)
                    .orderBy("firstName", Query.Direction.ASCENDING);

            // Recycler Options. To get out query into the adapter.
            FirestoreRecyclerOptions<Child> options = new FirestoreRecyclerOptions.Builder<Child>()
                    .setQuery(childQuery, Child.class)
                    .build();

            childAdapter = new ChildAdapter(options);
        } else {
            Log.d(TAG, "Query for parent is starting");
            // Query if user is parent
            childQuery = childrenRef.whereEqualTo("parentId", userId)
                    .orderBy("firstName", Query.Direction.ASCENDING);
            // Recycler Options. To get out query into the adapter.
            FirestoreRecyclerOptions<Child> options = new FirestoreRecyclerOptions.Builder<Child>()
                    .setQuery(childQuery, Child.class)
                    .build();
            childAdapter = new ChildAdapter(options);
        }
        onStart();

        // Connecting our class object of recycler view to the layout recycler view
        RecyclerView firestoreChildrenRecyclerView = findViewById(R.id.recyclerViewChildren_dashboard);

        // Connect out class object recycler view to the adapter
        firestoreChildrenRecyclerView.setHasFixedSize(true);
        firestoreChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreChildrenRecyclerView.setAdapter(childAdapter);

        // ItemTouchHelper to implement delete functionality
        if(isEmployee) {
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    Log.d(TAG, "Child Item is being deleted");
                    childAdapter.deleteItem(viewHolder.getAdapterPosition());
                }
            }).attachToRecyclerView(firestoreChildrenRecyclerView);
        }
        //On click fot the item , not the buttons!
        childAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Now we can create an intent and send
                // Get the document ID.
                String childId = documentSnapshot.getId();
                Log.d(TAG, "Child ID of item clicked is: " + childId);

                //get child name
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String parentId = documentSnapshot.getString("parentId");
                String name = firstName + " " + lastName;


                Log.d(TAG, "Child ID of item clicked is: " + childId);
                Log.d(TAG, "Child Name of item clicked is: " + name);


                Intent intent = new Intent(getApplicationContext(), ChildProfileActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", name);
                intent.putExtra("parentId", parentId);
                startActivity(intent);
            }
            @Override
            public void onTakeTempButtonClick(DocumentSnapshot documentSnapshot, int position) {
                if(isEmployee) {
                    // Code for when take temp button is clicked
                    // Get the document ID.
                    String childId = documentSnapshot.getId();
                    //get child name
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String childName = firstName + " " + lastName;
                    // Go to takeTempActivity
                    Intent intent = new Intent(getApplicationContext(), TemperatureActivity.class);
                    intent.putExtra("childId", childId);
                    intent.putExtra("childName", childName);
                    Log.d(TAG, "Child ID of button clicked is: " + childId);
                    Log.d(TAG, "Child Name of button clicked is: " + childName);
                    startActivity(intent);
                } else{
                    Log.d(TAG, "Not Employee");
                    Toast.makeText(UserMainPageActivity.this, "Only employees can take temperature", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    // Created our menu layout file in the resource directory (res/menu/menu_DASHBOARD), and we connected it to this activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    // Now we need to specify what happens once our menu items are clicked.
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuItem_settings:

                // TODO settings activity for language and preferences.
                return true;

            case R.id.menuItem_logout:

                Log.d(TAG, "User is logging out");
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();

                // We sign out the firebase user and they are sent to the login activity (MainActivity.java)
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class ));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // end of activity
}