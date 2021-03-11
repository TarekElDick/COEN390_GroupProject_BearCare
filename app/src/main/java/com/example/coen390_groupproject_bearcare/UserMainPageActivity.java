package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserMainPageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private TextView displayName, accessChildDirectory;


    String TAG = "debug_dashboard";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);


        // SetUpUI function
        setUpUI();

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Initializing the user
        user = mAuth.getCurrentUser();

        // end of onCreate()
    }



    @Override
    protected void onStart() {
        super.onStart();

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

        // parents cant see the child directory.

    }

    public void setUpUI() {

        // Connections
        displayName = findViewById(R.id.textViewUserName_dashboard);
        accessChildDirectory = findViewById(R.id.textViewAccessChildDirectory_dashboard);

        // onClickListeners
        accessChildDirectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "User accessing child directory");
                startActivity(new Intent(getApplicationContext(),ChildDirectoryActivity.class ));
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

                // TO-DO settings activity for language and preferences.

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