package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Model.Child;
import com.example.coen390_groupproject_bearcare.Model.Date;
import com.example.coen390_groupproject_bearcare.fragments.InsertChildDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChildDirectoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Child> children = new ArrayList<>();

    // Our floating action button class object
    protected FloatingActionButton addChildButton;

    String TAG = "debug_childDirectory";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_directory);

        // Because we are not adding our own toolbar it is sufficient enough to
        // get the support of our own actionBar, and set the UP button enabled.
        // Connections done in the manifest file.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpUI();





    }


    public void setUpUI(){

        // Connections
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
                // TO-DO settings activity for language and preferences.

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