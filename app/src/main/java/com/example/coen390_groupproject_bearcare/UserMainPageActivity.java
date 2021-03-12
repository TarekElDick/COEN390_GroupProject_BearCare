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
    private RecyclerView firestoreChildrenRecyclerView;
    private TextView displayName, accessChildDirectory;
    private Button buttonFillQuestionnaire;
    private boolean isEmployee;
    private String userId;
    String TAG = "debug_dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing the user
        user = mAuth.getCurrentUser();

        fStore = FirebaseFirestore.getInstance();

        childrenRef = fStore.collection("Children");

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

            // TO_D0 get the users information from fireStore and check if they are an employee or not as well.
            // 1) get children of users if parent
            // 2) get temperature history.


        } else {
            Log.d(TAG, "User is signed out");
            startActivity(new Intent(getApplicationContext(),MainActivity.class ));
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "adapter is stopping");
        childAdapter.stopListening();
    }

    public void setUpUI() {

        // Connections
        displayName = findViewById(R.id.textViewUserName_dashboard);
        accessChildDirectory = findViewById(R.id.textViewAccessChildDirectory_dashboard);
        buttonFillQuestionnaire = findViewById(R.id.buttonFillDailyQuestionnaire_dashboard);

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

        // RecyclerView for children setup
        Log.d(TAG, "Recycler View is initializing and Child Query is starting");
        Log.d(TAG, "User id is: " + userId);

        // check if user is employee or not
        DocumentReference docRef = fStore.collection("Users").document(userId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                isEmployee = user.isEmployee();
                Log.d(TAG, "User is employee: " + isEmployee);

                // TODO this takes to long time to be invoked , set everything to invisible when the activity starts.
                if(isEmployee){
                    accessChildDirectory.setVisibility(View.VISIBLE);
                    buttonFillQuestionnaire.setVisibility(View.INVISIBLE);

                    // TODO set last received and time to invisible

                    Log.d(TAG, "Making sure if employee " + isEmployee);
                    Log.d(TAG, "Query for employee is starting");
                }else{
                    accessChildDirectory.setVisibility(View.INVISIBLE);
                    buttonFillQuestionnaire.setVisibility(View.VISIBLE);
                    // TODO disable sensor icon,
                }
            }
        });

        // TODO Create a logical or queries , use the document online to figure it out,
        // TODO the next line always get false , because it takes long to check if the user is employee
        // TODO 1) either send it Boolean isEmployee with an intent from sign up and login, or make logical OR queries.

        // Query if user is employee
        Query childQuery= childrenRef.whereEqualTo("employeeId", userId)
                .orderBy("firstName", Query.Direction.ASCENDING);

        // Query if user is parent
//        Query childQueryParent = childrenRef.whereEqualTo("parentId", userId)
//                .orderBy("firstName", Query.Direction.ASCENDING);


        // Recycler Options. To get out query into the adapter.
        FirestoreRecyclerOptions<Child> options = new FirestoreRecyclerOptions.Builder<Child>()
                .setQuery(childQuery, Child.class)
                .build();

        childAdapter = new ChildAdapter(options);

        // Connecting our class object of recycler view to the layout recycler view
        firestoreChildrenRecyclerView   = findViewById(R.id.recyclerViewChildren_dashboard);

        // Connect out class object recycler view to the adapter
        firestoreChildrenRecyclerView.setHasFixedSize(true);
        firestoreChildrenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreChildrenRecyclerView.setAdapter(childAdapter);

        // ItemTouchHelper to implement delete functionality
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

        //On click fot the item , not the buttons!
        childAdapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Now we can create an intent and send
                // Get the document ID.
                String childId = documentSnapshot.getId();
                Log.d(TAG, "Child ID of item clicked is: " + childId);

                //TODO intent to go to child profile activity with the childID.
                Intent intent = new Intent(getApplicationContext(), ChildProfileActivity.class);
                intent.putExtra("childId", childId);
                startActivity(intent);

            }
        });

        //end of setUpUI function
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