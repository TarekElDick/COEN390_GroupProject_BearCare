package com.example.coen390_groupproject_bearcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coen390_groupproject_bearcare.Bluetooth.MyBluetoothService;
import com.example.coen390_groupproject_bearcare.Model.User;
import com.example.coen390_groupproject_bearcare.Network.NetworkService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    // Our class objects
    private EditText editTextLoginEmailAddress, editTextLoginPassword;
    private Button buttonLogin;
    private TextView textViewMessage, textViewCreateNewAccount;
    private ProgressBar progressBarLogin;

    // Firebase Shared Instances
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private boolean checkConnect;

    String TAG = "debug_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // A function setUpUI to connect our class objects to our layout widgets, and onClickListeners
        setUpUI();

        // Initializing Firebase Authentication.
        mAuth = FirebaseAuth.getInstance();

        // Get the user currently signed in
        user = mAuth.getCurrentUser();

        // Initializing firestore
        fStore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkConnect = NetworkService.checkNetwork(this);
        if(!checkConnect)
            Toast.makeText(this, "BearCare App Requires an Internet Connection Please Connect", Toast.LENGTH_LONG).show();

        Log.d(TAG, "Start: OnStart(): Check if returning user");
        // Check if user is already logged in or not, if they are a returning user
        if(user != null ){

            Log.d(TAG, "User is signed in send them to dashboard");
            // Send them to their dashboard activity.

            // We are going to the database to check of the user is a employee or not.
            DocumentReference docRef = fStore.collection("Users").document(user.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);

                    boolean isEmployee = user.isEmployee();

                    Log.d(TAG, "user email:" + user.getEmailAddress());
                    // We check if its the director
                    Intent intent;
                    if(user.getEmailAddress().equals("tarekdeek@bearcare.ca")){
                        Log.d(TAG, "User is a director");
                        //Add Intent for director
                        intent = new Intent(getApplicationContext(), DirectorDashboardActivity.class);
                        Toast.makeText(MainActivity.this, getString(R.string.welcome_back_director), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "isEmployee ? :" + isEmployee);
                        intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                        intent.putExtra("isEmployeeD", isEmployee);
                        Toast.makeText(MainActivity.this, getString(R.string.welcome_back), Toast.LENGTH_LONG).show();
                    }
                    startActivity(intent);
                }
            });
        }else {
            Log.d(TAG, "End: OnStart(): User is not signed in");
        }

        // End of onStart
    }


    @Override
    protected void onDestroy() {                // This will be used to close the bluetooth socket when the app is destroyed
        super.onDestroy();
        MyBluetoothService mine = new MyBluetoothService();
        MyBluetoothService.close();
    }

    private void setUpUI() {

        // Connecting class objects to our layout widgets.
        editTextLoginEmailAddress = findViewById(R.id.editTextEmailAddress_login);
        editTextLoginPassword     = findViewById(R.id.editTextPassword_login);
        buttonLogin               = findViewById(R.id.buttonLogin_login);
        textViewMessage           = findViewById(R.id.textViewMessage_login);
        textViewCreateNewAccount  = findViewById(R.id.textViewCreateNewAccount_login);
        progressBarLogin          = findViewById(R.id.progressBar_login);

        // onClickListeners
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginEmailAddress.getText().toString().trim();
                String password = editTextLoginPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    editTextLoginEmailAddress.setError(getString(R.string.email_is_required));
                    editTextLoginEmailAddress.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextLoginPassword.setError(getString(R.string.password_required));
                    editTextLoginPassword.requestFocus();
                    return;
                }

                // Display progressbar to let the user know something is happening
                textViewMessage.setVisibility(View.INVISIBLE);
                textViewCreateNewAccount.setVisibility(View.INVISIBLE);
                buttonLogin.setVisibility(View.INVISIBLE);
                progressBarLogin.setVisibility(View.VISIBLE);

                Log.d(TAG, "User is attempting to log in ");
                // onCompleteListener to check if the user account was already created before.
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Log.d(TAG, " User signed in successfully");
                            Toast.makeText(MainActivity.this, getString(R.string.signed_in_successfully), Toast.LENGTH_LONG).show();
                            //redirect to user profile
                            // check if user is employee or not
                            user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            DocumentReference docRef = fStore.collection("Users").document(userId);
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User user = documentSnapshot.toObject(User.class);

                                    boolean isEmployee = user.isEmployee();
                                    Log.d(TAG, "User is employee: " + isEmployee);

                                    // We check if its the director
                                    Log.d(TAG, "user email:" + user.getEmailAddress());
                                    Intent intent;
                                    if(user.getEmailAddress().equals("tarekdeek@bearcare.ca")){
                                        //Add Intent for director
                                        Log.d(TAG, "user is a director");
                                        intent = new Intent(getApplicationContext(), DirectorDashboardActivity.class);
                                        Toast.makeText(MainActivity.this, getString(R.string.welcome_back_director), Toast.LENGTH_LONG).show();
                                    } else {
                                        intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                                        intent.putExtra("isEmployeeD", isEmployee);
                                        Log.d(TAG, "isEmployee is being sent to next activity");
                                    }
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Log.d(TAG, "User didn't sign in successfully");
                            Toast.makeText(MainActivity.this, getString(R.string.no_account_associated), Toast.LENGTH_LONG).show();
                            textViewMessage.setVisibility(View.VISIBLE);
                            textViewCreateNewAccount.setVisibility(View.VISIBLE);
                            buttonLogin.setVisibility(View.VISIBLE);
                            progressBarLogin.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
        textViewCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "User is going to create an account");
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class ));
            }

        });
        // end of setUpUI function
    }

}