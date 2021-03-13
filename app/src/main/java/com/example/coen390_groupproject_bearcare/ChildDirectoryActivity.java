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
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.ChildAdapter;
import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.DialogFragmentsAndAdapters.InsertChildDialog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChildDirectoryActivity extends AppCompatActivity{


    // Our floating action button class object
    protected FloatingActionButton addChildButton;

    // Firestore Recycler List
    private RecyclerView firestoreChildrenDirectoryRecyclerView;

    // FireStore Reference
    private FirebaseFirestore fStore;

    // Reference to the collection
    private CollectionReference childrenRef;

    // Object of type childAdapter.
    private ChildAdapter adapter;

    String TAG = "debug_childDirectory";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_directory);

        // Because we are not adding our own toolbar it is sufficient enough to
        // get the support of our own actionBar, and set the UP button enabled.
        // Connections done in the manifest file.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialization
        fStore = FirebaseFirestore.getInstance();
        childrenRef = fStore.collection("Children");

        setUpUI();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Very important to tell it when to start listening and when to stop.
        Log.d(TAG, "adapter is starting");
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "adapter is stopping");
        adapter.stopListening();
    }

    private void setUpUI(){

        // Connections.
        addChildButton = findViewById(R.id.floatingActionButtonAddChild_childDirectory);

        // Onclick listener for our add child button
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "User accessing insertChildDialog");

                // Now we want to open a dialog fragment, requires two things
                // The layout which defines what we will see
                // and A class for it that expands from dialogFragment.
                InsertChildDialog dialog = new InsertChildDialog();

                // Now we want to show the fragment
                dialog.show(getSupportFragmentManager(),"Insert Child");

            }
        });

        // RecyclerView Set Up
        Log.d(TAG, "Recycler View is initializing and Query is starting");

        // Query
        Query query = childrenRef.orderBy("lastName", Query.Direction.ASCENDING);

        // Recycler Options. To get out query into the adapter.
        FirestoreRecyclerOptions<Child> options = new FirestoreRecyclerOptions.Builder<Child>()
                .setQuery(query, Child.class)
                .build();

        Log.d(TAG, "Adapter is initializing with outchild directory options");
        adapter = new ChildAdapter(options);

        // Connecting our class object of recycler view to the layout recycler view
        firestoreChildrenDirectoryRecyclerView   = findViewById(R.id.recyclerViewChildList_childDirectory);

        // Connect out class object recycler view to the adapter
        firestoreChildrenDirectoryRecyclerView.setHasFixedSize(true);
        firestoreChildrenDirectoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        firestoreChildrenDirectoryRecyclerView.setAdapter(adapter);

        // ItemTouchHelper to implement delete functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "Child Item is being deleted");
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(firestoreChildrenDirectoryRecyclerView);

        adapter.setOnItemClickListener(new ChildAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // Now we can create an intent and send
                // Get the document ID.
                String childId = documentSnapshot.getId();

                //get child name
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                String name = lastName + ", " + firstName;


                Log.d(TAG, "Child ID of item clicked is: " + childId);
                Log.d(TAG, "Child Name of item clicked is: " + name);

                //TODO intent to go to child profile activity with the childID.

                Intent intent = new Intent(getApplicationContext(), ChildProfileActivity.class);
                intent.putExtra("childId", childId);
                intent.putExtra("childName", name);

                Log.d("so", "Child ID of item clicked is: " + childId);
                Log.d("so", "Child Name of item clicked is: " + name);

                startActivity(intent);
            }
        });


        // end of setUpUI
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

                Log.d(TAG, "User settings clicked");
                // TODO settings activity for language and preferences.

                return true;

            case R.id.menuItem_logout:

                Log.d(TAG, "User is logging out");
                Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();

                // We sign out the firebase user and they are sent to the login activity (MainActivity.java)
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class ));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}